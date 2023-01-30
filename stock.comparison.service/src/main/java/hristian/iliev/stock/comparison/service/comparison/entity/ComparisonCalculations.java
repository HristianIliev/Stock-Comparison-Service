package hristian.iliev.stock.comparison.service.comparison.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ComparisonCalculations {

  private double averageOfDifferences;
  private double correlationCoefficient;
  private double medianOfDifferences;
  private double modeOfDifferences;
  private double standardDeviationOfDifferences;
  private double zScoreOfDifferences;
  private String name;
  private double firstLastKnownPrice;
  private double secondLastKnownPrice;
  private double firstAvgPrice;
  private double secondAvgPrice;
  private double firstAvgDividend;
  private double secondAvgDividend;
  private long firstAvgVolume;
  private long secondAvgVolume;
  private String tagName;
  private String tagColor;

}
