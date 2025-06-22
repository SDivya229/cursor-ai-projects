package com.currencyconverter;

public class ExchangeRateProviderTest {
    public static void main(String[] args) {
        ExchangeRateProvider rateProvider = new ExchangeRateProvider();
        CurrencyConverter converter = new CurrencyConverter(rateProvider);

        // Test 1: USD to EUR
        double usdToEur = converter.convert(100, "USD", "EUR");
        assert Math.abs(usdToEur - 92.0) < 0.01 : "USD to EUR failed";

        // Test 2: EUR to USD
        double eurToUsd = converter.convert(92, "EUR", "USD");
        assert Math.abs(eurToUsd - 100.0) < 0.01 : "EUR to USD failed";

        // Test 3: GBP to INR
        double gbpToInr = converter.convert(10, "GBP", "INR");
        assert Math.abs(gbpToInr - (10 / 0.79 * 83.2)) < 0.01 : "GBP to INR failed";

        // Test 4: JPY to USD
        double jpyToUsd = converter.convert(157, "JPY", "USD");
        assert Math.abs(jpyToUsd - 1.0) < 0.01 : "JPY to USD failed";

        System.out.println("All tests passed.");
    }
} 