package com.currencyconverter;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class PortfolioConverter {
    private final ExchangeRateProvider rateProvider;

    public PortfolioConverter(ExchangeRateProvider rateProvider) {
        this.rateProvider = rateProvider;
    }

    /**
     * Calculates the total value of a portfolio in the target currency.
     * @param portfolio List of PortfolioEntry objects (must not be null)
     * @param targetCurrency The currency code to convert to (must not be null)
     * @return The total value in the target currency
     * @throws IllegalArgumentException if portfolio or targetCurrency is null
     */
    public double getTotalValueInTarget(List<PortfolioEntry> portfolio, String targetCurrency) {
        if (portfolio == null) throw new IllegalArgumentException("Portfolio cannot be null");
        if (targetCurrency == null) throw new IllegalArgumentException("Target currency cannot be null");
        double total = 0.0;
        double targetRate = rateProvider.getRate(targetCurrency); // Moved outside loop for efficiency
        for (PortfolioEntry entry : portfolio) {
            double rate = rateProvider.getRate(entry.getCurrency());
            total += convertToTarget(entry.getAmount(), rate, targetRate);
        }
        return total;
    }

    /** Helper method for conversion logic, improves readability and reusability */
    private double convertToTarget(double amount, double fromRate, double toRate) {
        return amount / fromRate * toRate;
    }

    public Map<String, Double> getBreakdownInTarget(List<PortfolioEntry> portfolio, String targetCurrency) {
        Map<String, Double> breakdown = new HashMap<>();
        for (PortfolioEntry entry : portfolio) {
            double rate = rateProvider.getRate(entry.getCurrency());
            double targetRate = rateProvider.getRate(targetCurrency);
            breakdown.put(entry.getCurrency(), entry.getAmount() / rate * targetRate);
        }
        return breakdown;
    }
} 