package com.currencyconverter;

import java.util.Scanner;

public class CurrencyConverterApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ExchangeRateProvider rateProvider = new ExchangeRateProvider();
        CurrencyConverter converter = new CurrencyConverter(rateProvider);

        System.out.println("=== Currency Converter ===");
        System.out.print("Enter amount: ");
        String amountInput = scanner.nextLine();
        double amount;
        try {
            amount = Double.parseDouble(amountInput);
            if (amount < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a positive number.");
            return;
        }

        System.out.print("Enter source currency (USD, EUR, GBP, INR, JPY): ");
        String fromCurrency = scanner.nextLine().trim().toUpperCase();
        System.out.print("Enter target currency (USD, EUR, GBP, INR, JPY): ");
        String toCurrency = scanner.nextLine().trim().toUpperCase();

        if (!rateProvider.isSupportedCurrency(fromCurrency) || !rateProvider.isSupportedCurrency(toCurrency)) {
            System.out.println("Unsupported currency code.");
            return;
        }

        double result = converter.convert(amount, fromCurrency, toCurrency);
        System.out.printf("%.2f %s = %.2f %s\n", amount, fromCurrency, result, toCurrency);
    }
} 