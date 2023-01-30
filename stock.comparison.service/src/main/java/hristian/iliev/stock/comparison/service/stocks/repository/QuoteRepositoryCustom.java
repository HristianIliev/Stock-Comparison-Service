package hristian.iliev.stock.comparison.service.stocks.repository;

import hristian.iliev.stock.comparison.service.stocks.entity.Quote;

import java.util.List;

public interface QuoteRepositoryCustom {

  public List<Quote> findAllByStockName(String stockName);

}
