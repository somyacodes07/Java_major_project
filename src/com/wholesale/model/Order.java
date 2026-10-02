package com.wholesale.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a wholesale bulk order placed by a retailer.
 * Demonstrates: ArrayList composition for OrderItems, state management with OrderStatus,
 * and comprehensive accounting calculations.
 */
public class Order {
    private String orderId;
    private Retailer retailer;
    private List<OrderItem> items;
    private String orderDate;
    private OrderStatus status;
    private double taxRate; // e.g., 0.05 for 5% wholesale GST
    private String deliveryNotes;

    public Order(String orderId, Retailer retailer, double taxRate) {
        this.orderId = orderId;
        this.retailer = retailer;
        this.items = new ArrayList<>();
        this.taxRate = taxRate;
        this.status = OrderStatus.PLACED;
        this.orderDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        this.deliveryNotes = "Order placed successfully. Awaiting warehouse packing.";
    }

    public void addItem(OrderItem item) {
        items.add(item);
    }

    public double getSubtotal() {
        double sum = 0.0;
        for (OrderItem item : items) {
            sum += item.getLineTotal();
        }
        return sum;
    }

    public double getTotalSavings() {
        double sum = 0.0;
        for (OrderItem item : items) {
            sum += item.getSavings();
        }
        return sum;
    }

    public double getOriginalTotalWithoutDiscount() {
        double sum = 0.0;
        for (OrderItem item : items) {
            sum += item.getOriginalTotalWithoutDiscount();
        }
        return sum;
    }

    public double getTaxAmount() {
        return getSubtotal() * taxRate;
    }

    public double getFinalTotal() {
        return getSubtotal() + getTaxAmount();
    }

    public int getTotalUnits() {
        int count = 0;
        for (OrderItem item : items) {
            count += item.getQuantity();
        }
        return count;
    }

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public Retailer getRetailer() {
        return retailer;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public String getDeliveryNotes() {
        return deliveryNotes;
    }

    public void setDeliveryNotes(String deliveryNotes) {
        this.deliveryNotes = deliveryNotes;
    }
}
