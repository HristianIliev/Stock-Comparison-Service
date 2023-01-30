package hristian.iliev.stock.comparison.service.stocks;

import hristian.iliev.stock.comparison.service.comparison.entity.Comparison;
import hristian.iliev.stock.comparison.service.comparison.entity.ComparisonCalculations;
import hristian.iliev.stock.comparison.service.comparison.entity.DiagramData;

import java.io.IOException;

public interface StockQuoteService {

  public void updateStockQuotes() throws IOException, InterruptedException;

  public ComparisonCalculations calculateComparisonData(Comparison comparison, int periods);

  public boolean quotesForStockWithNameDoNotExist(String stockName);

  public DiagramData calculateComparisonDiagramData(Comparison comparison, int periods);

}
