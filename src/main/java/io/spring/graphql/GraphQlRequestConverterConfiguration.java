package io.spring.graphql;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ResolvableType;
import org.springframework.graphql.server.support.SerializableGraphQlRequest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverters;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tools.jackson.databind.json.JsonMapper;

/**
 * Spring GraphQL reads {@code /graphql} request bodies through the shared HTTP message converters,
 * so the {@code spring.jackson.deserialization.UNWRAP_ROOT_VALUE} setting that the root-wrapped
 * REST payloads rely on would make every GraphQL request fail to deserialize. GraphQL requests are
 * therefore read by a dedicated converter backed by a mapper that does not unwrap root values; all
 * other types keep using the shared converter.
 */
@Configuration
public class GraphQlRequestConverterConfiguration implements WebMvcConfigurer {

  @Override
  public void configureMessageConverters(HttpMessageConverters.ServerBuilder builder) {
    builder.configureMessageConvertersList(
        converters -> converters.add(0, new GraphQlRequestConverter()));
  }

  static class GraphQlRequestConverter extends JacksonJsonHttpMessageConverter {

    GraphQlRequestConverter() {
      super(JsonMapper.builder().build());
      setSupportedMediaTypes(
          List.of(
              MediaType.APPLICATION_JSON,
              new MediaType("application", "*+json"),
              MediaType.parseMediaType("application/graphql")));
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
      return isGraphQlRequest(clazz) && super.canRead(clazz, mediaType);
    }

    @Override
    public boolean canRead(ResolvableType type, MediaType mediaType) {
      return isGraphQlRequest(type.resolve()) && super.canRead(type, mediaType);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
      return false;
    }

    @Override
    public boolean canWrite(ResolvableType type, Class<?> clazz, MediaType mediaType) {
      return false;
    }

    private boolean isGraphQlRequest(Class<?> clazz) {
      return clazz != null && SerializableGraphQlRequest.class.isAssignableFrom(clazz);
    }
  }
}
