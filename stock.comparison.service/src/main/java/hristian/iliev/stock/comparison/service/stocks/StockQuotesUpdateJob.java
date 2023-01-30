package hristian.iliev.stock.comparison.service.stocks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StockQuotesUpdateJob {

  @Autowired
  private StockQuoteService quoteService;

  @Scheduled(fixedRate=60*1000)
  public void updateStocks() throws IOException, InterruptedException {
    quoteService.updateStockQuotes();
  }

}
