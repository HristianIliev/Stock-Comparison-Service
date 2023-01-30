package hristian.iliev.stock.comparison.service.stocks.repository;

import hristian.iliev.stock.comparison.service.stocks.entity.Quote;
import org.springframework.data.repository.CrudRepository;

public interface QuoteRepository extends CrudRepository<Quote, Long>, QuoteRepositoryCustom {
}
