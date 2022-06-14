package com.example.portfoliosOverview.webScraper;

import com.example.portfoliosOverview.models.Index;
import com.example.portfoliosOverview.models.Portfolio;
import com.example.portfoliosOverview.models.Stock;
import com.example.portfoliosOverview.repositories.IndexRepository;
import com.example.portfoliosOverview.repositories.PortfolioRepository;
import com.example.portfoliosOverview.repositories.StockRepository;
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
    StockRepository stockRepository;
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
        double percentChangePortfolio1Month = getPercentChangePortfolio1Month(portfolio);
        double percentChangePortfolio1Week = getPercentChangePortfolio1Week(portfolio);
        double percentChangePortfolio1Day = getPercentChangePortfolio1Day(portfolio);
        portfolioRepository.update(portfolio.getId(), percentChangePortfolio1Month, percentChangePortfolio1Week, percentChangePortfolio1Day);

        for (Stock stock : portfolio.getStocks()) {
            double totalMoneyInvested = getTotalMoneyInvested(portfolio);
            double currentPrice = getCurrentStockPrice(stock);
            double moneyInvestedInStock = stock.getAmountOfShares() * currentPrice;
            double percentOfPortfolio = getPercentOfPortfolio(moneyInvestedInStock, totalMoneyInvested);
            stockRepository.updatePercentOfPortfolio(stock.getId(), percentOfPortfolio);
        }
    }

    public void updateStockInPortfolio(Stock stock) {
        double percentChange1Month = getPercentChangeStock1Month(stock);
        double percentChange1Week = getPercentChangeStock1Week(stock);
        double percentChange1Day = getPercentChangeStock1Day(stock);
        stockRepository.update(stock.getId(), percentChange1Month, percentChange1Week, percentChange1Day);
    }

    public void updateIndex(Index index) {
        double percentChangeIndex1Month = getPercentChangeIndex1Month(index);
        double percentChangeIndex1Week = getPercentChangeIndex1Week(index);
        double percentChangeIndex1Day = getPercentChangeIndex1Day(index);
        indexRepository.update(index.getId(), percentChangeIndex1Month, percentChangeIndex1Week, percentChangeIndex1Day);
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
            double currentPrice = getCurrentStockPrice(stock);
            double moneyInvested = stock.getAmountOfShares() * currentPrice;
            if (stock.isUS()) {
                moneyInvested *= usDollarConversion;
            }
            totalMoneyInvested += moneyInvested;
        }
        return totalMoneyInvested;
    }

    double getPercentChangePortfolio1Month(Portfolio portfolio) {
        double percentChangePortfolio = 0;
        double totalMoneyInvested = getTotalMoneyInvested(portfolio);
        List<Stock> stocks = portfolio.getStocks();

        for (Stock stock : stocks) {
            double currentPrice = getCurrentStockPrice(stock);
            double moneyInvestedInStock = stock.getAmountOfShares() * currentPrice;
            if (stock.isUS()) {
                moneyInvestedInStock *= usDollarConversion;
            }
            double percentChange = getPercentChangeStock1Month(stock);
            double percentOfPortfolio = getPercentOfPortfolio(moneyInvestedInStock, totalMoneyInvested);
            percentChangePortfolio += percentChange * (percentOfPortfolio / 100);
            percentChangePortfolio = (double) Math.round(percentChangePortfolio * 100.0) / 100.0;
        }
        return percentChangePortfolio;
    }

    double getPercentChangePortfolio1Week(Portfolio portfolio) {
        double percentChangePortfolio = 0;
        double totalMoneyInvested = getTotalMoneyInvested(portfolio);
        List<Stock> stocks = portfolio.getStocks();

        for (Stock stock : stocks) {
            double currentPrice = getCurrentStockPrice(stock);
            double moneyInvestedInStock = stock.getAmountOfShares() * currentPrice;
            if (stock.isUS()) {
                moneyInvestedInStock *= usDollarConversion;
            }
            double percentChange = getPercentChangeStock1Week(stock);
            double percentOfPortfolio = getPercentOfPortfolio(moneyInvestedInStock, totalMoneyInvested);
            percentChangePortfolio += percentChange * (percentOfPortfolio / 100);
            percentChangePortfolio = (double) Math.round(percentChangePortfolio * 100.0) / 100.0;
        }
        return percentChangePortfolio;
    }

    double getPercentChangePortfolio1Day(Portfolio portfolio) {
        double percentChangePortfolio = 0;
        double totalMoneyInvested = getTotalMoneyInvested(portfolio);
        List<Stock> stocks = portfolio.getStocks();

        for (Stock stock : stocks) {
            double currentPrice = getCurrentStockPrice(stock);
            double moneyInvestedInStock = stock.getAmountOfShares() * currentPrice;
            if (stock.isUS()) {
                moneyInvestedInStock *= usDollarConversion;
            }
            double percentChange = getPercentChangeStock1Day(stock);
            double percentOfPortfolio = getPercentOfPortfolio(moneyInvestedInStock, totalMoneyInvested);
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

}
