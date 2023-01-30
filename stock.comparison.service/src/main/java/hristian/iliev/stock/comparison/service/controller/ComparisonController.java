package hristian.iliev.stock.comparison.service.controller;

import hristian.iliev.stock.comparison.service.Application;
import hristian.iliev.stock.comparison.service.comparison.ComparisonService;
import hristian.iliev.stock.comparison.service.comparison.entity.Comparison;
import hristian.iliev.stock.comparison.service.comparison.entity.ComparisonCalculations;
import hristian.iliev.stock.comparison.service.comparison.entity.Tag;
import hristian.iliev.stock.comparison.service.events.Event;
import hristian.iliev.stock.comparison.service.stocks.StockQuoteService;
import hristian.iliev.stock.comparison.service.users.UsersService;
import hristian.iliev.stock.comparison.service.users.entity.User;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ComparisonController {

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired
  private UsersService usersService;

  @Autowired
  private ComparisonService comparisonService;

  @Autowired
  private StockQuoteService stockQuoteService;

  @GetMapping("/users/{username}/comparisons")
  public String userComparisons(@PathVariable("username") String username, @RequestParam(value = "periods", required = false, defaultValue = "200") int periods, Model model) {
    System.out.println("Retrieving comparisons for " + username + " for " + periods + " periods");
    User user = usersService.retrieveUserByUsername(username);

    if (user == null) {
      return "error";
    }

    List<Comparison> comparisons = comparisonService.retrieveComparisonsByUser(user.getId());

    List<ComparisonCalculations> comparisonCalculations = new ArrayList<>();
    for (Comparison comparison : comparisons) {
      comparisonCalculations.add(stockQuoteService.calculateComparisonData(comparison, periods));
    }

    model.addAttribute("comparisons", comparisonCalculations);

    List<Tag> tags = comparisonService.retrieveTagsOfUser(user.getId());

    model.addAttribute("tags", tags);
    model.addAttribute("dashboards", user.getDashboards());
    model.addAttribute("username", username);

    return "home";
  }

  @GetMapping("/api/users/{username}/comparisons/names")
  public ResponseEntity<List<Comparison>> userComparisonNames(@PathVariable String username) {
    User user = usersService.retrieveUserByUsername(username);

    if (user == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    List<Comparison> comparisons = comparisonService.retrieveComparisonsByUser(user.getId());

    Event event = new Event.Builder()
                           .withAction(Event.EventAction.RETRIEVE)
                           .withEntityClass(Comparison.class.getName())
                           .withUsername(username)
                           .withMessage("User with " + username + " retrieved all of his comparison names")
                           .build();

    rabbitTemplate.convertAndSend(Application.topicExchangeName, "analytics.comparisons", event.toJson());

    return ResponseEntity.ok(comparisons);
  }

  @PostMapping("/api/users/{username}/comparisons")
  public ResponseEntity addStockComparison(@PathVariable String username, @RequestParam("firstStock") String firstStockName, @RequestParam("secondStock") String secondStockName) {
    System.out.println("Adding a new comparison to user: " + username + " " + firstStockName + ":" + secondStockName);

    User user = usersService.retrieveUserByUsername(username);

    if (user == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    if (stockQuoteService.quotesForStockWithNameDoNotExist(firstStockName) || stockQuoteService.quotesForStockWithNameDoNotExist(secondStockName)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    List<Comparison> comparisons = comparisonService.retrieveComparisonsByUser(user.getId());
    for (Comparison comparison : comparisons) {
      if (comparison.getFirstStockName().equals(firstStockName) && comparison.getSecondStockName().equals(secondStockName)) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
      }
    }

    comparisonService.addComparisonToUser(user, firstStockName, secondStockName);

    Event event = new Event.Builder()
        .withAction(Event.EventAction.CREATE)
        .withEntityClass(Comparison.class.getName())
        .withUsername(username)
        .withMessage("User with " + username + " created a new comparison between stock with name " + firstStockName + " and another stock with name " + secondStockName)
        .build();

    rabbitTemplate.convertAndSend(Application.topicExchangeName, "analytics.comparisons", event.toJson());

    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/api/users/{username}/comparisons")
  public ResponseEntity deleteStockComparison(@PathVariable String username, @RequestParam("firstStock") String firstStockName, @RequestParam("secondStock") String secondStockName) {
    User user = usersService.retrieveUserByUsername(username);

    if (user == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    List<Comparison> comparisons = comparisonService.retrieveComparisonsByUser(user.getId());
    for (Comparison comparison : comparisons) {
      if (comparison.getFirstStockName().equals(firstStockName) && comparison.getSecondStockName().equals(secondStockName)) {
        comparisonService.deleteComparison(user, firstStockName, secondStockName);
      }
    }

    Event event = new Event.Builder()
        .withAction(Event.EventAction.DELETE)
        .withEntityClass(Comparison.class.getName())
        .withUsername(username)
        .withMessage("User with " + username + " deleted comparison between stock with name " + firstStockName + " and another stock with name " + secondStockName)
        .build();

    rabbitTemplate.convertAndSend(Application.topicExchangeName, "analytics.comparisons", event.toJson());

    return ResponseEntity.ok().build();
  }

  @PostMapping("/api/users/{username}/comparisons/tags")
  public ResponseEntity addTagForComparison(@PathVariable String username, @RequestParam("firstStock") String firstStockName, @RequestParam("secondStock") String secondStockName, @RequestBody Tag tag) {
    System.out.println(tag);

    User user = usersService.retrieveUserByUsername(username);

    if (user == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    List<Comparison> comparisons = comparisonService.retrieveComparisonsByUser(user.getId());
    for (Comparison comparison : comparisons) {
      if (comparison.getFirstStockName().equals(firstStockName) && comparison.getSecondStockName().equals(secondStockName)) {
        comparisonService.tagComparison(user.getId(), firstStockName, secondStockName, tag);

        break;
      }
    }

    Event event = new Event.Builder()
        .withAction(Event.EventAction.CREATE)
        .withEntityClass(Tag.class.getName())
        .withUsername(username)
        .withMessage("User with " + username + " added a new tag for comparison between stock with name " + firstStockName + " and another stock with name " + secondStockName + ". The tag has name " + tag.getName())
        .build();

    rabbitTemplate.convertAndSend(Application.topicExchangeName, "analytics.tags", event.toJson());

    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/api/users/{username}/comparisons/tags")
  public ResponseEntity deleteTagForComparison(@PathVariable String username, @RequestParam("firstStock") String firstStockName, @RequestParam("secondStock") String secondStockName) {
    User user = usersService.retrieveUserByUsername(username);

    if (user == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    List<Comparison> comparisons = comparisonService.retrieveComparisonsByUser(user.getId());
    for (Comparison comparison : comparisons) {
      if (comparison.getFirstStockName().equals(firstStockName) && comparison.getSecondStockName().equals(secondStockName)) {
        comparisonService.deleteTagOf(comparison);

        break;
      }
    }

    Event event = new Event.Builder()
        .withAction(Event.EventAction.DELETE)
        .withEntityClass(Tag.class.getName())
        .withUsername(username)
        .withMessage("User with " + username + " deleted a tag for comparison between stock with name " + firstStockName + " and another stock with name " + secondStockName)
        .build();

    rabbitTemplate.convertAndSend(Application.topicExchangeName, "analytics.tags", event.toJson());

    return ResponseEntity.ok().build();
  }

}
