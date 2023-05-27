package hristian.iliev.stock.comparison.service.comparison.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
public class Note {

  @Id
  @GeneratedValue(strategy= GenerationType.AUTO)
  @JsonIgnore
  private Long id;

  @ManyToOne
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private Comparison comparison;

  private String text;

  private String title;

  private LocalDate createdAt;

}
