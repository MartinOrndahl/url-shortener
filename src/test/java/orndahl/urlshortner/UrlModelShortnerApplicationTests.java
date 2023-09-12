package orndahl.urlshortner;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
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
class UrlModelShortnerApplicationTests {

  @Container
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer("postgres:15.0")
          .withDatabaseName("postgres")
          .withUsername("username")
          .withPassword("password");

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.liquibase.change-log", () -> "classpath:db/changelog/changelog.yaml");
  }

  TestRestTemplate restTemplate = new TestRestTemplate();

  @Test
  void test() {
    HttpEntity<String> entity = new HttpEntity<>(null, new HttpHeaders());

    String originalUrl = "google.com";

    ResponseEntity<String> createResponse =
        restTemplate.exchange(
            "http://localhost:8080/create?url=%s".formatted(originalUrl),
            HttpMethod.POST,
            entity,
            String.class);

    String createResponseString = createResponse.getBody();

    ResponseEntity<String> lookupResponse =
        restTemplate.exchange(
            "http://localhost:8080/lookup?url=%s".formatted(createResponseString),
            HttpMethod.GET,
            entity,
            String.class);

    assert originalUrl.equals(lookupResponse.getBody());
  }
}
