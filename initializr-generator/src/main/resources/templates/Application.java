package ${packageName};

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;<% if (useSpringBootApplication) { %>
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.java.ServiceScan;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;<%} else { %>
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
<% } %>
<% if (useSpringBootApplication) { %>
@SpringBootApplication
@EnableCircuitBreaker
@EnableDiscoveryClient
@ServiceScan
@EnableOAuth2Sso<% } else { %>
@Configuration
@ComponentScan
@EnableAutoConfiguration <% } %>
public class ${applicationName} {

    private static final Logger logger = LoggerFactory.getLogger(${applicationName}.class);

    public static void main(String[] args) {
        SpringApplication.run(${applicationName}.class, args);
    }

}
