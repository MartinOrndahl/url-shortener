package orndahl.urlshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class ServiceIT {

  public static void main(String[] args) {
    SpringApplication.from(Application::main).with(ServiceIT.class).run(args);
  }
}
