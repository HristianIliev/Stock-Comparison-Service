package hristian.iliev.stock.comparison.service.comparison.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataPoint {

  private String time;
  private Double value;
  private Integer firstVolume;
  private Integer secondVolume;
  private Double firstPrice;
  private Double secondPrice;

}
