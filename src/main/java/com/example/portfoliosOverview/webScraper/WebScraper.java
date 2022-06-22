package com.example.portfoliosOverview.webScraper;

import com.example.portfoliosOverview.models.*;
import com.example.portfoliosOverview.repositories.IndexRepository;
import com.example.portfoliosOverview.repositories.PortfolioRepository;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@EnableScheduling
@Transactional
public class WebScraper {

    @Autowired
    PortfolioRepository portfolioRepository;

    @Autowired
    IndexRepository indexRepository;

    double usDollarConversion = 8.43;

    @Scheduled(cron = "0 0 23 * * 1-5")
    public void main() {
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

        double percentChangePortfolio1Month = getPercentChangePortfolio1Month(portfolio);
        double percentChangePortfolio1Week = getPercentChangePortfolio1Week(portfolio);
        double percentChangePortfolio1Day = getPercentChangePortfolio1Day(portfolio);
        portfolio.setPercentChange1Month(percentChangePortfolio1Month);
        portfolio.setPercentChange1Week(percentChangePortfolio1Week);
        portfolio.setPercentChange1Day(percentChangePortfolio1Day);
    }

    public void updateStockInPortfolio(Stock stock) {
        double currentPrice = getCurrentStockPrice(stock);
        double moneyInvestedInStock = getMoneyInvestedInStock(stock, currentPrice);
        double percentChange1Month = getPercentChangeStock1Month(stock);
        double percentChange1Week = getPercentChangeStock1Week(stock);
        double percentChange1Day = getPercentChangeStock1Day(stock);
        stock.setCurrentPrice(currentPrice);
        stock.setMoneyInvestedInStock(moneyInvestedInStock);
        stock.setPercentChange1Month(percentChange1Month);
        stock.setPercentChange1Week(percentChange1Week);
        stock.setPercentChange1Day(percentChange1Day);
    }

    public void updateIndex(Index index) {
        double percentChangeIndex1Month = getPercentChangeIndex1Month(index);
        double percentChangeIndex1Week = getPercentChangeIndex1Week(index);
        double percentChangeIndex1Day = getPercentChangeIndex1Day(index);
        index.setPercentChange1Month(percentChangeIndex1Month);
        index.setPercentChange1Week(percentChangeIndex1Week);
        index.setPercentChange1Day(percentChangeIndex1Day);
    }

    List<Portfolio> getPortfolios() {
        return portfolioRepository.findAll();
    }

    double getPercentChangeStock1Month(Stock stock) {
        double currentPrice = getCurrentStockPrice(stock);
        double lastMonthPrice = getLastMonthStockPrice(stock);
        double percentChange = (currentPrice / lastMonthPrice - 1) * 100;
        percentChange = (double) Math.round(percentChange * 100.0) / 100.0;
        return percentChange;
    }

    double getPercentChangeStock1Week(Stock stock) {
        double currentPrice = getCurrentStockPrice(stock);
        double lastWeekPrice = getLastWeekStockPrice(stock);
        double percentChange = (currentPrice / lastWeekPrice - 1) * 100;
        percentChange = (double) Math.round(percentChange * 100.0) / 100.0;
        return percentChange;
    }

    double getPercentChangeStock1Day(Stock stock) {
        double currentPrice = getCurrentStockPrice(stock);
        double lastDayPrice = getLastDayStockPrice(stock);
        double percentChange = (currentPrice / lastDayPrice - 1) * 100;
        percentChange = (double) Math.round(percentChange * 100.0) / 100.0;
        return percentChange;
    }

    double getPercentChangeIndex1Month(Index index) {
        double currentPrice = getCurrentIndexPrice(index);
        double lastMonthPrice = getLastMonthIndexPrice(index);
        double percentChange = (currentPrice / lastMonthPrice - 1) * 100;
        percentChange = (double) Math.round(percentChange * 100.0) / 100.0;
        return percentChange;
    }

    double getPercentChangeIndex1Week(Index index) {
        double currentPrice = getCurrentIndexPrice(index);
        double lastWeekPrice = getLastWeekIndexPrice(index);
        double percentChange = (currentPrice / lastWeekPrice - 1) * 100;
        percentChange = (double) Math.round(percentChange * 100.0) / 100.0;
        return percentChange;
    }

    double getPercentChangeIndex1Day(Index index) {
        double currentPrice = getCurrentIndexPrice(index);
        double lastDayPrice = getLastDayIndexPrice(index);
        double percentChange = (currentPrice / lastDayPrice - 1) * 100;
        percentChange = (double) Math.round(percentChange * 100.0) / 100.0;
        return percentChange;
    }

    double getPercentOfPortfolio(double moneyInvestedInStock, double totalMoneyInvested) {
        double percentOfPortfolio =  moneyInvestedInStock / totalMoneyInvested * 100;
        percentOfPortfolio = (double) Math.round(percentOfPortfolio * 100.0) / 100.0;
        return percentOfPortfolio;
    }

    double getTotalMoneyInvested(Portfolio portfolio) {
        double totalMoneyInvested = 0;
        List<Stock> stocks = portfolio.getStocks();

        for (Stock stock : stocks) {
            double moneyInvested = stock.getMoneyInvestedInStock();
            totalMoneyInvested += moneyInvested;
        }
        return totalMoneyInvested;
    }

    double getPercentChangePortfolio1Month(Portfolio portfolio) {
        double percentChangePortfolio = 0;
        List<Stock> stocks = portfolio.getStocks();

        for (Stock stock : stocks) {
            double percentChange = stock.getPercentChange1Month();
            double percentOfPortfolio = stock.getPercentOfPortfolio();
            percentChangePortfolio += percentChange * (percentOfPortfolio / 100);
            percentChangePortfolio = (double) Math.round(percentChangePortfolio * 100.0) / 100.0;
        }
        return percentChangePortfolio;
    }

    double getPercentChangePortfolio1Week(Portfolio portfolio) {
        double percentChangePortfolio = 0;
        List<Stock> stocks = portfolio.getStocks();

        for (Stock stock : stocks) {
            double percentChange = getPercentChangeStock1Week(stock);
            double percentOfPortfolio = stock.getPercentOfPortfolio();
            percentChangePortfolio += percentChange * (percentOfPortfolio / 100);
            percentChangePortfolio = (double) Math.round(percentChangePortfolio * 100.0) / 100.0;
        }
        return percentChangePortfolio;
    }

    double getPercentChangePortfolio1Day(Portfolio portfolio) {
        double percentChangePortfolio = 0;
        List<Stock> stocks = portfolio.getStocks();

        for (Stock stock : stocks) {
            double percentChange = getPercentChangeStock1Day(stock);
            double percentOfPortfolio = stock.getPercentOfPortfolio();
            percentChangePortfolio += percentChange * (percentOfPortfolio / 100);
            percentChangePortfolio = (double) Math.round(percentChangePortfolio * 100.0) / 100.0;
        }
        return percentChangePortfolio;
    }

    double getCurrentStockPrice(Stock stock) {
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

    double getLastMonthStockPrice(Stock stock) {
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

    double getLastWeekStockPrice(Stock stock) {
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

    double getLastDayStockPrice(Stock stock) {
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


    double getCurrentIndexPrice(Index index) {
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

    double getLastMonthIndexPrice(Index index) {
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

    double getLastWeekIndexPrice(Index index) {
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

    double getLastDayIndexPrice(Index index) {
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

    Date getDate1MonthPrior(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, -1);
        return cal.getTime();
    }

    Date getDate1WeekPrior(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, -7);
        return cal.getTime();
    }

    long convertDateToEpoch(Date date) {
        return date.getTime() / 1000;
    }

    private double getMoneyInvestedInStock(Stock stock, double currentPrice) {
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
                    stocks.add(new SearchedStock(name, displayName, isUS, cid));
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
}
