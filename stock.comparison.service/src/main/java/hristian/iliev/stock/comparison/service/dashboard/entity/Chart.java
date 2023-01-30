package hristian.iliev.stock.comparison.service.dashboard.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Chart {

  @Id
  @GeneratedValue(strategy= GenerationType.AUTO)
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private Long id;

  private int periods;

  @ManyToOne
  @JsonIgnore
  private Dashboard dashboard;

  private String firstStockName;
  private String secondStockName;

  private String type;

}
