package com.wholesale.test;

import com.wholesale.exception.BusinessRuleException;
import com.wholesale.exception.InsufficientStockException;
import com.wholesale.model.*;
import com.wholesale.service.DataRepository;
import com.wholesale.service.OrderService;

import java.util.ArrayList;
import java.util.List;

/**
 * Automated test suite to verify core business logic, tiered pricing, and exception handling.
 */
public class WholesaleSystemTest {
    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("  RUNNING AUTOMATED TEST SUITE: WHOLESALE ORDER PROCESSING");
        System.out.println("==========================================================");

        int passed = 0;
        int failed = 0;

        // Test 1: Tiered Pricing Calculation
        try {
            Product p = new Product("TEST-01", "Test Product", "Test Category", 1000.0, 1000, 10);
            assertDoubleEquals("Tier 1 (10 units - 0% discount)", 1000.0, p.getEffectiveUnitPrice(10));
            assertDoubleEquals("Tier 2 (60 units - 10% discount)", 900.0, p.getEffectiveUnitPrice(60));
            assertDoubleEquals("Tier 3 (250 units - 18% discount)", 820.0, p.getEffectiveUnitPrice(250));
            assertDoubleEquals("Tier 4 (600 units - 25% discount)", 750.0, p.getEffectiveUnitPrice(600));
            System.out.println("✔ Test 1: Tiered Pricing Rules PASSED");
            passed++;
        } catch (Throwable t) {
            System.err.println("✘ Test 1 FAILED: " + t.getMessage());
            failed++;
        }

        // Test 2: HashMap O(1) Key-Based Lookup
        try {
            DataRepository repo = DataRepository.getInstance();
            Retailer r = repo.getRetailer("RET-001");
            if (r == null || !r.getBusinessName().equals("Apex Supermarts Ltd")) {
                throw new RuntimeException("Retailer lookup by ID failed!");
            }
            Product prod = repo.getProduct("PRD-101");
            if (prod == null || !prod.getName().contains("Basmati Rice")) {
                throw new RuntimeException("Product lookup by ID failed!");
            }
            System.out.println("✔ Test 2: HashMap Key-based Lookup PASSED");
            passed++;
        } catch (Throwable t) {
            System.err.println("✘ Test 2 FAILED: " + t.getMessage());
            failed++;
        }

        // Test 3: Insufficient Stock Exception Handling
        try {
            OrderService service = new OrderService();
            DataRepository repo = DataRepository.getInstance();
            Retailer r = repo.getRetailer("RET-001");
            Product prod = repo.getProduct("PRD-101");

            List<OrderItem> items = new ArrayList<>();
            items.add(new OrderItem(prod, prod.getStockQuantity() + 500)); // Excess quantity

            boolean caught = false;
            try {
                service.placeOrder(r, items, 0.05);
            } catch (InsufficientStockException ex) {
                caught = true;
            }

            if (!caught) {
                throw new RuntimeException("Expected InsufficientStockException was not thrown!");
            }
            System.out.println("✔ Test 3: Insufficient Stock Exception Handling PASSED");
            passed++;
        } catch (Throwable t) {
            System.err.println("✘ Test 3 FAILED: " + t.getMessage());
            failed++;
        }

        // Test 4: Credit Limit Violation Handling
        try {
            OrderService service = new OrderService();
            DataRepository repo = DataRepository.getInstance();
            Retailer lowCreditRetailer = new Retailer("TEST-RET", "Small Shop", "Owner", "12345", "test@shop.com", "City", 100.0);
            repo.addRetailer(lowCreditRetailer);

            Product prod = repo.getProduct("PRD-103"); // Price 3200
            List<OrderItem> items = new ArrayList<>();
            items.add(new OrderItem(prod, 15)); // Exceeds 100 credit limit

            boolean caught = false;
            try {
                service.placeOrder(lowCreditRetailer, items, 0.05);
            } catch (BusinessRuleException ex) {
                if (ex.getMessage().contains("Credit limit exceeded")) {
                    caught = true;
                }
            }

            if (!caught) {
                throw new RuntimeException("Expected credit limit BusinessRuleException was not thrown!");
            }
            System.out.println("✔ Test 4: Retailer Credit Limit Enforcement PASSED");
            passed++;
        } catch (Throwable t) {
            System.err.println("✘ Test 4 FAILED: " + t.getMessage());
            failed++;
        }

        // Test 5: Successful Order Placement & Inventory Deduction
        try {
            OrderService service = new OrderService();
            DataRepository repo = DataRepository.getInstance();
            Retailer r = repo.getRetailer("RET-003");
            Product prod = repo.getProduct("PRD-106");

            int initialStock = prod.getStockQuantity();
            int orderQty = 15;

            List<OrderItem> items = new ArrayList<>();
            items.add(new OrderItem(prod, orderQty));

            Order placedOrder = service.placeOrder(r, items, 0.05);
            if (placedOrder == null) {
                throw new RuntimeException("Order was not returned!");
            }

            // Verify stock reduced
            if (prod.getStockQuantity() != initialStock - orderQty) {
                throw new RuntimeException("Stock was not deducted correctly!");
            }

            // Verify invoice created
            Invoice inv = repo.getInvoiceByOrderId(placedOrder.getOrderId());
            if (inv == null) {
                throw new RuntimeException("Invoice was not generated for order!");
            }

            System.out.println("✔ Test 5: Order Placement, Stock Deduction & Invoice Creation PASSED");
            passed++;
        } catch (Throwable t) {
            System.err.println("✘ Test 5 FAILED: " + t.getMessage());
            failed++;
        }

        System.out.println("==========================================================");
        System.out.printf("  TEST RESULTS: %d Passed, %d Failed\n", passed, failed);
        System.out.println("==========================================================");

        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void assertDoubleEquals(String label, double expected, double actual) {
        if (Math.abs(expected - actual) > 0.001) {
            throw new RuntimeException(String.format("%s: Expected %.2f, Got %.2f", label, expected, actual));
        }
    }
}
