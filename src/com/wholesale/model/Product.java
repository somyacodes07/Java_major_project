package com.wholesale.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a wholesale product available for bulk purchase by retailers.
 * Demonstrates: Encapsulation, ArrayList usage for tiered pricing rules, and business calculation logic.
 */
public class Product {
    private String productId;
    private String name;
    private String category;
    private double basePrice;
    private int stockQuantity;
    private int minOrderQuantity;
    private List<TieredPricingRule> pricingTiers;

    public Product(String productId, String name, String category, double basePrice,
                   int stockQuantity, int minOrderQuantity) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.basePrice = basePrice;
        this.stockQuantity = stockQuantity;
        this.minOrderQuantity = minOrderQuantity;
        this.pricingTiers = new ArrayList<>();
        setupDefaultTiers();
    }

    /**
     * Standard wholesale industry tiered bulk discount:
     * - 1 to 49 units: 0% discount
     * - 50 to 199 units: 10% discount
     * - 200 to 499 units: 18% discount
     * - 500+ units: 25% discount
     */
    private void setupDefaultTiers() {
        pricingTiers.add(new TieredPricingRule(1, 49, 0.0));
        pricingTiers.add(new TieredPricingRule(50, 199, 10.0));
        pricingTiers.add(new TieredPricingRule(200, 499, 18.0));
        pricingTiers.add(new TieredPricingRule(500, Integer.MAX_VALUE, 25.0));
    }

    public double getDiscountPercent(int quantity) {
        for (TieredPricingRule rule : pricingTiers) {
            if (rule.appliesTo(quantity)) {
                return rule.getDiscountPercent();
            }
        }
        return 0.0;
    }

    /**
     * Calculates the unit price after applying the tiered volume discount.
     */
    public double getEffectiveUnitPrice(int quantity) {
        double discount = getDiscountPercent(quantity);
        return basePrice * (1.0 - (discount / 100.0));
    }

    public String getTierSummary() {
        StringBuilder sb = new StringBuilder();
        for (TieredPricingRule rule : pricingTiers) {
            if (sb.length() > 0) sb.append(" | ");
            sb.append(rule.getTierDescription());
        }
        return sb.toString();
    }

    // Getters and Setters
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public int getMinOrderQuantity() {
        return minOrderQuantity;
    }

    public void setMinOrderQuantity(int minOrderQuantity) {
        this.minOrderQuantity = minOrderQuantity;
    }

    public List<TieredPricingRule> getPricingTiers() {
        return pricingTiers;
    }

    public void reduceStock(int quantity) {
        this.stockQuantity -= quantity;
    }

    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    @Override
    public String toString() {
        return productId + " - " + name + " (₹" + String.format("%.2f", basePrice) + " / unit)";
    }
}
