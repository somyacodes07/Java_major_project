package com.wholesale.model;

/**
 * Represents the lifecycle stages of a wholesale bulk order.
 */
public enum OrderStatus {
    PLACED("Placed"),
    PROCESSING("In Processing"),
    DISPATCHED("Dispatched"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
