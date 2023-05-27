package hristian.iliev.stock.comparison.service.dashboard;

import hristian.iliev.stock.comparison.service.dashboard.entity.Chart;
import hristian.iliev.stock.comparison.service.dashboard.entity.Dashboard;
import hristian.iliev.stock.comparison.service.dashboard.repository.ChartRepository;
import hristian.iliev.stock.comparison.service.dashboard.repository.DashboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.InvalidParameterException;

@Service
public class DomainDashboardService implements DashboardService {

  @Autowired
  private DashboardRepository dashboardRepository;

  @Autowired
  private ChartRepository chartRepository;

  @Override
  public void addChartToDashboard(String username, String dashboardName, Chart chart) {
    for (Dashboard dashboard : dashboardRepository.findAll()) {
      if (dashboard.getUser().getUsername().equals(username) && dashboard.getName().equals(dashboardName)) {
        for (Chart chartFromDB : dashboard.getCharts()) {
          if (chartFromDB.getFirstStockName().equals(chart.getFirstStockName()) && chartFromDB.getSecondStockName().equals(chart.getSecondStockName()) && chartFromDB.getPeriods() == chart.getPeriods()) {
            throw new InvalidParameterException("Chart already exists");
          }
        }

        chart.setDashboard(dashboard);

        chartRepository.save(chart);
      }
    }
  }

  @Override
  public void saveDashboard(Dashboard dashboard) {
    dashboardRepository.save(dashboard);
  }

  @Override
  public void deleteDashboard(Long dashboardId) {
    dashboardRepository.deleteById(dashboardId);
  }

}
