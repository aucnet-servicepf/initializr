/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.generator

import groovy.util.logging.Slf4j
import io.spring.initializr.InitializrException
import io.spring.initializr.metadata.Dependency
import io.spring.initializr.metadata.InitializrMetadataProvider
import io.spring.initializr.util.GroovyTemplate
import io.spring.initializr.util.Version

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.util.Assert

import static io.spring.initializr.metadata.InitializrConfiguration.Env.Maven.ParentPom

/**
 * Generate a project based on the configured metadata.
 *
 * @author Dave Syer
 * @author Stephane Nicoll
 * @author Sebastien Deleuze
 * @since 1.0
 */
@Slf4j
class ProjectGenerator {

	private static final VERSION_1_2_0_RC1 = Version.parse('1.2.0.RC1')

	private static final VERSION_1_3_0_M1 = Version.parse('1.3.0.M1')

	private static final VERSION_1_4_0_M2 = Version.parse('1.4.0.M2')

	private static final VERSION_1_4_0_M3 = Version.parse('1.4.0.M3')

	private static final VERSION_1_4_2_M1 = Version.parse('1.4.2.M1')

	@Autowired
	ApplicationEventPublisher eventPublisher

	@Autowired
	InitializrMetadataProvider metadataProvider

	@Autowired
	ProjectRequestResolver requestResolver

	@Autowired
	GroovyTemplate groovyTemplate = new GroovyTemplate()

	@Autowired
	ProjectResourceLocator projectResourceLocator = new ProjectResourceLocator()

	@Value('${TMPDIR:.}')
	String tmpdir

	private transient Map<String, List<File>> temporaryFiles = [:]

	/**
	 * Generate a Maven pom for the specified {@link ProjectRequest}.
	 */
	byte[] generateMavenPom(ProjectRequest request) {
		try {
			def model = resolveModel(request)
			if (!isMavenBuild(request)) {
				throw new InvalidProjectRequestException("Could not generate Maven pom, " +
						"invalid project type $request.type")
			}
			def content = doGenerateMavenPom(model)
			publishProjectGeneratedEvent(request)
			content
		} catch (InitializrException ex) {
			publishProjectFailedEvent(request, ex)
			throw ex
		}
	}

	/**
	 * Generate a Gradle build file for the specified {@link ProjectRequest}.
	 */
	byte[] generateGradleBuild(ProjectRequest request) {
		try {
			def model = resolveModel(request)
			if (!isGradleBuild(request)) {
				throw new InvalidProjectRequestException("Could not generate Gradle build, " +
						"invalid project type $request.type")
			}
			def content = doGenerateGradleBuild(model)
			publishProjectGeneratedEvent(request)
			content
		} catch (InitializrException ex) {
			publishProjectFailedEvent(request, ex)
			throw ex
		}
	}

	/**
	 * Generate a project structure for the specified {@link ProjectRequest}. Returns
	 * a directory containing the project.
	 */
	File generateProjectStructure(ProjectRequest request) {
		try {
			doGenerateProjectStructure(request)
		} catch (InitializrException ex) {
			publishProjectFailedEvent(request, ex)
			throw ex
		}
	}

	protected File doGenerateProjectStructure(ProjectRequest request) {
		def model = resolveModel(request)

		def rootDir = File.createTempFile('tmp', '', new File(tmpdir))
		addTempFile(rootDir.name, rootDir)
		rootDir.delete()
		rootDir.mkdirs()


        if (request.hasFacet('mybatis')) {
            request.buildProperties.maven['mybatis.generator.overwrite']={"true"}
            request.buildProperties.maven['mybatis.generator.configurationFile']={'mybatis-generator-config.xml'}

         }


		def dir = initializerProjectDir(rootDir, request)

		if (isGradleBuild(request)) {
			def gradle = new String(doGenerateGradleBuild(model))
			new File(dir, 'build.gradle').write(gradle)
			writeGradleWrapper(dir)
		} else {
			def pom = new String(doGenerateMavenPom(model))
			new File(dir, 'pom.xml').write(pom)
			writeMavenWrapper(dir)
		}

		generateGitIgnore(dir, request)

        def packageWithPjName = request.packageName.replace('.', '/')

		def applicationName = request.applicationName
		def language = request.language

		String codeLocation = language
		def src = new File(new File(dir, "src/main/$codeLocation"), packageWithPjName)
		src.mkdirs()
		def extension = (language.equals('kotlin') ? 'kt' : language)
		write(new File(src, "${applicationName}.${extension}"), "Application.$extension", model)

		if (request.packaging == 'war') {
			def fileName = "ServletInitializer.$extension"
			write(new File(src, fileName), fileName, model)
		}

		def test = new File(new File(dir, "src/test/$codeLocation"), packageWithPjName)
		test.mkdirs()
		setupTestModel(request, model)
		write(new File(test, "${applicationName}Tests.${extension}"), "ApplicationTests.$extension", model)

		def resources = new File(dir, 'src/main/resources')
		resources.mkdirs()
		//new File(resources, 'application.properties').write('')

        write(new File(dir, 'src/main/resources/application.properties'), 'application.properties', model);

		//MOD
		write(new File(dir, 'src/main/resources/bootstrap.properties'), 'bootstrap.properties', model);


        //sample data
        new File(dir, 'src/main/resources/db/migration/').mkdirs()
        writeBinaryResource(new File(dir, 'src/main/resources/db/migration/'), 'V1__Create_Table_With_Seed.sql' , 'V1__Create_Table_With_Seed.sql')


		new File(dir, 'src/main/java/' + packageWithPjName + "/business/domain/").mkdirs()
		new File(dir, 'src/main/java/' + packageWithPjName + "/api/v1/").mkdirs()
		write(new File(dir, 'src/main/java/' + packageWithPjName + "/business/domain/Product.java"), 'Product.java', model);
        write(new File(dir, 'src/main/java/' + packageWithPjName + "/business/domain/ProductKey.java"), 'ProductKey.java', model);
		write(new File(dir, 'src/main/java/'+ packageWithPjName + "/api/v1/ProductRestControllerV1.java"), 'ProductRestControllerV1.java', model);
        write(new File(dir, 'src/main/java/'+ packageWithPjName + "/api/v1/ProductServiceV1.java"), 'ProductServiceV1.java', model);
        write(new File(dir, 'src/main/java/'+ packageWithPjName + "/api/v1/ProductResource.java"), 'ProductResource.java', model);


        //
        new File(dir, 'src/main/java/' + packageWithPjName + "/config").mkdirs()
        write(new File(dir, 'src/main/java/'+ packageWithPjName + "/config/Config.java"), 'Config.java', model);

        //TODO 一時的用な物 self-signSSL問題対応
       // new File(dir, 'src/main/java/org/apache/http/conn/ssl/').mkdirs()
       // writeTextResource(new File(dir, 'src/main/java/org/apache/http/conn/ssl/'), 'AbstractVerifier.java' , 'AbstractVerifier.java')

       // write(new File(dir, 'src/main/java/'+packageWithPjName+"/SSLValidationDisabler.java"), 'SSLValidationDisabler.java', model);

        new File(dir, 'src/main/java/org/springframework/security/concurrent/').mkdirs()
        write(new File(dir, 'src/main/java/org/springframework/security/concurrent/DelegatingSecurityContextCallable.java'), 'DelegatingSecurityContextCallable.java', model);


        //repository複雑化のもの
		new File(dir, 'src/main/java/' + packageWithPjName + "/integration/repository").mkdirs()

		new File(dir, 'src/main/java/' + packageWithPjName + "/business/repository/").mkdirs()
		new File(dir, 'src/main/java/' + packageWithPjName + "/business/service/").mkdirs()

        new File(dir, 'src/test/resources/' + packageWithPjName + "/business/service/").mkdirs()
        new File(dir, 'src/test/java/' + packageWithPjName + "/business/service/").mkdirs()


		write(new File(dir, 'src/main/java/'+ packageWithPjName + "/business/service/ProductService.java"), 'ProductService.java', model);
		write(new File(dir, 'src/main/java/'+ packageWithPjName + "/business/service/ProductServiceImpl.java"), 'ProductServiceImpl.java', model);



        write(new File(dir, 'src/test/java/'+ packageWithPjName + "/business/service/ProductServiceTests.java"), 'ProductServiceTests.java', model);


        //Inegration test holder
        new File(dir, 'src/test/java/' + packageWithPjName + "/integrationtest/").mkdirs()



        writeBinaryResource(new File(dir, 'src/test/resources/'+ packageWithPjName + "/business/service/"), 'products.xlsx' , 'products.xlsx')


        //check style
        write(new File(dir, '.checkstyle'), 'checkstyle.tmpl', model)


        //CI用
        new File(dir, 'ci/scripts').mkdirs()
        write(new File(dir, "ci/unit-test.yml"), 'unit-test.yml', model);
        write(new File(dir, "ci/integration-test.yml"), 'integration-test.yml', model);
        write(new File(dir, "ci/merge-branch.yml"), 'merge-branch.yml', model);
        write(new File(dir, "ci/qa-report.yml"), 'qa-report.yml', model);
        write(new File(dir, "ci/package.yml"), 'package.yml', model);

        write(new File(dir, "ci/scripts/unit-test.sh"), 'unit-test.sh', model);
        write(new File(dir, "ci/scripts/integration-test.sh"), 'integration-test.sh', model);
        write(new File(dir, "ci/scripts/merge-branch.sh"), 'merge-branch.sh', model);
        write(new File(dir, "ci/scripts/package.sh"), 'package.sh', model);
        write(new File(dir, "ci/scripts/qa-report.sh"), 'qa-report.sh', model);


        write(new File(dir, "manifest.yml"), 'manifest.yml', model);


        //TODO 共通LIBに移動する予定
        new File(dir, 'src/test/java/jp/co/aucnet/test/util/').mkdirs()
//        write(new File(dir, 'src/test/java/jp/co/aucnet/test/util/YamlDataSetLoader.java'), 'YamlDataSetLoader.java', model);
//        write(new File(dir, 'src/test/java/jp/co/aucnet/test/util/YamlDataSet.java'), 'YamlDataSet.java', model);

        writeBinaryResource(new File(dir, 'src/test/java/jp/co/aucnet/test/util/'), 'ExcelDataSetLoader.java' , 'ExcelDataSetLoader.java')

        writeBinaryResource(new File(dir, 'src/test/java/jp/co/aucnet/test/util/'), 'XlsxDataSet.java' , 'XlsxDataSet.java')
        writeBinaryResource(new File(dir, 'src/test/java/jp/co/aucnet/test/util/'), 'XlsxDataSetWriter.java' , 'XlsxDataSetWriter.java')



		if (request.hasFacet('mybatis')) {

			write(new File(dir, 'src/main/java/'+ packageWithPjName + "/integration/repository/ProductMapper.java"), 'ProductMapper.java', model);

            write(new File(dir, 'src/main/java/'+ packageWithPjName + "/integration/repository/ProductMapper.xml"), 'ProductMapper.xml', model);


            write(new File(dir, 'src/main/java/'+ packageWithPjName + "/business/repository/ProductRepository.java"), 'ProductRepository.java', model);

            write(new File(dir, 'src/main/java/'+ packageWithPjName + "/integration/repository/ProductRepositoryImpl.java"), 'ProductRepositoryImpl.java', model);



            write(new File(dir, 'src/main/resources/mybatis-config.xml'), 'mybatis-config.xml', model);

            write(new File(dir, 'mybatis-generator-config.xml'), 'mybatis-generator-config.xml', model);

            //write(new File(dir, 'src/main/java/'+ packageWithPjName + "/business/domain/AbstractBaseEntity.java"), 'AbstractBaseEntity.java', model);


            write(new File(dir, 'src/main/java/' + packageWithPjName + "/business/domain/ProductExample.java"), 'ProductExample.java', model);


         }


		writeTextResource(dir, 'checkstyle-aucnet.xml' , 'checkstyle-aucnet.xml')
		writeTextResource(dir, 'formatter-aucnet.xml' , 'formatter-aucnet.xml')



		if (request.hasWebFacet()) {
			new File(dir, 'src/main/resources/templates').mkdirs()
			new File(dir, 'src/main/resources/static').mkdirs()
		}
		publishProjectGeneratedEvent(request)
		rootDir

	}

	/**
	 * Create a distribution file for the specified project structure
	 * directory and extension
	 */
	File createDistributionFile(File dir, String extension) {
		def download = new File(tmpdir, dir.name + extension)
		addTempFile(dir.name, download)
		download
	}

	/**
	 * Clean all the temporary files that are related to this root
	 * directory.
	 * @see #createDistributionFile
	 */
	void cleanTempFiles(File dir) {
		def tempFiles = temporaryFiles.remove(dir.name)
		if (tempFiles) {
			tempFiles.each { File file ->
				if (file.directory) {
					file.deleteDir()
				} else {
					file.delete()
				}
			}
		}
	}

	private void publishProjectGeneratedEvent(ProjectRequest request) {
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request)
		eventPublisher.publishEvent(event)
	}

	private void publishProjectFailedEvent(ProjectRequest request, Exception cause) {
		ProjectFailedEvent event = new ProjectFailedEvent(request, cause)
		eventPublisher.publishEvent(event)
	}

	/**
	 * Generate a {@code .gitignore} file for the specified {@link ProjectRequest}
	 * @param dir the root directory of the project
	 * @param request the request to handle
	 */
	protected void generateGitIgnore(File dir, ProjectRequest request) {
		def model = [:]
		model['build'] = isGradleBuild(request) ? 'gradle' : 'maven'
		write(new File(dir, '.gitignore'), 'gitignore.tmpl', model)
	}

	/**
	 * Resolve the specified {@link ProjectRequest} and return the model to use
	 * to generate the project
	 * @param request the request to handle
	 * @return a model for that request
	 */
	protected Map resolveModel(ProjectRequest originalRequest) {
		Assert.notNull originalRequest.bootVersion, 'boot version must not be null'
		def model = [:]
		def metadata = metadataProvider.get()

		ProjectRequest request = requestResolver.resolve(originalRequest, metadata)

		// request resolved so we can log what has been requested
		def dependencies = request.resolvedDependencies
		def dependencyIds = dependencies.collect { it.id }
		log.info("Processing request{type=$request.type, dependencies=$dependencyIds}")

		if (isMavenBuild(request)) {
			ParentPom parentPom = metadata.configuration.env.maven.resolveParentPom(request.bootVersion)
			if (parentPom.includeSpringBootBom && !request.boms['spring-boot']) {
				request.boms['spring-boot'] = metadata.createSpringBootBom(
						request.bootVersion, 'spring-boot.version')
			}

			model['mavenParentGroupId'] = parentPom.groupId
			model['mavenParentArtifactId'] = parentPom.artifactId
			model['mavenParentVersion'] = parentPom.version
			model['includeSpringBootBom'] = parentPom.includeSpringBootBom
		}

		model['compileDependencies'] = filterDependencies(dependencies, Dependency.SCOPE_COMPILE)
		model['runtimeDependencies'] = filterDependencies(dependencies, Dependency.SCOPE_RUNTIME)
		model['compileOnlyDependencies'] = filterDependencies(dependencies, Dependency.SCOPE_COMPILE_ONLY)
		model['providedDependencies'] = filterDependencies(dependencies, Dependency.SCOPE_PROVIDED)
		model['testDependencies'] = filterDependencies(dependencies, Dependency.SCOPE_TEST)

		request.boms.each { k, v ->
			if (v.versionProperty) {
				request.buildProperties.versions[v.versionProperty] = { v.version }
			}
		}

		// Add various versions
		model['dependencyManagementPluginVersion'] = metadata.configuration.env.gradle.dependencyManagementPluginVersion
		model['kotlinVersion'] = metadata.configuration.env.kotlin.version

		// @SpringBootApplication available as from 1.2.0.RC1
		model['useSpringBootApplication'] = VERSION_1_2_0_RC1
				.compareTo(Version.safeParse(request.bootVersion)) <= 0

		// Gradle plugin has changed as from 1.3.0
		model['bootOneThreeAvailable'] = VERSION_1_3_0_M1
				.compareTo(Version.safeParse(request.bootVersion)) <= 0

		// Gradle plugin has changed again as from 1.4.2
		model['springBootPluginName'] = (VERSION_1_4_2_M1.compareTo(
				Version.safeParse(request.bootVersion)) <= 0 ? 'org.springframework.boot' : 'spring-boot')

		// New testing stuff
		model['newTestInfrastructure'] = isNewTestInfrastructureAvailable(request)

		// New Servlet Initializer location
		model['newServletInitializer']  = isNewServletInitializerAvailable(request)

		// Append the project request to the model
		request.properties.each { model[it.key] = it.value }



		//MOD
		model['hasMybatis'] = request.hasFacet('mybatis')

		model
	}

	protected void setupTestModel(ProjectRequest request, Map<String, Object> model) {
		String imports = ''
		String testAnnotations = ''
		def newTestInfrastructure = isNewTestInfrastructureAvailable(request)
		if (newTestInfrastructure) {
			imports += String.format(generateImport('org.springframework.boot.test.context.SpringBootTest',
					request.language) + "%n")
			imports += String.format(generateImport('org.springframework.test.context.junit4.SpringRunner',
					request.language) + "%n")
		} else {
			imports += String.format(generateImport('org.springframework.boot.test.SpringApplicationConfiguration',
					request.language) + "%n")
			imports += String.format(generateImport('org.springframework.test.context.junit4.SpringJUnit4ClassRunner',
					request.language) + "%n")
		}
		if (request.hasWebFacet() && !newTestInfrastructure) {
			imports += String.format(generateImport('org.springframework.test.context.web.WebAppConfiguration',
					request.language) + "%n")
			testAnnotations = String.format('@WebAppConfiguration%n')
		}
		model.testImports = imports
		model.testAnnotations = testAnnotations
	}

	protected String generateImport(String type, String language) {
		String end = (language.equals("groovy") || language.equals("kotlin")) ? '' : ';'
		"import $type$end"
	}

	private static isGradleBuild(ProjectRequest request) {
		return 'gradle'.equals(request.build)
	}

	private static isMavenBuild(ProjectRequest request) {
		return 'maven'.equals(request.build)
	}

	private static boolean isNewTestInfrastructureAvailable(ProjectRequest request) {
		VERSION_1_4_0_M2
				.compareTo(Version.safeParse(request.bootVersion)) <= 0
	}

	private static boolean isNewServletInitializerAvailable(ProjectRequest request) {
		VERSION_1_4_0_M3
				.compareTo(Version.safeParse(request.bootVersion)) <= 0
	}

	private byte[] doGenerateMavenPom(Map model) {
		groovyTemplate.process 'starter-pom.xml', model
	}


	private byte[] doGenerateGradleBuild(Map model) {
		groovyTemplate.process 'starter-build.gradle', model
	}

	private void writeGradleWrapper(File dir) {
		writeTextResource(dir, 'gradlew.bat', 'gradle/gradlew.bat')
		writeTextResource(dir, 'gradlew', 'gradle/gradlew')

		def wrapperDir = new File(dir, 'gradle/wrapper')
		wrapperDir.mkdirs()
		writeTextResource(wrapperDir, 'gradle-wrapper.properties',
				'gradle/gradle/wrapper/gradle-wrapper.properties')
		writeBinaryResource(wrapperDir, 'gradle-wrapper.jar',
				'gradle/gradle/wrapper/gradle-wrapper.jar')
	}

	private void writeMavenWrapper(File dir) {
		writeTextResource(dir, 'mvnw.cmd', 'maven/mvnw.cmd')
		writeTextResource(dir, 'mvnw', 'maven/mvnw')

		def wrapperDir = new File(dir, '.mvn/wrapper')
		wrapperDir.mkdirs()
		writeTextResource(wrapperDir, 'maven-wrapper.properties',
				'maven/wrapper/maven-wrapper.properties')
		writeBinaryResource(wrapperDir, 'maven-wrapper.jar',
				'maven/wrapper/maven-wrapper.jar')
	}

	private File writeBinaryResource(File dir, String name, String location) {
		doWriteProjectResource(dir, name, location, true)
	}

	private File writeTextResource(File dir, String name, String location) {
		doWriteProjectResource(dir, name, location, false)
	}

	private File doWriteProjectResource(File dir, String name, String location, boolean binary) {
		def target = new File(dir, name)
		if (binary) {
			target << projectResourceLocator.getBinaryResource("classpath:project/$location")
		}
		else {
			target.write(projectResourceLocator.getTextResource("classpath:project/$location"))
		}
		target
	}

	private File initializerProjectDir(File rootDir, ProjectRequest request) {
		if (request.baseDir) {
			File dir = new File(rootDir, request.baseDir)
			dir.mkdirs()
			return dir
		} else {
			return rootDir
		}
	}

	def write(File target, String templateName, def model) {
		def tmpl = templateName.endsWith('.groovy') ? templateName + '.tmpl' : templateName
		def body = groovyTemplate.process tmpl, model
		target.write(body, "utf-8")
	}

	private void addTempFile(String group, File file) {
		def content = temporaryFiles[group]
		if (!content) {
			content = []
			temporaryFiles[group] = content
		}
		content << file
	}

	private static def filterDependencies(def dependencies, String scope) {
		dependencies.findAll { dep -> scope.equals(dep.scope) }.sort { a, b -> a.id <=> b.id }
	}

}
