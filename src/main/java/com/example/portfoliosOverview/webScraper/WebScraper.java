package com.example.portfoliosOverview.webScraper;

import com.example.portfoliosOverview.models.Portfolio;
import com.example.portfoliosOverview.models.Stock;
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
import java.util.List;

@Service
@EnableScheduling
@Transactional
public class WebScraper {

    @Autowired
    PortfolioRepository portfolioRepository;
    @Autowired
    StockRepository stockRepository;

    double usDollarConversion = 8.43;

    @Scheduled(cron = "0 0 * * * 1-5")
    public void main() {
        List<Portfolio> portfolios = getPortfolios();
        for (int i = 0; i < portfolios.size(); i++) {
            Portfolio portfolio = portfolios.get(i);
            List<Stock> stocks = portfolio.getStocks();
            double percentChangePortfolio = getPercentChange(portfolio);
            portfolioRepository.update(portfolio.getId(), percentChangePortfolio);

            for (int j = 0; j < stocks.size(); j++) {
                Stock stock = stocks.get(j);
                double percentChangeStock = getPercentChange(stock);
                double totalMoneyInvested = getTotalMoneyInvested(portfolio);
                double currentPrice = getCurrentStockPrice(stock);
                double moneyInvestedInStock = stock.getAmountOfShares() * currentPrice;
                if (stock.isUS()) {
                    moneyInvestedInStock *= usDollarConversion;
                }
                double percentOfPortfolio = getPercentOfPortfolio(moneyInvestedInStock, totalMoneyInvested);
                stockRepository.update(stock.getId(), percentChangeStock, percentOfPortfolio);
            }
        }
    }

    List<Portfolio> getPortfolios() {
        return portfolioRepository.findAll();
    }

    double getPercentChange(Stock stock) {
        double currentPrice = getCurrentStockPrice(stock);
        double lastMonthPrice = getLastMonthStockPrice(stock);
        double percentChange = (currentPrice / lastMonthPrice - 1) * 100;
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

        for (int j = 0; j < stocks.size(); j++) {
            Stock stock = stocks.get(j);
            double currentPrice = getCurrentStockPrice(stock);
            double moneyInvested = stock.getAmountOfShares() * currentPrice;
            if (stock.isUS()) {
                moneyInvested *= usDollarConversion;
            }
            totalMoneyInvested += moneyInvested;
        }
        return totalMoneyInvested;
    }

    double getPercentChange(Portfolio portfolio) {
        double percentChangePortfolio = 0;
        double totalMoneyInvested = getTotalMoneyInvested(portfolio);
        List<Stock> stocks = portfolio.getStocks();

        for (int j = 0; j < stocks.size(); j++) {
            Stock stock = stocks.get(j);
            double currentPrice = getCurrentStockPrice(stock);
            double moneyInvestedInStock = stock.getAmountOfShares() * currentPrice;
            if (stock.isUS()) {
                moneyInvestedInStock *= usDollarConversion;
            }
            double percentChange = getPercentChange(stock);
            double percentOfPortfolio = getPercentOfPortfolio(moneyInvestedInStock, totalMoneyInvested);
            percentChangePortfolio += percentChange * (percentOfPortfolio / 100);
            percentChangePortfolio = (double) Math.round(percentChangePortfolio * 100.0) / 100.0;
        }
        return percentChangePortfolio;
    }

    double getCurrentStockPrice(Stock stock) {
        String url = "https://www.investing.com/equities/" + stock.getName() + "-historical-data";
        if (stock.getCid() != null) {
            url += "?cid=" + stock.getCid();
        }

        double currentPrice = 0;

        try {
            final Document document = Jsoup.connect(url).get();
            Elements rows = document.select("table.genTbl.closedTbl.historicalTbl tr");
            currentPrice = Double.parseDouble(rows.get(1).select("td:nth-of-type(2)").text().replace(",", ""));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return currentPrice;
    }

    double getLastMonthStockPrice(Stock stock) {
        String url = "https://www.investing.com/equities/" + stock.getName() + "-historical-data";
        if (stock.getCid() != null) {
            url += "?cid=" + stock.getCid();
        }

        double lastMonthPrice = 0;

        try {
            final Document document = Jsoup.connect(url).get();
            Elements rows = document.select("table.genTbl.closedTbl.historicalTbl tr");
            int amountOfRows = rows.size();
            lastMonthPrice = Double.parseDouble(rows.get(amountOfRows - 1).select("td:nth-of-type(2)").text().replace(",", ""));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return lastMonthPrice;
    }

}
