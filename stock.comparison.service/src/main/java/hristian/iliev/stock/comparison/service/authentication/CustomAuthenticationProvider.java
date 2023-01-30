package hristian.iliev.stock.comparison.service.authentication;

import com.google.common.hash.Hashing;
import hristian.iliev.stock.comparison.service.users.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class CustomAuthenticationProvider implements AuthenticationProvider {

  @Autowired
  private UsersService usersService;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String username = authentication.getName();
    String password = authentication.getCredentials().toString();

    UserDetails user = usersService.loadUserByUsername(username);
    if (user == null) {
      throw new BadCredentialsException("No such username");
    }

    return checkPassword(user, password);
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }

  private Authentication checkPassword(UserDetails user, String rawPassword) {
    String sha256EncodedString = Hashing.sha256()
        .hashString(rawPassword, StandardCharsets.UTF_8)
        .toString();

    if (sha256EncodedString.equals(user.getPassword())) {
      return new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());
    } else {
      throw new BadCredentialsException("Bad credentials");
    }
  }

}
