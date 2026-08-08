package io.spring.graphql;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.server.WebGraphQlHandler;
import org.springframework.graphql.server.webmvc.GraphQlHttpHandler;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

/**
 * The REST API unwraps root values (for example {@code {"user": {...}}}), a setting that must not
 * be applied to GraphQL requests. The GraphQL endpoint therefore reads requests with its own
 * mapper.
 */
@Configuration
public class GraphQLHttpConfiguration {

  @Bean
  public GraphQlHttpHandler graphQlHttpHandler(WebGraphQlHandler webGraphQlHandler) {
    JsonMapper mapper =
        JsonMapper.builder().disable(DeserializationFeature.UNWRAP_ROOT_VALUE).build();
    return new GraphQlHttpHandler(webGraphQlHandler, new JacksonJsonHttpMessageConverter(mapper));
  }
}
