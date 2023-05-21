package hristian.iliev.stock.comparison.service.stocks;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@AllArgsConstructor
public class StockQuotesUpdateJob {

  private final StockQuoteService quoteService;

  @Scheduled(fixedRate=60*1000)
  public void updateStocks() throws IOException, InterruptedException {
    quoteService.updateStockQuotes();
  }

}
