package io.spring.graphql;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.server.WebGraphQlHandler;
import org.springframework.graphql.server.webmvc.GraphQlHttpHandler;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import tools.jackson.databind.json.JsonMapper;

/**
 * The REST API unwraps root values (for example {@code {"user": {...}}}), which is enabled globally
 * for the application object mapper. GraphQL payloads are not wrapped, so the GraphQL endpoint gets
 * its own converter using a plain mapper.
 */
@Configuration
public class GraphQlHttpConfiguration {

  @Bean
  public GraphQlHttpHandler graphQlHttpHandler(WebGraphQlHandler webGraphQlHandler) {
    return new GraphQlHttpHandler(
        webGraphQlHandler, new JacksonJsonHttpMessageConverter(JsonMapper.builder().build()));
  }
}
