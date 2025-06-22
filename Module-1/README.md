# Currency Converter Java Project

## Overview
This is a simple Java application that allows users to convert an amount from one currency to another using static exchange rates. The application features a user-friendly console interface.

## Features
- Input amount to convert
- Select source and target currencies (USD, EUR, GBP, INR, JPY)
- Uses static exchange rates (easily extendable)
- Error handling for invalid inputs
- Modular code structure for easy extension
- Basic unit tests for conversion logic

## How to Run
1. Ensure you have Java (JDK 8 or higher) installed.
2. Compile the source files:
   ```
   javac -d out src/com/currencyconverter/*.java
   ```
3. Run the application:
   ```
   java -cp out com.currencyconverter.CurrencyConverterApp
   ```

## Project Structure
```
Module-1/
├── src/
│   └── com/
│       └── currencyconverter/
│           ├── CurrencyConverterApp.java
│           ├── CurrencyConverter.java
│           ├── ExchangeRateProvider.java
│           └── ExchangeRateProviderTest.java
└── README.md
```

## Extending
- To add more currencies, update the `ExchangeRateProvider` class.
- To use real-time rates, modify `ExchangeRateProvider` to fetch from an API. 