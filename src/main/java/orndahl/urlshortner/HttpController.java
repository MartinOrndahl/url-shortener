package orndahl.urlshortner;

import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HttpController {

  private final UrlRepository urlRepository;

  @SneakyThrows
  public HttpController(UrlRepository urlRepository) {
    this.urlRepository = urlRepository;
  }

  @RequestMapping(method = RequestMethod.POST, path = "/create")
  public String createUrl(@RequestParam final String url) {
    String shortenedUrl = DigestUtils.sha256Hex(url);
    Url urlModel = Url.builder().original(url).shortened(shortenedUrl).build();
    urlRepository.save(urlModel);

    return shortenedUrl;
  }

  @RequestMapping(method = RequestMethod.GET, path = "/lookup")
  public String lookupUrl(@RequestParam final String url) {
    return urlRepository.findByShortened(url).original;
  }
}
