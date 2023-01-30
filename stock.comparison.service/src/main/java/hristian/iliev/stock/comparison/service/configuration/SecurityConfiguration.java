package hristian.iliev.stock.comparison.service.configuration;

import hristian.iliev.stock.comparison.service.authentication.CustomAuthenticationProvider;
import hristian.iliev.stock.comparison.service.authentication.LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

  @Autowired
  private CustomAuthenticationProvider authenticationProvider;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf().disable();

    http
        .authorizeHttpRequests(requests -> requests
            .antMatchers("/css/**", "/images/**", "/js/**", "/register")
            .permitAll()
            .antMatchers(HttpMethod.POST, "/api/register")
            .permitAll()
            .anyRequest()
            .authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login")
            .successHandler(new LoginSuccessHandler())
            .permitAll()
        )
        .logout(LogoutConfigurer::permitAll);

    return http.build();
  }

  @Autowired
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(authenticationProvider);
  }

}
