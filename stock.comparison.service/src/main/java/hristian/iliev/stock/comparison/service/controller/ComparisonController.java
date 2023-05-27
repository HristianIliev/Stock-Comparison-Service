package hristian.iliev.stock.comparison.service.controller;

import hristian.iliev.stock.comparison.service.comparison.ComparisonService;
import hristian.iliev.stock.comparison.service.comparison.entity.Comparison;
import hristian.iliev.stock.comparison.service.comparison.entity.Tag;
import hristian.iliev.stock.comparison.service.stocks.StockQuoteService;
import hristian.iliev.stock.comparison.service.users.UsersService;
import hristian.iliev.stock.comparison.service.users.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ComparisonController {

  @Autowired
  private UsersService usersService;

  @Autowired
  private ComparisonService comparisonService;

  @Autowired
  private StockQuoteService stockQuoteService;

  @GetMapping("/api/users/{username}/comparisons/names")
  public ResponseEntity<List<Comparison>> userComparisonNames(@PathVariable String username) {
    User user = usersService.retrieveUserByUsername(username);

    if (user == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    List<Comparison> comparisons = comparisonService.retrieveComparisonsByUser(user.getId());

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

    return ResponseEntity.ok().build();
  }

}
