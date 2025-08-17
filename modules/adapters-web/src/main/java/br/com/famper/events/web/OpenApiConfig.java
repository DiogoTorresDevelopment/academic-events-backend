package br.com.famper.events.web;

import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.models.GroupedOpenApi;

@Configuration
public class OpenApiConfig {
  @Bean
  GroupedOpenApi api() {
    return GroupedOpenApi.builder()
      .group("v1")
      .pathsToMatch("/api/v1/**")
      .build();
  }
}