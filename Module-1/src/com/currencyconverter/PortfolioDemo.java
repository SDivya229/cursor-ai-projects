package com.currencyconverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PortfolioDemo {
    public static void main(String[] args) {
        ExchangeRateProvider rateProvider = new ExchangeRateProvider();
        PortfolioConverter portfolioConverter = new PortfolioConverter(rateProvider);

        // Example portfolio: 100 USD, 200 EUR, 5000 JPY
        List<PortfolioEntry> portfolio = new ArrayList<>();
        portfolio.add(new PortfolioEntry("USD", 100));
        portfolio.add(new PortfolioEntry("EUR", 200));
        portfolio.add(new PortfolioEntry("JPY", 5000));
        portfolio.add(new PortfolioEntry("INR", 10000));

        String targetCurrency = "USD";
        double total = portfolioConverter.getTotalValueInTarget(portfolio, targetCurrency);
        System.out.printf("Total portfolio value in %s: %.2f\n", targetCurrency, total);

        Map<String, Double> breakdown = portfolioConverter.getBreakdownInTarget(portfolio, targetCurrency);
        System.out.println("Breakdown by currency:");
        for (Map.Entry<String, Double> entry : breakdown.entrySet()) {
            System.out.printf("%s: %.2f %s\n", entry.getKey(), entry.getValue(), targetCurrency);
        }
    }
} 