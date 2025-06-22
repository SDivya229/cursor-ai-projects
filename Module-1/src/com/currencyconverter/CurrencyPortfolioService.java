package com.currencyconverter;

import java.util.*;
import java.time.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing and analyzing currency portfolios.
 * Supports adding/removing/updating entries, calculating values, generating reports, and more.
 */
public class CurrencyPortfolioService {
    private final Map<String, PortfolioEntry> portfolio = new ConcurrentHashMap<>();
    private final ExchangeRateProvider rateProvider;
    private final Map<String, Double> rateCache;
    private final Map<String, LocalDateTime> cacheTimestamps;
    private final List<String> logs;
    private String currentUser;
    private final Logger logger = new Logger();

    public CurrencyPortfolioService(ExchangeRateProvider rateProvider) {
        this.rateProvider = rateProvider;
        this.rateCache = new HashMap<>();
        this.cacheTimestamps = new HashMap<>();
        this.logs = new ArrayList<>();
        this.currentUser = null;
    }

    /**
     * Authenticates a user with the given username and password.
     * <p>
     * This is a simulated authentication method. The password is hardcoded as "password" for demonstration purposes.
     *
     * @param username the username to authenticate (must not be null or empty)
     * @param password the password to authenticate (must be "password")
     * @return {@code true} if authentication is successful, {@code false} otherwise
     *
     * <h3>Usage Example:</h3>
     * <pre>{@code
     * CurrencyPortfolioService service = new CurrencyPortfolioService(new ExchangeRateProvider());
     * boolean loggedIn = service.login("alice", "password");
     * }</pre>
     *
     * <h3>Error Conditions:</h3>
     * <ul>
     *   <li>If username is null or empty, authentication fails.</li>
     *   <li>If password is not "password", authentication fails.</li>
     * </ul>
     */
    public boolean login(String username, String password) {
        // Simulate authentication
        if (username != null && !username.isEmpty() && password.equals("password")) {
            this.currentUser = username;
            log("User '" + username + "' logged in.");
            return true;
        }
        log("Failed login attempt for user '" + username + "'.");
        return false;
    }

    /**
     * Logs out the current user, if any.
     * <p>
     * After calling this method, the user will no longer be authenticated and must log in again to perform portfolio operations.
     *
     * <h3>Usage Example:</h3>
     * <pre>{@code
     * service.logout();
     * }</pre>
     */
    public void logout() {
        log("User '" + currentUser + "' logged out.");
        this.currentUser = null;
    }

    /**
     * Checks if a user is currently authenticated.
     *
     * @return {@code true} if a user is authenticated, {@code false} otherwise
     *
     * <h3>Usage Example:</h3>
     * <pre>{@code
     * if (service.isAuthenticated()) {
     *     // perform actions
     * }
     * }</pre>
     */
    public boolean isAuthenticated() {
        return currentUser != null;
    }

    /**
     * Adds a new entry to the portfolio for the specified currency and amount.
     *
     * @param currency the ISO currency code (e.g., "USD", "EUR") to add
     * @param amount the amount to add for the currency
     *
     * @throws SecurityException if the user is not authenticated
     * @throws IllegalArgumentException if the currency is not supported or already exists in the portfolio
     *
     * <h3>Usage Example:</h3>
     * <pre>{@code
     * service.addEntry("USD", 1000);
     * }</pre>
     */
    public void addEntry(String currency, double amount) {
        requireAuth();
        validateCurrency(currency);
        if (portfolio.containsKey(currency)) {
            throw new IllegalArgumentException("Entry for currency already exists. Use updateEntry.");
        }
        portfolio.put(currency, new PortfolioEntry(currency, amount));
        log("Added entry: " + amount + " " + currency);
    }

    /**
     * Updates the amount for an existing currency entry in the portfolio.
     *
     * @param currency the ISO currency code to update
     * @param amount the new amount for the currency
     *
     * @throws SecurityException if the user is not authenticated
     * @throws IllegalArgumentException if the currency is not supported or does not exist in the portfolio
     *
     * <h3>Usage Example:</h3>
     * <pre>{@code
     * service.updateEntry("USD", 2000);
     * }</pre>
     */
    public void updateEntry(String currency, double amount) {
        requireAuth();
        validateCurrency(currency);
        if (!portfolio.containsKey(currency)) {
            throw new IllegalArgumentException("No entry for currency. Use addEntry.");
        }
        portfolio.get(currency).setAmount(amount);
        log("Updated entry: " + amount + " " + currency);
    }

    /**
     * Removes an entry for the specified currency from the portfolio.
     *
     * @param currency the ISO currency code to remove
     *
     * @throws SecurityException if the user is not authenticated
     * @throws IllegalArgumentException if the currency is not supported or does not exist in the portfolio
     *
     * <h3>Usage Example:</h3>
     * <pre>{@code
     * service.removeEntry("USD");
     * }</pre>
     */
    public void removeEntry(String currency) {
        requireAuth();
        validateCurrency(currency);
        if (!portfolio.containsKey(currency)) {
            throw new IllegalArgumentException("No entry for currency.");
        }
        portfolio.remove(currency);
        log("Removed entry for currency: " + currency);
    }

    /**
     * Retrieves the portfolio entry for the specified currency.
     *
     * @param currency the ISO currency code to retrieve
     * @return the {@link PortfolioEntry} for the specified currency, or {@code null} if not found
     *
     * @throws SecurityException if the user is not authenticated
     * @throws IllegalArgumentException if the currency is not supported
     *
     * <h3>Usage Example:</h3>
     * <pre>{@code
     * PortfolioEntry entry = service.getEntry("USD");
     * }</pre>
     */
    public PortfolioEntry getEntry(String currency) {
        requireAuth();
        validateCurrency(currency);
        return portfolio.get(currency);
    }

    /**
     * Retrieves all portfolio entries for the current user.
     *
     * @return a list of all {@link PortfolioEntry} objects in the portfolio
     *
     * @throws SecurityException if the user is not authenticated
     *
     * <h3>Usage Example:</h3>
     * <pre>{@code
     * List<PortfolioEntry> entries = service.getAllEntries();
     * }</pre>
     */
    public List<PortfolioEntry> getAllEntries() {
        requireAuth();
        return new ArrayList<>(portfolio.values());
    }

    /**
     * Removes all entries from the current user's portfolio.
     *
     * @throws SecurityException if the user is not authenticated
     *
     * <h3>Usage Example:</h3>
     * <pre>{@code
     * service.clearPortfolio();
     * }</pre>
     */
    public void clearPortfolio() {
        requireAuth();
        portfolio.clear();
        log("Cleared portfolio.");
    }

    // --- Exchange Rate Caching ---
    private static final int CACHE_MINUTES = 10;

    /**
     * Retrieves the exchange rate from one currency to another, using a cache for efficiency.
     * <p>
     * Rates are cached for 10 minutes per currency pair. If the cache is expired or missing, a new rate is fetched.
     *
     * @param from the source ISO currency code
     * @param to the target ISO currency code
     * @return the exchange rate from {@code from} to {@code to}
     *
     * <h3>Usage Example:</h3>
     * <pre>{@code
     * double rate = service.getRate("USD", "EUR");
     * }</pre>
     */
    public double getRate(String from, String to) {
        String key = from + ":" + to;
        LocalDateTime now = LocalDateTime.now();
        if (rateCache.containsKey(key) && cacheTimestamps.containsKey(key)) {
            LocalDateTime cachedAt = cacheTimestamps.get(key);
            if (Duration.between(cachedAt, now).toMinutes() < CACHE_MINUTES) {
                log("Using cached rate for " + key);
                return rateCache.get(key);
            }
        }
        // Simulate fetching real-time rate
        double rate = fetchRate(from, to);
        rateCache.put(key, rate);
        cacheTimestamps.put(key, now);
        log("Fetched new rate for " + key + ": " + rate);
        return rate;
    }

    private double fetchRate(String from, String to) {
        // For now, use static rates from rateProvider
        double fromRate = rateProvider.getRate(from);
        double toRate = rateProvider.getRate(to);
        return toRate / fromRate;
    }

    // --- Portfolio Value Calculations ---
    /**
     * Calculates the total value of the current user's currency portfolio in a specified target currency.
     * <p>
     * This method iterates over all portfolio entries, converts each entry's amount from its original currency
     * to the target currency using the latest (or cached) exchange rates, and sums the results to produce the total.
     * The user must be authenticated to call this method.
     *
     * @param targetCurrency the ISO currency code (e.g., "USD", "EUR", "JPY") to which all portfolio entries will be converted.
     *                      Must be a supported currency as defined by the {@link ExchangeRateProvider}.
     * @return the total value of the portfolio in the target currency, as a {@code double}.
     *
     * @throws SecurityException if the user is not authenticated.
     * @throws IllegalArgumentException if {@code targetCurrency} is not supported.
     *
     * <h3>Usage Example:</h3>
     * <pre>{@code
     * CurrencyPortfolioService service = new CurrencyPortfolioService(new ExchangeRateProvider());
     * service.login("alice", "password");
     * service.addEntry("USD", 1000);
     * service.addEntry("EUR", 500);
     * double totalInJpy = service.getTotalValue("JPY");
     * System.out.println("Total in JPY: " + totalInJpy);
     * }</pre>
     *
     * <h3>Error Conditions:</h3>
     * <ul>
     *   <li>If the user is not authenticated, a {@code SecurityException} is thrown.</li>
     *   <li>If the target currency is not supported, an {@code IllegalArgumentException} is thrown.</li>
     *   <li>If any portfolio entry's currency is not supported, an {@code IllegalArgumentException} is thrown.</li>
     * </ul>
     *
     * <h3>Performance Notes:</h3>
     * <ul>
     *   <li>Exchange rates are cached for 10 minutes per currency pair to improve performance and reduce redundant calculations.</li>
     *   <li>For large portfolios or frequent calls with many different currency pairs, memory usage may increase due to caching.</li>
     *   <li>This method is thread-safe for portfolio access, but the underlying {@link ExchangeRateProvider} should also be thread-safe if used concurrently.</li>
     * </ul>
     */
    public double getTotalValue(String targetCurrency) {
        requireAuth();
        double total = 0.0;
        for (PortfolioEntry entry : portfolio.values()) {
            double rate = getRate(entry.getCurrency(), targetCurrency);
            total += entry.getAmount() * rate;
        }
        log("Calculated total value in " + targetCurrency + ": " + total);
        return total;
    }

    /**
     * Provides a breakdown of the portfolio's value by each currency, converted to the target currency.
     *
     * @param targetCurrency the ISO currency code to which all values will be converted
     * @return a map where each key is a currency code from the portfolio and each value is the amount in the target currency
     *
     * @throws SecurityException if the user is not authenticated
     * @throws IllegalArgumentException if the target currency is not supported
     *
     * <h3>Usage Example:</h3>
     * <pre>{@code
     * Map<String, Double> breakdown = service.getBreakdown("USD");
     * }</pre>
     */
    public Map<String, Double> getBreakdown(String targetCurrency) {
        requireAuth();
        Map<String, Double> breakdown = new HashMap<>();
        for (PortfolioEntry entry : portfolio.values()) {
            double rate = getRate(entry.getCurrency(), targetCurrency);
            breakdown.put(entry.getCurrency(), entry.getAmount() * rate);
        }
        log("Generated breakdown in " + targetCurrency);
        return breakdown;
    }

    // --- Reporting ---
    /**
     * Generates a summary report of the portfolio in the specified target currency.
     *
     * @param targetCurrency the ISO currency code for the report
     * @return a formatted string containing the summary report
     *
     * @throws SecurityException if the user is not authenticated
     * @throws IllegalArgumentException if the target currency is not supported
     *
     * <h3>Usage Example:</h3>
     * <pre>{@code
     * String report = service.generateSummaryReport("EUR");
     * System.out.println(report);
     * }</pre>
     */
    public String generateSummaryReport(String targetCurrency) {
        requireAuth();
        StringBuilder sb = new StringBuilder();
        sb.append("Portfolio Summary for user: ").append(currentUser).append("\n");
        sb.append("Target currency: ").append(targetCurrency).append("\n");
        sb.append("Total value: ").append(getTotalValue(targetCurrency)).append(" ").append(targetCurrency).append("\n");
        sb.append("Breakdown:\n");
        Map<String, Double> breakdown = getBreakdown(targetCurrency);
        for (Map.Entry<String, Double> entry : breakdown.entrySet()) {
            sb.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append(" ").append(targetCurrency).append("\n");
        }
        log("Generated summary report in " + targetCurrency);
        return sb.toString();
    }

    /**
     * Generates a simulated historical report for the portfolio, showing approximate values as of a given number of days ago.
     *
     * @param targetCurrency the ISO currency code for the report
     * @param daysAgo the number of days in the past to simulate
     * @return a formatted string containing the historical simulation report
     *
     * @throws SecurityException if the user is not authenticated
     * @throws IllegalArgumentException if the target currency is not supported
     *
     * <h3>Usage Example:</h3>
     * <pre>{@code
     * String historical = service.generateHistoricalSimulation("USD", 30);
     * System.out.println(historical);
     * }</pre>
     */
    public String generateHistoricalSimulation(String targetCurrency, int daysAgo) {
        requireAuth();
        // Simulate historical rates by applying a random factor
        Random rand = new Random(daysAgo);
        StringBuilder sb = new StringBuilder();
        sb.append("Historical Simulation (approximate) for ").append(daysAgo).append(" days ago\n");
        double total = 0.0;
        for (PortfolioEntry entry : portfolio.values()) {
            double rate = getRate(entry.getCurrency(), targetCurrency);
            double factor = 0.95 + 0.1 * rand.nextDouble(); // Simulate +/-5% fluctuation
            double historicalValue = entry.getAmount() * rate * factor;
            sb.append(entry.getCurrency()).append(": ").append(historicalValue).append(" ").append(targetCurrency).append("\n");
            total += historicalValue;
        }
        sb.append("Total (simulated): ").append(total).append(" ").append(targetCurrency).append("\n");
        log("Generated historical simulation for " + daysAgo + " days ago in " + targetCurrency);
        return sb.toString();
    }

    // --- Logging ---
    private void log(String message) {
        logger.log(message);
    }

    /**
     * Returns a list of all log messages generated by the service.
     *
     * @return a list of log messages as strings
     *
     * <h3>Usage Example:</h3>
     * <pre>{@code
     * List<String> logs = service.getLogs();
     * logs.forEach(System.out::println);
     * }</pre>
     */
    public List<String> getLogs() {
        return new ArrayList<>(logs);
    }

    // --- Validation and Auth Helpers ---
    private void requireAuth() {
        if (!isAuthenticated()) {
            throw new SecurityException("User not authenticated.");
        }
    }

    private void validateCurrency(String currency) {
        if (!rateProvider.isSupportedCurrency(currency)) {
            throw new IllegalArgumentException("Unsupported currency: " + currency);
        }
    }
} 