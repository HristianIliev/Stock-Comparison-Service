package hristian.iliev.stock.comparison.service.stocks.repository;

import hristian.iliev.stock.comparison.service.stocks.entity.Quote;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class QuoteRepositoryImpl implements QuoteRepositoryCustom {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public List<Quote> findAllByStockName(String stockName) {
    Query query = entityManager.createNativeQuery("SELECT q.* FROM quote as q " +
        "WHERE q.stock_name = ?", Quote.class);
    query.setParameter(1, stockName);

    return query.getResultList();
  }

}
