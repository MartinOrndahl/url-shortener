package orndahl.urlshortener;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "url")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UrlModel {

  @Id String shortened;

  String original;
}
