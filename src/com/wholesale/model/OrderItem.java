package com.wholesale.model;

/**
 * Represents an individual product line in a wholesale bulk order.
 * Calculates effective line totals and discount savings based on bulk volume.
 */
public class OrderItem {
    private Product product;
    private int quantity;
    private double baseUnitPrice;
    private double discountPercent;
    private double effectiveUnitPrice;
    private double lineTotal;

    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.baseUnitPrice = product.getBasePrice();
        this.discountPercent = product.getDiscountPercent(quantity);
        this.effectiveUnitPrice = product.getEffectiveUnitPrice(quantity);
        this.lineTotal = this.effectiveUnitPrice * quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getBaseUnitPrice() {
        return baseUnitPrice;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public double getEffectiveUnitPrice() {
        return effectiveUnitPrice;
    }

    public double getLineTotal() {
        return lineTotal;
    }

    public double getOriginalTotalWithoutDiscount() {
        return baseUnitPrice * quantity;
    }

    public double getSavings() {
        return getOriginalTotalWithoutDiscount() - lineTotal;
    }
}
