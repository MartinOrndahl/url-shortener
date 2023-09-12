package orndahl.urlshortener;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DatabaseRepository extends JpaRepository<UrlModel, Long> {
  Optional<UrlModel> findByShortened(String shortened);
}
