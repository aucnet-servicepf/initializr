package ${packageName};

import org.mybatis.spring.annotation.MapperScan;

import org.springframework.boot.SpringApplication;<% if (useSpringBootApplication) { %>
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import lombok.extern.slf4j.Slf4j;<%} else { %>
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import jp.co.aucnet.project.SSLValidationDisabler;
<% } %>
<% if (useSpringBootApplication) { %>
@SpringBootApplication
@EnableCircuitBreaker
@EnableDiscoveryClient
@MapperScan(basePackages = "${packageName}.integration.repository")
@Slf4j<% } else { %>
@Configuration
@ComponentScan
@EnableAutoConfiguration <% } %>
public class ${applicationName} {

    /**
     * SpringBoot start.
     *
     * @param args 起動パラメータ
     */
    public static void main(String[] args) {
        log.info("app start");

        SpringApplication.run(${applicationName}.class, args);
    }

}
