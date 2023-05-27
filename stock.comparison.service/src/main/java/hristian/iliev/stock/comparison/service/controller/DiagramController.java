package hristian.iliev.stock.comparison.service.controller;

import hristian.iliev.stock.comparison.service.comparison.entity.Comparison;
import hristian.iliev.stock.comparison.service.comparison.entity.DiagramData;
import hristian.iliev.stock.comparison.service.stocks.StockQuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DiagramController {

  @Autowired
  private StockQuoteService quoteService;

  @GetMapping("/api/diagrams")
  @ResponseBody
  public DiagramData generateDiagram(@RequestParam("firstStockName") String firstStockName, @RequestParam("secondStockName") String secondStockName, @RequestParam(value = "periods", required = false, defaultValue = "200") int periods) {
    Comparison comparison = new Comparison();
    comparison.setFirstStockName(firstStockName);
    comparison.setSecondStockName(secondStockName);

    return quoteService.calculateComparisonDiagramData(comparison, periods);
  }

}
