package io.spring.graphql;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class GraphQlRequestConverterTest {

  @Autowired private MockMvc mockMvc;

  @Test
  public void should_read_graphql_request_without_unwrapping_root_value() throws Exception {
    mockMvc
        .perform(
            post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"query\": \"{ tags }\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.tags").exists());
  }

  @Test
  public void should_keep_unwrapping_root_value_for_rest_requests() throws Exception {
    String username = "converter" + System.nanoTime();
    mockMvc
        .perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    String.format(
                        "{\"user\":{\"email\":\"%s@test.com\",\"username\":\"%s\",\"password\":\"pass1234\"}}",
                        username, username)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.user.username").value(username));
  }
}
