package com.wholesale.service;

import com.wholesale.exception.BusinessRuleException;
import com.wholesale.exception.InsufficientStockException;
import com.wholesale.model.*;

import java.util.List;

/**
 * Service handling business rules, calculations, stock deduction, and order lifecycle.
 */
public class OrderService {
    private final DataRepository repository;

    public OrderService() {
        this.repository = DataRepository.getInstance();
    }

    /**
     * Validates business constraints and places a bulk order:
     * 1. Checks item count >= 1
     * 2. Checks MOQ (Minimum Order Quantity)
     * 3. Checks inventory availability
     * 4. Checks Retailer Credit Limit
     * 5. Deducts inventory and updates retailer account
     */
    public synchronized Order placeOrder(Retailer retailer, List<OrderItem> items, double taxRate)
            throws BusinessRuleException, InsufficientStockException {

        if (retailer == null) {
            throw new BusinessRuleException("Please select a valid Retailer.");
        }

        if (items == null || items.isEmpty()) {
            throw new BusinessRuleException("Order must contain at least one product item.");
        }

        // Validate MOQ and Stock for each item
        for (OrderItem item : items) {
            Product product = item.getProduct();
            if (item.getQuantity() < product.getMinOrderQuantity()) {
                throw new BusinessRuleException(String.format(
                        "Product '%s' requires a minimum bulk quantity of %d units. (Entered: %d)",
                        product.getName(), product.getMinOrderQuantity(), item.getQuantity()));
            }

            if (item.getQuantity() > product.getStockQuantity()) {
                throw new InsufficientStockException(product.getProductId(), product.getStockQuantity(), item.getQuantity());
            }
        }

        // Create temporary order to calculate financial total
        String orderId = repository.generateNextOrderId();
        Order order = new Order(orderId, retailer, taxRate);
        for (OrderItem item : items) {
            order.addItem(item);
        }

        double finalTotal = order.getFinalTotal();
        double availableCredit = retailer.getAvailableCredit();

        // Check retailer credit limit
        if (finalTotal > availableCredit) {
            throw new BusinessRuleException(String.format(
                    "Credit limit exceeded for %s!\nOrder Total: ₹%.2f | Available Credit: ₹%.2f (Limit: ₹%.2f)",
                    retailer.getBusinessName(), finalTotal, availableCredit, retailer.getCreditLimit()));
        }

        // Deduct inventory
        for (OrderItem item : items) {
            Product product = item.getProduct();
            product.reduceStock(item.getQuantity());
            repository.updateProduct(product);
        }

        // Save order and create invoice
        repository.saveOrder(order);

        return order;
    }

    /**
     * Updates order delivery status. If order is cancelled, automatically restores warehouse stock.
     */
    public synchronized void updateOrderStatus(String orderId, OrderStatus newStatus, String notes)
            throws BusinessRuleException {
        Order order = repository.getOrder(orderId);
        if (order == null) {
            throw new BusinessRuleException("Order ID '" + orderId + "' not found.");
        }

        OrderStatus currentStatus = order.getStatus();

        // If transitioning to CANCELLED from non-cancelled status, restore stock and balance
        if (newStatus == OrderStatus.CANCELLED && currentStatus != OrderStatus.CANCELLED) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                product.addStock(item.getQuantity());
                repository.updateProduct(product);
            }
            order.getRetailer().reduceBalance(order.getFinalTotal());
        }

        order.setStatus(newStatus);
        if (notes != null && !notes.trim().isEmpty()) {
            order.setDeliveryNotes(notes.trim());
        }
    }

    // Dashboard & Reporting metrics
    public double getTotalRevenue() {
        double revenue = 0.0;
        for (Order o : repository.getAllOrders()) {
            if (o.getStatus() != OrderStatus.CANCELLED) {
                revenue += o.getFinalTotal();
            }
        }
        return revenue;
    }

    public double getTotalDiscountGranted() {
        double discounts = 0.0;
        for (Order o : repository.getAllOrders()) {
            if (o.getStatus() != OrderStatus.CANCELLED) {
                discounts += o.getTotalSavings();
            }
        }
        return discounts;
    }

    public int getPendingDeliveriesCount() {
        int count = 0;
        for (Order o : repository.getAllOrders()) {
            if (o.getStatus() == OrderStatus.PLACED || o.getStatus() == OrderStatus.PROCESSING || o.getStatus() == OrderStatus.DISPATCHED) {
                count++;
            }
        }
        return count;
    }

    public int getLowStockProductsCount(int threshold) {
        int count = 0;
        for (Product p : repository.getAllProducts()) {
            if (p.getStockQuantity() <= threshold) {
                count++;
            }
        }
        return count;
    }
}
