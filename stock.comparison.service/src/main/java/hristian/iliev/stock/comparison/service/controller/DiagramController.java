package hristian.iliev.stock.comparison.service.controller;

import hristian.iliev.stock.comparison.service.Application;
import hristian.iliev.stock.comparison.service.comparison.entity.Comparison;
import hristian.iliev.stock.comparison.service.comparison.entity.DiagramData;
import hristian.iliev.stock.comparison.service.dashboard.entity.Chart;
import hristian.iliev.stock.comparison.service.events.Event;
import hristian.iliev.stock.comparison.service.stocks.StockQuoteService;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@AllArgsConstructor
public class DiagramController {

  private RabbitTemplate rabbitTemplate;

  private StockQuoteService quoteService;

  @GetMapping("/api/diagrams")
  @ResponseBody
  public DiagramData generateDiagram(@RequestParam("firstStockName") String firstStockName, @RequestParam("secondStockName") String secondStockName, @RequestParam(value = "periods", required = false, defaultValue = "200") int periods) {
    Comparison comparison = new Comparison();
    comparison.setFirstStockName(firstStockName);
    comparison.setSecondStockName(secondStockName);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();

    Event event = new Event.Builder()
        .withAction(Event.EventAction.RETRIEVE)
        .withEntityClass(Chart.class.getName())
        .withUsername(username)
        .withMessage("User with " + username + " generated a diagram for stocks: " + firstStockName + ":" + secondStockName)
        .build();

    rabbitTemplate.convertAndSend(Application.topicExchangeName, "analytics.diagrams", event.toJson());

    return quoteService.calculateComparisonDiagramData(comparison, periods);
  }

}
