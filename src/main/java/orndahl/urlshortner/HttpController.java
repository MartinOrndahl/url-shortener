package orndahl.urlshortner;

import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RestController
public class HttpController {

  private static final Charset charset = StandardCharsets.UTF_8;

  private final UrlRepository urlRepository;
  private final MessageDigest md;

  @SneakyThrows
  public HttpController(UrlRepository urlRepository) {
    this.urlRepository = urlRepository;
    this.md = MessageDigest.getInstance("SHA-256");
  }

  @RequestMapping(method = RequestMethod.POST, path = "/create")
  public String createUrl(final String url) {
    String shortenedUrl = new String(md.digest(url.getBytes(charset)), charset);
    Url urlModel = Url.builder().original(url).shortened(shortenedUrl).build();

    urlRepository.save(urlModel);

    return shortenedUrl;
  }

  @RequestMapping(method = RequestMethod.POST, path = "/lookup")
  public String lookupUrl(final String shortUrl) {
    String original = urlRepository.findByShortened(shortUrl).original;
    return "redirect:%s".formatted(original);
  }
}
