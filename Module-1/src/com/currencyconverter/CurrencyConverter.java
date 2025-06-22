package com.currencyconverter;

import java.util.HashMap;
import java.util.Map;

public class CurrencyConverter {
    private final ExchangeRateProvider rateProvider;

    public CurrencyConverter(ExchangeRateProvider rateProvider) {
        this.rateProvider = rateProvider;
    }

    public double convert(double amount, String fromCurrency, String toCurrency) {
        double fromRate = rateProvider.getRate(fromCurrency);
        double toRate = rateProvider.getRate(toCurrency);
        return amount / fromRate * toRate;
    }

    public Map<String, Double> getDollarEquivalents(double amount) {
        Map<String, Double> equivalents = new HashMap<>();
        for (String currency : rateProvider.getSupportedCurrencies()) {
            double usdEquivalent = convert(amount, currency, "USD");
            equivalents.put(currency, usdEquivalent);
        }
        return equivalents;
    }
} 