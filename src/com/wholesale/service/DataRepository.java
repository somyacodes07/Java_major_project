package com.wholesale.service;

import com.wholesale.model.*;

import java.util.*;

/**
 * DataRepository acts as the central in-memory database.
 * Demonstrates:
 * 1. HashMap: Fast key-based lookup (O(1) time complexity) for Retailers, Products, Orders, Users.
 * 2. ArrayList: Dynamic collections for table listings, filtering, and reporting.
 */
public class DataRepository {
    private static DataRepository instance;

    // Fast key-value lookups (HashMaps)
    private Map<String, Retailer> retailerMap;
    private Map<String, Product> productMap;
    private Map<String, Order> orderMap;
    private Map<String, Invoice> invoiceMap;
    private Map<String, User> userMap;

    // Dynamic collections (ArrayLists)
    private List<Retailer> retailerList;
    private List<Product> productList;
    private List<Order> orderList;

    private int orderCounter = 1000;
    private int invoiceCounter = 2000;

    private DataRepository() {
        retailerMap = new HashMap<>();
        productMap = new HashMap<>();
        orderMap = new HashMap<>();
        invoiceMap = new HashMap<>();
        userMap = new HashMap<>();

        retailerList = new ArrayList<>();
        productList = new ArrayList<>();
        orderList = new ArrayList<>();

        initializeSeedData();
    }

    public static synchronized DataRepository getInstance() {
        if (instance == null) {
            instance = new DataRepository();
        }
        return instance;
    }

    private void initializeSeedData() {
        // Users for login
        User admin = new User("admin", "admin123", "Rajesh Sharma", "Warehouse Administrator");
        User sales = new User("sales", "sales123", "Priya Verma", "Senior Sales Executive");
        userMap.put(admin.getUsername(), admin);
        userMap.put(sales.getUsername(), sales);

        // Seed Retailers
        addRetailer(new Retailer("RET-001", "Apex Supermarts Ltd", "Vikas Gupta", "9876543210", "vikas@apexmart.in", "Mumbai", 500000.0));
        addRetailer(new Retailer("RET-002", "Sunrise Daily Provisions", "Anjali Deshmukh", "9823012345", "anjali@sunriseprovisions.com", "Pune", 250000.0));
        addRetailer(new Retailer("RET-003", "Metro Tech & Electronics", "Rohan Mehta", "9819988776", "rohan@metrotech.org", "Bangalore", 1000000.0));
        addRetailer(new Retailer("RET-004", "GreenGrocer Hyperstores", "Karan Johar", "9899112233", "karan@greengrocer.in", "Delhi", 750000.0));

        // Seed Products
        addProduct(new Product("PRD-101", "Basmati Rice 25kg Master Bag", "Food & Grains", 1850.0, 800, 20));
        addProduct(new Product("PRD-102", "Refined Sunflower Oil 15L Tin", "Oils & Staples", 2150.0, 500, 15));
        addProduct(new Product("PRD-103", "Commercial LED Tube Light 40W (Box of 20)", "Electrical & Lighting", 3200.0, 350, 10));
        addProduct(new Product("PRD-104", "A4 Copier Paper Ream (Carton of 10)", "Office Supplies", 2400.0, 600, 25));
        addProduct(new Product("PRD-105", "Antiseptic Hand Wash 5L Jar (Pack of 4)", "Hygiene & FMCG", 1600.0, 400, 15));
        addProduct(new Product("PRD-106", "Smart Wifi Surge Protector strip", "Electronics", 1250.0, 250, 10));

        // Create initial sample orders to demonstrate tracking & invoices
        try {
            // Sample Order 1: Bulk basmati rice + sunflower oil for RET-001
            Retailer r1 = getRetailer("RET-001");
            Order o1 = new Order(generateNextOrderId(), r1, 0.05);
            o1.addItem(new OrderItem(getProduct("PRD-101"), 60)); // Qualifies for 10% discount
            o1.addItem(new OrderItem(getProduct("PRD-102"), 55)); // Qualifies for 10% discount
            o1.setStatus(OrderStatus.DELIVERED);
            o1.setDeliveryNotes("Delivered via BlueDart Cargo. Consignment #BD-889123");
            saveOrder(o1);

            // Sample Order 2: Office Supplies & FMCG for RET-002
            Retailer r2 = getRetailer("RET-002");
            Order o2 = new Order(generateNextOrderId(), r2, 0.05);
            o2.addItem(new OrderItem(getProduct("PRD-104"), 210)); // Qualifies for 18% discount
            o2.addItem(new OrderItem(getProduct("PRD-105"), 80));  // Qualifies for 10% discount
            o2.setStatus(OrderStatus.DISPATCHED);
            o2.setDeliveryNotes("Dispatched on Express Freight Truck MH-12-AB-4040");
            saveOrder(o2);
        } catch (Exception e) {
            // Ignore during startup initialization
        }
    }

    public synchronized String generateNextOrderId() {
        orderCounter++;
        return "ORD-" + orderCounter;
    }

    public synchronized String generateNextInvoiceId() {
        invoiceCounter++;
        return "INV-" + invoiceCounter;
    }

    // ==================== Retailer Operations ====================
    public synchronized void addRetailer(Retailer retailer) {
        retailerMap.put(retailer.getRetailerId(), retailer);
        if (!retailerList.contains(retailer)) {
            retailerList.add(retailer);
        }
    }

    public synchronized void updateRetailer(Retailer retailer) {
        retailerMap.put(retailer.getRetailerId(), retailer);
        for (int i = 0; i < retailerList.size(); i++) {
            if (retailerList.get(i).getRetailerId().equals(retailer.getRetailerId())) {
                retailerList.set(i, retailer);
                break;
            }
        }
    }

    public synchronized boolean deleteRetailer(String retailerId) {
        Retailer r = retailerMap.remove(retailerId);
        if (r != null) {
            retailerList.remove(r);
            return true;
        }
        return false;
    }

    public Retailer getRetailer(String retailerId) {
        return retailerMap.get(retailerId);
    }

    public List<Retailer> getAllRetailers() {
        return new ArrayList<>(retailerList);
    }

    // ==================== Product Operations ====================
    public synchronized void addProduct(Product product) {
        productMap.put(product.getProductId(), product);
        if (!productList.contains(product)) {
            productList.add(product);
        }
    }

    public synchronized void updateProduct(Product product) {
        productMap.put(product.getProductId(), product);
        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).getProductId().equals(product.getProductId())) {
                productList.set(i, product);
                break;
            }
        }
    }

    public synchronized boolean deleteProduct(String productId) {
        Product p = productMap.remove(productId);
        if (p != null) {
            productList.remove(p);
            return true;
        }
        return false;
    }

    public Product getProduct(String productId) {
        return productMap.get(productId);
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(productList);
    }

    // ==================== Order & Invoice Operations ====================
    public synchronized void saveOrder(Order order) {
        orderMap.put(order.getOrderId(), order);
        if (!orderList.contains(order)) {
            orderList.add(0, order); // Add newest on top
        }

        // Create corresponding invoice if not already created
        if (!invoiceMap.containsKey(order.getOrderId())) {
            Invoice inv = new Invoice(generateNextInvoiceId(), order);
            invoiceMap.put(order.getOrderId(), inv);
        }

        // Update retailer outstanding balance
        Retailer retailer = order.getRetailer();
        if (retailer != null) {
            retailer.addBalance(order.getFinalTotal());
        }
    }

    public Order getOrder(String orderId) {
        return orderMap.get(orderId);
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(orderList);
    }

    public Invoice getInvoiceByOrderId(String orderId) {
        return invoiceMap.get(orderId);
    }

    public User authenticateUser(String username, String password) {
        User user = userMap.get(username);
        if (user != null && user.validatePassword(password)) {
            return user;
        }
        return null;
    }
}
