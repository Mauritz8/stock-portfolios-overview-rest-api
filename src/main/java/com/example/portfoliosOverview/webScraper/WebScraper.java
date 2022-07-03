package com.example.portfoliosOverview.webScraper;

import com.example.portfoliosOverview.models.*;
import com.example.portfoliosOverview.repositories.IndexRepository;
import com.example.portfoliosOverview.repositories.PortfolioRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Service
@EnableScheduling
@Transactional
public class WebScraper {

    @Autowired
    PortfolioRepository portfolioRepository;

    @Autowired
    IndexRepository indexRepository;

    double usDollarConversion = 10;

    @Scheduled(cron = "0 0 23 * * 1-5")
    public void main() {
        setUSDollarConversion();

        List<Portfolio> portfolios = getPortfolios();
        for (Portfolio portfolio : portfolios) {
            List<Stock> stocks = portfolio.getStocks();

            for (Stock stock : stocks) {
                updateStockInPortfolio(stock);
            }
            updatePortfolio(portfolio);
        }

        List<Index> indexes = indexRepository.findAll();
        for (Index index : indexes) {
            updateIndex(index);
        }
    }

    public boolean stockExists(Stock stock) {
        String urlLink = "https://in.investing.com/equities/" + stock.getName();
        if (stock.getCid() != null) {
            urlLink += "?cid=" + stock.getCid();
        }
        try {
            URL url = new URL(urlLink);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestProperty("User-Agent", "Mozilla/5.0");
            int responseCode = http.getResponseCode();
            http.disconnect();
            return responseCode == 200;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean indexExists(String indexName) {
        String urlLink = "https://in.investing.com/indices/" + indexName;
        try {
            URL url = new URL(urlLink);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestProperty("User-Agent", "Mozilla/5.0");
            int responseCode = http.getResponseCode();
            http.disconnect();
            return responseCode == 200;
        } catch (Exception e) {
            return false;
        }
    }

    public void updatePortfolio(Portfolio portfolio) {
        double totalMoneyInvested = getTotalMoneyInvested(portfolio);
        portfolio.setTotalMoneyInvested(totalMoneyInvested);

        for (Stock stock : portfolio.getStocks()) {
            double moneyInvestedInStock = stock.getMoneyInvestedInStock();
            double percentOfPortfolio = getPercentOfPortfolio(moneyInvestedInStock, totalMoneyInvested);
            stock.setPercentOfPortfolio(percentOfPortfolio);
        }

        setPercentChangesPortfolio(portfolio);
    }

    public void updateStockInPortfolio(Stock stock) {
        double currentPrice = getCurrentStockPrice(stock);
        double lastMonthPrice = getLastMonthStockPrice(stock);
        double lastWeekPrice = getLastWeekStockPrice(stock);
        double lastDayPrice = getLastDayStockPrice(stock);
        double moneyInvestedInStock = getMoneyInvestedInStock(stock, currentPrice);

        double percentChange1Month = getPercentChange(currentPrice, lastMonthPrice);
        double percentChange1Week = getPercentChange(currentPrice, lastWeekPrice);
        double percentChange1Day = getPercentChange(currentPrice, lastDayPrice);

        stock.setCurrentPrice(currentPrice);
        stock.setMoneyInvestedInStock(moneyInvestedInStock);
        stock.setPercentChange1Month(percentChange1Month);
        stock.setPercentChange1Week(percentChange1Week);
        stock.setPercentChange1Day(percentChange1Day);
    }

    public void updateIndex(Index index) {
        double currentPrice = getCurrentIndexPrice(index);
        double lastMonthPrice = getLastMonthIndexPrice(index);
        double lastWeekPrice = getLastWeekIndexPrice(index);
        double lastDayPrice = getLastDayIndexPrice(index);

        double percentChangeIndex1Month = getPercentChange(currentPrice, lastMonthPrice);
        double percentChangeIndex1Week = getPercentChange(currentPrice, lastWeekPrice);
        double percentChangeIndex1Day = getPercentChange(currentPrice, lastDayPrice);

        index.setPercentChange1Month(percentChangeIndex1Month);
        index.setPercentChange1Week(percentChangeIndex1Week);
        index.setPercentChange1Day(percentChangeIndex1Day);
    }

    private List<Portfolio> getPortfolios() {
        return portfolioRepository.findAll();
    }

    private double getPercentChange(double newValue, double oldValue) {
        double percentChange = (newValue / oldValue - 1) * 100;
        percentChange = (double) Math.round(percentChange * 100.0) / 100.0;
        return percentChange;
    }

    private double getPercentOfPortfolio(double moneyInvestedInStock, double totalMoneyInvested) {
        double percentOfPortfolio =  moneyInvestedInStock / totalMoneyInvested * 100;
        percentOfPortfolio = (double) Math.round(percentOfPortfolio * 100.0) / 100.0;
        return percentOfPortfolio;
    }

    private double getTotalMoneyInvested(Portfolio portfolio) {
        double totalMoneyInvested = 0;
        List<Stock> stocks = portfolio.getStocks();

        for (Stock stock : stocks) {
            double moneyInvested = stock.getMoneyInvestedInStock();
            totalMoneyInvested += moneyInvested;
        }
        totalMoneyInvested = (double) Math.round(totalMoneyInvested * 100.0) / 100.0;
        return totalMoneyInvested;
    }

    private void setPercentChangesPortfolio(Portfolio portfolio) {
        double percentChangePortfolio1Month = 0;
        double percentChangePortfolio1Week = 0;
        double percentChangePortfolio1Day = 0;

        List<Stock> stocks = portfolio.getStocks();
        for (Stock stock : stocks) {
            double percentOfPortfolio = stock.getPercentOfPortfolio();

            double percentChange1Month = stock.getPercentChange1Month();
            double percentChange1Week = stock.getPercentChange1Week();
            double percentChange1Day = stock.getPercentChange1Day();

            percentChangePortfolio1Month += percentChange1Month * (percentOfPortfolio / 100);
            percentChangePortfolio1Week += percentChange1Week * (percentOfPortfolio / 100);
            percentChangePortfolio1Day += percentChange1Day * (percentOfPortfolio / 100);
        }
        percentChangePortfolio1Month = (double) Math.round(percentChangePortfolio1Month * 100.0) / 100.0;
        percentChangePortfolio1Week = (double) Math.round(percentChangePortfolio1Week * 100.0) / 100.0;
        percentChangePortfolio1Day = (double) Math.round(percentChangePortfolio1Day * 100.0) / 100.0;

        portfolio.setPercentChange1Month(percentChangePortfolio1Month);
        portfolio.setPercentChange1Week(percentChangePortfolio1Week);
        portfolio.setPercentChange1Day(percentChangePortfolio1Day);
    }

    private double getCurrentStockPrice(Stock stock) {
        String url = "https://in.investing.com/equities/" + stock.getName() + "-historical-data";
        if (stock.getCid() != null) {
            url += "?cid=" + stock.getCid();
        }

        double currentPrice = 0;

        try {
            final Document document = Jsoup.connect(url).get();
            Elements rows = document.select("table.common-table.medium.js-table tr");
            currentPrice = Double.parseDouble(rows.get(1).select("td:nth-of-type(2)").text().replace(",", ""));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return currentPrice;
    }

    private double getLastMonthStockPrice(Stock stock) {
        long lastWorkingDayEpoch = convertDateToEpoch(new Date());
        Date date1MonthFromLastWorkingDay = getDate1MonthPrior(new Date());
        long date1MonthFromLastWorkingDayEpoch = convertDateToEpoch(date1MonthFromLastWorkingDay);

        String url = "https://in.investing.com/equities/" + stock.getName() + "-historical-data?st_date=" + date1MonthFromLastWorkingDayEpoch +
                "&end_date=" + lastWorkingDayEpoch + "&interval_sec=daily";
        if (stock.getCid() != null) {
            url += "&cid=" + stock.getCid();
        }

        double lastMonthPrice = 0;

        try {
            final Document document = Jsoup.connect(url).get();
            Elements rows = document.select("table.common-table.medium.js-table tr");
            int amountOfRows = rows.size();
            lastMonthPrice = Double.parseDouble(rows.get(amountOfRows - 1).select("td:nth-of-type(2)").text().replace(",", ""));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return lastMonthPrice;
    }

    private double getLastWeekStockPrice(Stock stock) {
        long lastWorkingDayEpoch = convertDateToEpoch(new Date());
        Date date1WeekFromLastWorkingDay = getDate1WeekPrior(new Date());
        long date1WeekFromLastWorkingDayEpoch = convertDateToEpoch(date1WeekFromLastWorkingDay);

        String url = "https://in.investing.com/equities/" + stock.getName() + "-historical-data?st_date=" + date1WeekFromLastWorkingDayEpoch +
                "&end_date=" + lastWorkingDayEpoch + "&interval_sec=daily";
        if (stock.getCid() != null) {
            url += "&cid=" + stock.getCid();
        }

        double lastWeekPrice = 0;

        try {
            final Document document = Jsoup.connect(url).get();
            Elements rows = document.select("table.common-table.medium.js-table tr");
            int amountOfRows = rows.size();
            lastWeekPrice = Double.parseDouble(rows.get(amountOfRows - 1).select("td:nth-of-type(2)").text().replace(",", ""));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return lastWeekPrice;
    }

    private double getLastDayStockPrice(Stock stock) {
        String url = "https://in.investing.com/equities/" + stock.getName() + "-historical-data";
        if (stock.getCid() != null) {
            url += "?cid=" + stock.getCid();
        }

        double lastDayPrice = 0;

        try {
            final Document document = Jsoup.connect(url).get();
            Elements rows = document.select("table.common-table.medium.js-table tr");
            lastDayPrice = Double.parseDouble(rows.get(2).select("td:nth-of-type(2)").text().replace(",", ""));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return lastDayPrice;
    }


    private double getCurrentIndexPrice(Index index) {
        String url = "https://in.investing.com/indices/" + index.getName() + "-historical-data";

        double currentPrice = 0;

        try {
            final Document document = Jsoup.connect(url).get();
            Elements rows = document.select("table.common-table.medium.js-table tr");
            currentPrice = Double.parseDouble(rows.get(1).select("td:nth-of-type(2)").text().replace(",", ""));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return currentPrice;
    }

    private double getLastMonthIndexPrice(Index index) {
        long lastWorkingDayEpoch = convertDateToEpoch(new Date());
        Date date1MonthFromLastWorkingDay = getDate1MonthPrior(new Date());
        long date1MonthFromLastWorkingDayEpoch = convertDateToEpoch(date1MonthFromLastWorkingDay);

        String url = "https://in.investing.com/indices/" + index.getName() + "-historical-data?st_date=" + date1MonthFromLastWorkingDayEpoch +
                "&end_date=" + lastWorkingDayEpoch + "&interval_sec=daily";

        double lastMonthPrice = 0;

        try {
            final Document document = Jsoup.connect(url).get();
            Elements rows = document.select("table.common-table.medium.js-table tr");
            int amountOfRows = rows.size();
            lastMonthPrice = Double.parseDouble(rows.get(amountOfRows - 1).select("td:nth-of-type(2)").text().replace(",", ""));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return lastMonthPrice;
    }

    private double getLastWeekIndexPrice(Index index) {
        long lastWorkingDayEpoch = convertDateToEpoch(new Date());
        Date date1WeekFromLastWorkingDay = getDate1WeekPrior(new Date());
        long date1WeekFromLastWorkingDayEpoch = convertDateToEpoch(date1WeekFromLastWorkingDay);

        String url = "https://in.investing.com/indices/" + index.getName() + "-historical-data?st_date=" + date1WeekFromLastWorkingDayEpoch +
                "&end_date=" + lastWorkingDayEpoch + "&interval_sec=daily";

        double lastWeekPrice = 0;

        try {
            final Document document = Jsoup.connect(url).get();
            Elements rows = document.select("table.common-table.medium.js-table tr");
            int amountOfRows = rows.size();
            lastWeekPrice = Double.parseDouble(rows.get(amountOfRows - 1).select("td:nth-of-type(2)").text().replace(",", ""));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return lastWeekPrice;
    }

    private double getLastDayIndexPrice(Index index) {
        String url = "https://in.investing.com/indices/" + index.getName() + "-historical-data";

        double lastDayPrice = 0;

        try {
            final Document document = Jsoup.connect(url).get();
            Elements rows = document.select("table.common-table.medium.js-table tr");
            lastDayPrice = Double.parseDouble(rows.get(2).select("td:nth-of-type(2)").text().replace(",", ""));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return lastDayPrice;
    }

    private Date getDate1MonthPrior(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, -1);
        return cal.getTime();
    }

    private Date getDate1WeekPrior(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, -7);
        return cal.getTime();
    }

    private long convertDateToEpoch(Date date) {
        return date.getTime() / 1000;
    }

    public double getMoneyInvestedInStock(Stock stock, double currentPrice) {
        double moneyInvestedInStock = stock.getAmountOfShares() * currentPrice;
        if (stock.isUS()) {
            moneyInvestedInStock *= usDollarConversion;
        }
        moneyInvestedInStock = (double) Math.round(moneyInvestedInStock * 100.0) / 100.0;
        return moneyInvestedInStock;
    }


    public List<SearchedStock> findStocksMatchingQuery(String query) {
        String url = "https://in.investing.com/search/?q=" + query;

        List<SearchedStock> stocks = new ArrayList<>();

        try {
            final Document document = Jsoup.connect(url).get();
            Elements rows = document.select("div.js-inner-search-results-wrapper.common-table.medium a.tr.common-table-item");

            for (Element row : rows) {
                String displayName = row.select("span.td.col-name").text();
                String[] infoTexts = row.select("span.td.col-type").text().split("-");
                String type = "";
                String exchange = "";
                if (infoTexts.length == 2) {
                    type = infoTexts[0].strip();
                    exchange = infoTexts[1].strip();
                }
                // only get rows that are stocks from the Stockholm, NYSE, or NASDAQ exchange
                if (type.equals("Share") && (exchange.equals("Stockholm") || exchange.equals("NYSE") || exchange.equals("NASDAQ"))) {
                    boolean isUS = false;
                    if (!exchange.equals("Stockholm")) {
                        isUS = true;
                    }

                    // extract name of stock in url
                    // extract the cid url parameter
                    String stockUrl = row.attr("href");
                    String[] splitUrl = stockUrl.split("\\?");
                    String path = splitUrl[0];
                    String name = path.split("/")[2];
                    String cid = "";
                    if (splitUrl.length == 2) {
                        cid = splitUrl[1].split("\\=")[1];
                    }
                    stocks.add(new SearchedStock(name, displayName, exchange, isUS, cid));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return stocks;
    }

    public List<SearchedIndex> findIndexesMatchingQuery(String query) {
        String url = "https://in.investing.com/search/?q=" + query;

        List<SearchedIndex> indexes = new ArrayList<>();

        try {
            final Document document = Jsoup.connect(url).get();
            Elements rows = document.select("div.js-inner-search-results-wrapper.common-table.medium a.tr.common-table-item");

            for (Element row : rows) {
                String displayName = row.select("span.td.col-name").text();
                String[] infoTexts = row.select("span.td.col-type").text().split("-");
                String type = "";
                String exchange = "";
                if (infoTexts.length == 2) {
                    type = infoTexts[0].strip();
                    exchange = infoTexts[1].strip();
                }
                // only get rows that are indexes from the Stockholm, NYSE, or NASDAQ exchange
                if (type.equals("Index") && (exchange.equals("Stockholm") || exchange.equals("NYSE") || exchange.equals("NASDAQ"))) {
                    // extract name of stock in url
                    String stockUrl = row.attr("href");
                    String[] splitUrl = stockUrl.split("\\?");
                    String path = splitUrl[0];
                    String name = path.split("/")[2];
                    indexes.add(new SearchedIndex(name, displayName));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return indexes;
    }

    private void setUSDollarConversion() {
        try {
            Properties properties = new Properties();
            File file = new File("src/main/java/com/example/portfoliosOverview/webScraper/secret.properties");
            properties.load(new FileInputStream(file));
            String apiKey = properties.getProperty("API_KEY");
            URL url = new URL("https://v6.exchangerate-api.com/v6/" + apiKey + "/pair/USD/SEK");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestProperty("User-Agent", "Mozilla/5.0");
            http.connect();
            JsonObject jsonObject = JsonParser.parseReader(new InputStreamReader((InputStream) http.getContent())).getAsJsonObject();
            usDollarConversion = jsonObject.get("conversion_rate").getAsDouble();
            http.disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @PostConstruct
    private void setUSDollarConversionOnStartup() {
        setUSDollarConversion();
    }
}
