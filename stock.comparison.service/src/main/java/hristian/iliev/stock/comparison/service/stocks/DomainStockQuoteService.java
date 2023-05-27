package hristian.iliev.stock.comparison.service.stocks;

import com.opencsv.CSVReader;
import hristian.iliev.stock.comparison.service.comparison.entity.Comparison;
import hristian.iliev.stock.comparison.service.comparison.entity.ComparisonCalculations;
import hristian.iliev.stock.comparison.service.comparison.entity.DiagramData;
import hristian.iliev.stock.comparison.service.comparison.repository.TagRepository;
import hristian.iliev.stock.comparison.service.stocks.entity.Quote;
import hristian.iliev.stock.comparison.service.stocks.repository.QuoteRepository;
import hristian.iliev.stock.comparison.service.comparison.entity.DataPoint;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class DomainStockQuoteService implements StockQuoteService {

  private static final int MAX_PERIODS = 200;

  @Autowired
  private QuoteRepository quoteRepository;

  @Autowired
  private TagRepository tagRepository;

  @Override
  public void updateStockQuotes() throws IOException, InterruptedException {
    Iterable<Quote> quotes = quoteRepository.findAll();

    Map<String, List<Quote>> stockQuotes = groupByStockName(quotes);

    sortQuotesByDateTime(stockQuotes);

    for (Map.Entry<String, List<Quote>> entry : stockQuotes.entrySet()) {
      String stockName = entry.getKey();
      List<Quote> quotesForStock = entry.getValue();

      System.out.println("Starting update of stock with name: " + stockName);

      Quote lastQuote = quotesForStock.get(0);

      System.out.println("The last quote of stock with name: " + stockName + " " + lastQuote);

      LocalDate now = LocalDate.now();
      int daysSinceLastQuote = (int) ChronoUnit.DAYS.between(lastQuote.getTime(), LocalDate.now());
      if ((isWeekend(now) && daysSinceLastQuote < 3) || daysSinceLastQuote < 2 || (isMonday(now) && isFriday(lastQuote.getTime()) && daysSinceLastQuote == 3)) {
        System.out.println("Stock with name " + stockName + " doesn't need to be updated.");

        continue;
      }

      System.out.println("Updating stock with name: " + stockName);

      String json = fetchDataFor(stockName);
      if (json.contains("5 calls per minute")) {
        System.out.println("Sleeping for 2 minutes");

        Thread.sleep(120000);
      }

      json = fetchDataFor(stockName);

      File file = new File("temp.csv");
      FileWriter fr = new FileWriter(file, true);
      if (file.createNewFile()) {
        System.out.println("File created: " + file.getName());
      }

      fr.write(json);

      fr.close();

      try (CSVReader reader = new CSVReader(new FileReader("temp.csv"))) {
        String[] line;
        while ((line = reader.readNext()) != null) {
          if (line[0].contains("timestamp") || line[0].contains("{") || line[0].contains("Note") || line[0].contains("}")) {
            continue;
          }

          DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd")
                                                                      .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                                                                      .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                                                                      .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                                                                      .toFormatter();

          LocalDate date = LocalDate.parse(line[0], formatter);

          Quote quote = new Quote(stockName, date, Double.parseDouble(line[1]), Double.parseDouble(line[2]), Double.parseDouble(line[3]), Double.parseDouble(line[4]), Double.parseDouble(line[7]), Integer.parseInt(line[6]));

          if (quote.isBefore(lastQuote) || quote.getTime().getYear() < 2020) {
            System.out.println("Skipping quote: " + quote);

            continue;
          } else {
            System.out.println("Inserting " + quote);

            quoteRepository.save(quote);
          }
        }
      }

      file.delete();
    }
  }

  @Override
  public ComparisonCalculations calculateComparisonData(Comparison comparison, int periods) {
    String firstStockName = comparison.getFirstStockName();
    String secondStockName = comparison.getSecondStockName();

    List<Quote> firstStockQuotes = quoteRepository.findAllByStockName(firstStockName);
    List<Quote> secondStockQuotes = quoteRepository.findAllByStockName(secondStockName);

    sortQuotes(firstStockQuotes);
    sortQuotes(secondStockQuotes);

    List<Double> differences = new ArrayList<>();
    for (int i = 0; i < periods; i++) {
      differences.add(firstStockQuotes.get(i).getClose() - secondStockQuotes.get(i).getClose());
    }

    ComparisonCalculations calculations = new ComparisonCalculations();

    DecimalFormat formatter = new DecimalFormat("#.##");
    formatter.setRoundingMode(RoundingMode.CEILING);

    calculations.setAverageOfDifferences(Double.parseDouble(formatter.format(calculateAverageOfList(differences))));
    calculations.setCorrelationCoefficient(Double.parseDouble(formatter.format(calculateCorrelationCoefficientBetween(firstStockQuotes, secondStockQuotes, periods))));
    calculations.setMedianOfDifferences(Double.parseDouble(formatter.format(calculateMedianOf(differences))));
    calculations.setModeOfDifferences(Double.parseDouble(formatter.format(calculateModeOf(differences))));
    calculations.setStandardDeviationOfDifferences(Double.parseDouble(formatter.format(calculateStandardDeviationOf(differences))));
    calculations.setZScoreOfDifferences(Double.parseDouble(formatter.format(calculateZScoreOf(differences))));

    if (comparison.getTag() != null) {
      calculations.setName(firstStockName + ":" + secondStockName + " <span class=\"badge\" style=\"background-color:" + comparison.getTag().getColor() + ";\">" + comparison.getTag().getName() + "</span>");
    } else {
      calculations.setName(firstStockName + ":" + secondStockName);
    }

    calculations.setFirstLastKnownPrice(Double.parseDouble(formatter.format(firstStockQuotes.get(0).getClose())));
    calculations.setSecondLastKnownPrice(Double.parseDouble(formatter.format(secondStockQuotes.get(0).getClose())));

    List<Double> firstDividends = new ArrayList<>();
    for (int i = 0; i < periods; i++) {
      firstDividends.add(firstStockQuotes.get(i).getDividend());
    }

    List<Double> secondDividends = new ArrayList<>();
    for (int i = 0; i < periods; i++) {
      secondDividends.add(secondStockQuotes.get(i).getDividend());
    }

    calculations.setFirstAvgDividend(Double.parseDouble(formatter.format(calculateAverageOfList(firstDividends))));
    calculations.setSecondAvgDividend(Double.parseDouble(formatter.format(calculateAverageOfList(secondDividends))));

    List<Integer> firstVolumes = new ArrayList<>();
    for (int i = 0; i < periods; i++) {
      firstVolumes.add(firstStockQuotes.get(i).getVolume());
    }

    List<Integer> secondVolumes = new ArrayList<>();
    for (int i = 0; i < periods; i++) {
      secondVolumes.add(secondStockQuotes.get(i).getVolume());
    }

    calculations.setFirstAvgVolume(calculateAverageOfIntList(firstVolumes));
    calculations.setSecondAvgVolume(calculateAverageOfIntList(secondVolumes));

    if (comparison.getTag() != null) {
      calculations.setTagColor(comparison.getTag().getColor());
      calculations.setTagName(comparison.getTag().getName());
    }

    System.out.println(calculations);

    return calculations;
  }

  @Override
  public boolean quotesForStockWithNameDoNotExist(String stockName) {
    List<Quote> stockQuotes = quoteRepository.findAllByStockName(stockName);

    return stockQuotes == null || stockQuotes.isEmpty();
  }

  @Override
  public DiagramData calculateComparisonDiagramData(Comparison comparison, int periods) {
    String firstStockName = comparison.getFirstStockName();
    String secondStockName = comparison.getSecondStockName();

    List<Quote> firstStockQuotes = quoteRepository.findAllByStockName(firstStockName);
    List<Quote> secondStockQuotes = quoteRepository.findAllByStockName(secondStockName);

    sortQuotes(firstStockQuotes);
    sortQuotes(secondStockQuotes);

    DecimalFormat decimalFormatter = new DecimalFormat("#.##");
    decimalFormatter.setRoundingMode(RoundingMode.CEILING);

    List<Double> differences = new ArrayList<>();
    for (int i = 0; i < periods; i++) {
      differences.add(Double.parseDouble(decimalFormatter.format(firstStockQuotes.get(i).getClose() - secondStockQuotes.get(i).getClose())));
    }

    List<String> times = new ArrayList<>();
    for (int i = 0; i < periods; i++) {
      LocalDate localDate = firstStockQuotes.get(i).getTime();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      String formattedDate = localDate.format(formatter);

      times.add(formattedDate);
    }

    List<Double> firstClosingPrices = new ArrayList<>();
    for (int i = 0; i < periods; i++) {
      firstClosingPrices.add(Double.parseDouble(decimalFormatter.format(firstStockQuotes.get(i).getClose())));
    }

    List<Double> secondClosingPrices = new ArrayList<>();
    for (int i = 0; i < periods; i++) {
      secondClosingPrices.add(Double.parseDouble(decimalFormatter.format(secondStockQuotes.get(i).getClose())));
    }

    List<Integer> firstVolumes = new ArrayList<>();
    for (int i = 0; i < periods; i++) {
      firstVolumes.add(firstStockQuotes.get(i).getVolume());
    }
    List<Integer> secondVolumes = new ArrayList<>();
    for (int i = 0; i < periods; i++) {
      secondVolumes.add(secondStockQuotes.get(i).getVolume());
    }

    List<DataPoint> dataPoints = new ArrayList<>();
    for (int i = 0; i < periods; i++) {
      DataPoint dataPoint = new DataPoint();
      dataPoint.setTime(times.get(i));
      dataPoint.setValue(differences.get(i));
      dataPoint.setFirstPrice(firstClosingPrices.get(i));
      dataPoint.setSecondPrice(secondClosingPrices.get(i));
      dataPoint.setFirstVolume(firstVolumes.get(i));
      dataPoint.setSecondVolume(secondVolumes.get(i));

      dataPoints.add(dataPoint);
    }

    ComparisonCalculations calculations = calculateComparisonData(comparison, periods);
    calculations.setFirstAvgPrice(Double.parseDouble(decimalFormatter.format(calculateAverageOfList(firstClosingPrices))));
    calculations.setSecondAvgPrice(Double.parseDouble(decimalFormatter.format(calculateAverageOfList(secondClosingPrices))));

    DiagramData diagramData = new DiagramData();
    diagramData.setDataPoints(dataPoints);
    diagramData.setCalculations(calculations);

    return diagramData;
  }

  private double calculateZScoreOf(List<Double> numbers) {
    double sum = 0.0;

    for(double temp : numbers) {
      sum += temp;
    }

    double averageSpread = sum / numbers.size();
    double zScore;

    if (numbers.get(0) - averageSpread == 0.0) {
      zScore = 0.0;
    } else {
      zScore = (Math.abs(numbers.get(0)) - Math.abs(averageSpread)) / Math.sqrt(Math.abs(sum) / numbers.size());
    }

    return zScore;
  }

  private double calculateStandardDeviationOf(List<Double> differences) {
    double sum = 0.0;
    double standardDeviation = 0.0;

    for(double temp : differences) {
      sum += temp;
    }

    double mean = sum / differences.size();

    for(double temp: differences) {
      standardDeviation += Math.pow(temp - mean, 2);
    }

    return Math.sqrt(standardDeviation / differences.size());
  }

  private double calculateModeOf(List<Double> numbers) {
    Double[] nums = new Double[numbers.size()];
    numbers.toArray(nums);

    double maxValue = 0.0;
    int maxCount = 0;

    for (int i = 0; i < nums.length; ++i) {
      int count = 0;
      for (int j = 0; j < nums.length; ++j) {
        if (nums[j].equals(nums[i]))
          count++;
      }

      if (count > maxCount) {
        maxCount = count;
        maxValue = nums[i];
      }
    }

    return maxValue;
  }

  private double calculateMedianOf(List<Double> numbers) {
    Double[] nums = new Double[numbers.size()];
    numbers.toArray(nums);

    double median;
    if (nums.length % 2 == 0) {
      median = ((double) nums[nums.length / 2] + (double) nums[nums.length / 2 - 1]) / 2;
    } else {
      median = (double) nums[nums.length / 2];
    }

    return median;
  }

  private double calculateCorrelationCoefficientBetween(List<Quote> firstStockQuotes, List<Quote> secondStockQuotes, int periods) {
    double[] firstArray = new double[periods];
    double[] secondArray = new double[periods];

    for (int i = 0; i < periods; i++) {
      firstArray[i] = firstStockQuotes.get(i).getClose();
    }

    for (int i = 0; i < periods; i++) {
      secondArray[i] = secondStockQuotes.get(i).getClose();
    }

    return new PearsonsCorrelation().correlation(firstArray, secondArray);
  }

  private double calculateAverageOfList(List<Double> numbers) {
    double sum = 0.0;
    for (double number : numbers) {
      sum += number;
    }

    return sum / numbers.size();
  }

  private long calculateAverageOfIntList(List<Integer> numbers) {
    long sum = 0;
    for (int number : numbers) {
      sum += number;
    }

    return sum / numbers.size();
  }

  private boolean isWeekend(LocalDate date) {
    DayOfWeek day = DayOfWeek.of(date.get(ChronoField.DAY_OF_WEEK));

    return day == DayOfWeek.SUNDAY || day == DayOfWeek.SATURDAY;
  }

  private boolean isMonday(LocalDate date) {
    DayOfWeek day = DayOfWeek.of(date.get(ChronoField.DAY_OF_WEEK));

    return day == DayOfWeek.MONDAY;
  }

  private boolean isFriday(LocalDate date) {
    DayOfWeek day = DayOfWeek.of(date.get(ChronoField.DAY_OF_WEEK));

    return day == DayOfWeek.FRIDAY;
  }

  private String fetchDataFor(String stockName) throws IOException {
    String url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol=" + stockName + "&outputsize=full&apikey=SNV8SHFH46IZ5F4B&datatype=csv";
    Request request = new Request.Builder().url(url).build();

    OkHttpClient client = new OkHttpClient();

    try (Response response = client.newCall(request).execute()) {
      return response.body().string();
    }
  }

  private void sortQuotesByDateTime(Map<String, List<Quote>> stockQuotes) {
    for (Map.Entry<String, List<Quote>> entry : stockQuotes.entrySet()) {
      sortQuotes(entry.getValue());
    }
  }

  private void sortQuotes(List<Quote> stockQuotes) {
    Collections.sort(stockQuotes, ((Comparator<Quote>) (firstQuote, secondQuote) -> firstQuote.getTime().compareTo(secondQuote.getTime())).reversed());
  }

  private Map<String, List<Quote>> groupByStockName(Iterable<Quote> quotes) {
    Map<String, List<Quote>> result = new HashMap<>();
    for (Quote quote : quotes) {
      if (result.containsKey(quote.getStockName())) {
        result.get(quote.getStockName()).add(quote);
      } else {
        List<Quote> list = new ArrayList<>();
        list.add(quote);

        result.put(quote.getStockName(), list);
      }
    }

    return result;
  }

}
