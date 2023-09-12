package orndahl.urlshortener;

import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HttpController {

  private final DatabaseRepository databaseRepository;

  @SneakyThrows
  public HttpController(DatabaseRepository databaseRepository) {
    this.databaseRepository = databaseRepository;
  }

  @RequestMapping(method = RequestMethod.POST, path = "/create")
  public String createUrl(@RequestParam final String url) {
    String shortenedUrl = DigestUtils.sha256Hex(url);
    UrlModel urlModel = UrlModel.builder().original(url).shortened(shortenedUrl).build();
    databaseRepository.save(urlModel);

    return shortenedUrl;
  }

  @RequestMapping(method = RequestMethod.GET, path = "/lookup")
  public String lookupUrl(@RequestParam final String url) {
    return databaseRepository.findByShortened(url).original;
  }
}
