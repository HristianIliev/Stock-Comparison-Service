package hristian.iliev.stock.comparison.service.users;

import hristian.iliev.stock.comparison.service.users.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UsersService extends UserDetailsService {

  public void save(User user);

  public User retrieveUserByUsername(String username);

}
