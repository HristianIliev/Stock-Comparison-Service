package hristian.iliev.stock.comparison.service.comparison.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

  // to prevent infinity loop in json
  @JsonBackReference
  @ManyToOne
  @JoinColumn(name = "comparison_id")
  private Comparison comparison;

  private String text;

  private String title;

  private LocalDate createdAt;

}
