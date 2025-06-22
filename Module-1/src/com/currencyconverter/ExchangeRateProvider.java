package com.currencyconverter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ExchangeRateProvider {
    private final Map<String, Double> rates;

    public ExchangeRateProvider() {
        rates = new HashMap<>();
        rates.put("USD", 1.0);    // Base currency
        rates.put("EUR", 0.92);
        rates.put("GBP", 0.79);
        rates.put("INR", 83.2);
        rates.put("JPY", 157.0);
    }

    public double getRate(String currency) {
        if (!rates.containsKey(currency)) {
            throw new IllegalArgumentException("Unsupported currency: " + currency);
        }
        return rates.get(currency);
    }

    public boolean isSupportedCurrency(String currency) {
        return rates.containsKey(currency);
    }

    public Set<String> getSupportedCurrencies() {
        return rates.keySet();
    }
} 