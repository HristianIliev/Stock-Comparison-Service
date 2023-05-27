package hristian.iliev.stock.comparison.service.controller;

import hristian.iliev.stock.comparison.service.dashboard.DashboardService;
import hristian.iliev.stock.comparison.service.dashboard.entity.Chart;
import hristian.iliev.stock.comparison.service.dashboard.entity.Dashboard;
import hristian.iliev.stock.comparison.service.users.UsersService;
import hristian.iliev.stock.comparison.service.users.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.InvalidParameterException;

@Controller
public class DashboardController {

  @Autowired
  private UsersService usersService;

  @Autowired
  private DashboardService dashboardService;

  @PostMapping("/api/users/{username}/dashboards")
  public ResponseEntity addDashboard(@PathVariable String username, @RequestBody Dashboard dashboard) {
    User user = usersService.retrieveUserByUsername(username);

    if (user == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    dashboard.setUser(user);

    dashboardService.saveDashboard(dashboard);

    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/api/users/{username}/dashboards/{dashboardId}")
  public ResponseEntity deleteDashboard(@PathVariable String username, @PathVariable int dashboardId) {
    dashboardService.deleteDashboard(new Long(dashboardId));

    return ResponseEntity.ok().build();
  }

  @PostMapping("/api/users/{username}/dashboards/{dashboardName}/charts")
  public ResponseEntity addChartToDashboard(@PathVariable String username, @PathVariable String dashboardName, @RequestBody Chart chart) {
    System.out.println(chart);

    User user = usersService.retrieveUserByUsername(username);

    if (user == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    try {
      dashboardService.addChartToDashboard(username, dashboardName, chart);
    } catch(InvalidParameterException exception) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    return ResponseEntity.ok().build();
  }

}
