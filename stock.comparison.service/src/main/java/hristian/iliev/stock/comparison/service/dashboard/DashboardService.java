package hristian.iliev.stock.comparison.service.dashboard;

import hristian.iliev.stock.comparison.service.dashboard.entity.Chart;
import hristian.iliev.stock.comparison.service.dashboard.entity.Dashboard;

public interface DashboardService {

  public void addChartToDashboard(String username, String dashboardName, Chart chart);

  public void saveDashboard(Dashboard dashboard);

  public void deleteDashboard(Long dashboardId);

}
