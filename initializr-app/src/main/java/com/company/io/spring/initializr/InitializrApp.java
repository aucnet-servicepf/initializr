package com.company.io.spring.initializr;

import java.util.concurrent.Executor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
/**
 */
@SpringBootApplication

public class InitializrApp {

    @EnableOAuth2Sso
    @Configuration
    @Profile("cloud")
    static class SSO {

    }

  public static void main(String[] args) {

      SSLValidationDisabler.disableSSLValidation();
      ApplicationContext ctx = SpringApplication.run(InitializrApp.class, args);
  }

  @Configuration
  @EnableAsync
  static class AsyncConfiguration extends AsyncConfigurerSupport {
    @Override
    public Executor getAsyncExecutor() {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(1);
      executor.setMaxPoolSize(5);
      executor.setThreadNamePrefix("initializr-");
      executor.initialize();
      return executor;
    }
  }
}
