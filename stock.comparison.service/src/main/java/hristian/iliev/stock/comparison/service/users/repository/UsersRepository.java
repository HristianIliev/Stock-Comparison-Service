package hristian.iliev.stock.comparison.service.users.repository;

import hristian.iliev.stock.comparison.service.users.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UsersRepository extends CrudRepository<User, Long> {
}
