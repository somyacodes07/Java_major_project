package com.wholesale.exception;

/**
 * Custom Exception thrown when an order request exceeds warehouse stock availability.
 */
public class InsufficientStockException extends BusinessRuleException {
    private String productId;
    private int available;
    private int requested;

    public InsufficientStockException(String productId, int available, int requested) {
        super(String.format("Insufficient stock for Product ID '%s'. Available: %d units, Requested: %d units.",
                productId, available, requested));
        this.productId = productId;
        this.available = available;
        this.requested = requested;
    }

    public String getProductId() {
        return productId;
    }

    public int getAvailable() {
        return available;
    }

    public int getRequested() {
        return requested;
    }
}
