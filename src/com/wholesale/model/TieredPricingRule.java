package com.wholesale.model;

/**
 * Defines a pricing tier for bulk ordering discounts.
 * E.g., for orders >= minQuantity and <= maxQuantity, a specific discount percentage applies.
 */
public class TieredPricingRule {
    private int minQuantity;
    private int maxQuantity; // Integer.MAX_VALUE for unbounded upper tier
    private double discountPercent; // e.g., 10.0 for 10%

    public TieredPricingRule(int minQuantity, int maxQuantity, double discountPercent) {
        this.minQuantity = minQuantity;
        this.maxQuantity = maxQuantity;
        this.discountPercent = discountPercent;
    }

    public boolean appliesTo(int quantity) {
        return quantity >= minQuantity && quantity <= maxQuantity;
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public String getTierDescription() {
        if (maxQuantity == Integer.MAX_VALUE) {
            return minQuantity + "+ units: " + discountPercent + "% OFF";
        } else {
            return minQuantity + "-" + maxQuantity + " units: " + discountPercent + "% OFF";
        }
    }
}
