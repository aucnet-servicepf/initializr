package ${packageName};

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;<% if (useSpringBootApplication) { %>
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;<%} else { %>
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import jp.co.aucnet.project.SSLValidationDisabler;
<% } %>
<% if (useSpringBootApplication) { %>
@SpringBootApplication
@EnableCircuitBreaker
@EnableDiscoveryClient
@MapperScan(basePackages="${packageName}.business.repository")<% } else { %>
@Configuration
@ComponentScan
@EnableAutoConfiguration <% } %>
public class ${applicationName} {

    private static final Logger logger = LoggerFactory.getLogger(${applicationName}.class);

    public static void main(String[] args) {
        logger.info("app start");

        SpringApplication.run(${applicationName}.class, args);
    }

}
