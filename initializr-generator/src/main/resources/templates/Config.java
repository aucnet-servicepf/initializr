package ${packageName}.config;


import org.dozer.spring.DozerBeanMapperFactoryBean;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.web.client.RestTemplate;


@Configuration
public class Config {

    @Bean(name="restTemplate")
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    DozerBeanMapperFactoryBean getDozerBeanMapperFactoryBean(){
        return new DozerBeanMapperFactoryBean();
    }


    @EnableOAuth2Sso
    @Configuration
    @Profile("cloud")
    public static class SSoOnCloud {

        @Bean
        @LoadBalanced
        @Primary
        public OAuth2RestTemplate oAuth2RestTemplate(OAuth2ProtectedResourceDetails resource,
                        OAuth2ClientContext oauth2Context) {

            OAuth2RestTemplate template = new OAuth2RestTemplate(resource, oauth2Context);

            return template;
        }
    }

}
