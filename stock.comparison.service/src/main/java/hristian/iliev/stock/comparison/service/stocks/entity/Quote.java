package hristian.iliev.stock.comparison.service.stocks.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Quote {

  @Id
  @GeneratedValue(strategy= GenerationType.AUTO)
  private Long id;

  private LocalDate time;

  private double open;

  private double high;

  private double low;

  private double close;

  private String stockName;

  private double dividend;

  private int volume;

  public Quote() {
  }

  public Quote(String stockName, LocalDate time, double open, double high, double low, double close, double dividend, int volume) {
    this.stockName = stockName;
    this.time = time;
    this.open = open;
    this.high = high;
    this.low = low;
    this.close = close;
    this.dividend = dividend;
    this.volume = volume;
  }

  public String getStockName() {
    return stockName;
  }

  public LocalDate getTime() {
    return time;
  }

  @Override
  public String toString() {
    return "Quote{" +
        "id=" + id +
        ", time=" + time +
        ", open=" + open +
        ", high=" + high +
        ", low=" + low +
        ", close=" + close +
        ", stockName='" + stockName + '\'' +
        '}';
  }

  public boolean isBefore(Quote another) {
    return time.isBefore(another.getTime());
  }

}
