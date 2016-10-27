package ${packageName}.${artifactId};

import org.springframework.boot.SpringApplication;<% if (useSpringBootApplication) { %>
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;<%} else { %>
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
<% } %>
<% if (useSpringBootApplication) { %>
@SpringBootApplication
@EnableCircuitBreaker
@EnableDiscoveryClient<% } else { %>
@Configuration
@ComponentScan
@EnableAutoConfiguration <% } %>
public class ${applicationName} {

    public static void main(String[] args) {
        SpringApplication.run(ProjectApplication.class, args);
    }

}
