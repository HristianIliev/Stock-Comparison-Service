package hristian.iliev.stock.comparison.service.comparison;

import hristian.iliev.stock.comparison.service.comparison.entity.Comparison;
import hristian.iliev.stock.comparison.service.comparison.entity.Tag;
import hristian.iliev.stock.comparison.service.users.entity.User;

import java.util.List;

public interface ComparisonService {

  public List<Comparison> retrieveComparisonsByUser(long userId);

  public void addComparisonToUser(User user, String firstStockName, String secondStockName);

  public void tagComparison(Long id, String firstStockName, String secondStockName, Tag tag);

  public void deleteTagOf(Comparison comparison);

  public void deleteComparison(User user, String firstStockName, String secondStockName);

}
