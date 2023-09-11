package orndahl.urlshortner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends JpaRepository<UrlModel, Long> {
  UrlModel findByShortened(String shortened);
}
