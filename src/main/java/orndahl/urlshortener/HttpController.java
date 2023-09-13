package orndahl.urlshortener;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PathVariable;
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

  @Async
  @RequestMapping(
      method = RequestMethod.POST,
      path = "/links",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public CompletableFuture<ResponseEntity<?>> createUrl(@RequestParam final String url) {
    String shortenedUrl = DigestUtils.sha1Hex(url);
    UrlModel urlModel = UrlModel.builder().original(url).shortened(shortenedUrl).build();

    try {
      databaseRepository.save(urlModel);
    } catch (Exception e) {
      return CompletableFuture.completedFuture(
          new ResponseEntity<>(
              Map.of("errorMessage", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    return CompletableFuture.completedFuture(
        new ResponseEntity<>(Map.of("linkId", shortenedUrl), HttpStatus.OK));
  }

  @Async
  @RequestMapping(method = RequestMethod.GET, path = "/links/{id}")
  public CompletableFuture<ResponseEntity<Map<String, String>>> lookupUrl(
      @PathVariable("id") final String id) {

    final Optional<UrlModel> urlModel;
    try {
      urlModel = databaseRepository.findByShortened(id);
    } catch (Exception e) {
      return CompletableFuture.completedFuture(
          new ResponseEntity<>(
              Map.of("errorMessage", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    if (urlModel.isEmpty()) {
      return CompletableFuture.completedFuture(
          new ResponseEntity<>(
              Map.of("errorMessage", "%s could not be found".formatted(id)), HttpStatus.NOT_FOUND));
    }

    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(URI.create(urlModel.get().original));

    return CompletableFuture.completedFuture(
        new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY));
  }
}
