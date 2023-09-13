package orndahl.urlshortener;

import java.util.Map;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(
    classes = Application.class,
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ApplicationTests {

  public static final String DB_USERNAME = "username";
  public static final String DB_PASSWORD = "password";

  @Container
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer("postgres:15.0")
          .withDatabaseName("postgres")
          .withUsername(DB_USERNAME)
          .withPassword(DB_PASSWORD);

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.liquibase.change-log", () -> "classpath:db/changelog/changelog.yaml");
  }

  TestRestTemplate restTemplate = new TestRestTemplate();

  @SneakyThrows
  @Test
  @DisplayName("Ensure a URL is shortened, stored, and retrieved")
  void serviceTest() {
    HttpEntity<String> entity = new HttpEntity<>(null, new HttpHeaders());

    String originalUrl = "https://www.google.com/";

    ResponseEntity<Map<String, String>> createResponse =
        restTemplate.exchange(
            "http://localhost:8080/links?url=%s".formatted(originalUrl),
            HttpMethod.POST,
            entity,
            new ParameterizedTypeReference<>() {});

    Map<String, String> bodyMap = createResponse.getBody();
    String id = bodyMap.get("linkId");

    ResponseEntity<?> lookupResponse =
        restTemplate.exchange(
            "http://localhost:8080/links/%s".formatted(id), HttpMethod.GET, entity, Object.class);

    assert originalUrl.equalsIgnoreCase(String.valueOf(lookupResponse.getHeaders().getLocation()));
  }
}
