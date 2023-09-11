package orndahl.urlshortner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestUrlShortnerApplication {

  public static void main(String[] args) {
    SpringApplication.from(Application::main).with(TestUrlShortnerApplication.class).run(args);
  }
}
