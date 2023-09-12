package orndahl.urlshortener;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

  // GET is an odd choice here but allows for using a browser
  @RequestMapping(method = RequestMethod.GET, path = "/create")
  public ResponseEntity<?> createUrl(@RequestParam final String url) {
    String shortenedUrl = DigestUtils.sha1Hex(url);
    UrlModel urlModel = UrlModel.builder().original(url).shortened(shortenedUrl).build();

    try {
      databaseRepository.save(urlModel);
    } catch (Exception e) {
      return new ResponseEntity<>(
          Map.of("errorMessage", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new ResponseEntity<>(Map.of("shortenedUrl", shortenedUrl), headers, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.GET, path = "/lookup")
  public ResponseEntity<?> lookupUrl(@RequestParam final String url) {

    final Optional<UrlModel> urlModel;
    try {
      urlModel = databaseRepository.findByShortened(url);
    } catch (Exception e) {
      return new ResponseEntity<>(
          Map.of("errorMessage", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    if (urlModel.isEmpty()) {
      return new ResponseEntity<>(
          Map.of("errorMessage", "%s could not be found".formatted(url)), HttpStatus.NOT_FOUND);
    }

    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(URI.create(urlModel.get().original));
    return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
  }
}
