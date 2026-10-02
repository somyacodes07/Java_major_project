package com.wholesale.ui;

import com.wholesale.exception.BusinessRuleException;
import com.wholesale.exception.InsufficientStockException;
import com.wholesale.model.*;
import com.wholesale.service.DataRepository;
import com.wholesale.service.OrderService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Interactive Bulk Order Creation Panel with real-time Tiered Pricing calculation,
 * credit validation, and automated invoicing.
 */
public class CreateOrderPanel extends JPanel {
    private final DataRepository repository;
    private final OrderService orderService;
    private final Runnable onOrderPlacedCallback;

    private JComboBox<Retailer> comboRetailer;
    private JLabel lblRetailerCreditInfo;

    private JComboBox<Product> comboProduct;
    private JTextField txtQuantity;
    private JLabel lblPricingPreview;

    private JTable cartTable;
    private DefaultTableModel cartModel;
    private List<OrderItem> currentCartItems;

    // Financial labels
    private JLabel lblGrossTotal;
    private JLabel lblBulkSavings;
    private JLabel lblSubtotal;
    private JLabel lblTax;
    private JLabel lblFinalTotal;

    public CreateOrderPanel(Runnable onOrderPlacedCallback) {
        this.repository = DataRepository.getInstance();
        this.orderService = new OrderService();
        this.onOrderPlacedCallback = onOrderPlacedCallback;
        this.currentCartItems = new ArrayList<>();

        setLayout(new BorderLayout(15, 15));
        setBackground(UIUtils.BG_LIGHT);
        setBorder(new EmptyBorder(15, 20, 15, 20));

        initComponents();
    }

    private void initComponents() {
        // Top Header
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setBackground(UIUtils.BG_LIGHT);
        JLabel lblTitle = new JLabel("Create Bulk Wholesale Order");
        lblTitle.setFont(UIUtils.FONT_HEADER);
        lblTitle.setForeground(UIUtils.TEXT_PRIMARY);

        JLabel lblDesc = new JLabel("Select a retailer, add bulk products, view real-time volume discounts, and generate an invoice.");
        lblDesc.setFont(UIUtils.FONT_SMALL);
        lblDesc.setForeground(UIUtils.TEXT_MUTED);

        headerPanel.add(lblTitle);
        headerPanel.add(lblDesc);
        add(headerPanel, BorderLayout.NORTH);

        // Center Content: Split into Left (Inputs & Cart) and Right (Order Summary & Actions)
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBackground(UIUtils.BG_LIGHT);

        // Left Panel (Selection & Cart Table)
        JPanel leftPanel = new JPanel(new BorderLayout(12, 12));
        leftPanel.setBackground(UIUtils.BG_LIGHT);

        // Retailer & Product Selection Card
        JPanel selectionCard = UIUtils.createCardPanel();
        selectionCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        // Retailer Row
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.15;
        selectionCard.add(new JLabel("Select Retailer:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.55;
        comboRetailer = new JComboBox<>();
        comboRetailer.setFont(UIUtils.FONT_REGULAR);
        comboRetailer.addActionListener(e -> updateRetailerCreditDisplay());
        selectionCard.add(comboRetailer, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.3;
        lblRetailerCreditInfo = new JLabel("Available Credit: ₹0.00");
        lblRetailerCreditInfo.setFont(UIUtils.FONT_SMALL);
        lblRetailerCreditInfo.setForeground(UIUtils.ACCENT_BLUE);
        selectionCard.add(lblRetailerCreditInfo, gbc);

        // Product Row
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.15;
        selectionCard.add(new JLabel("Product:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.55;
        comboProduct = new JComboBox<>();
        comboProduct.setFont(UIUtils.FONT_REGULAR);
        comboProduct.addActionListener(e -> updatePricingPreview());
        selectionCard.add(comboProduct, gbc);

        // Quantity Row
        JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        qtyPanel.setBackground(Color.WHITE);
        qtyPanel.add(new JLabel("Qty:"));
        txtQuantity = UIUtils.createStyledTextField(6);
        txtQuantity.setText("50");
        txtQuantity.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                updatePricingPreview();
            }
        });
        qtyPanel.add(txtQuantity);

        JButton btnAddCart = UIUtils.createPrimaryButton("+ Add to Cart");
        btnAddCart.addActionListener(e -> handleAddToCart());
        qtyPanel.add(btnAddCart);

        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0.3;
        selectionCard.add(qtyPanel, gbc);

        // Live calculation preview row
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 2;
        lblPricingPreview = new JLabel("Live Calculation: Select product & enter quantity.");
        lblPricingPreview.setFont(UIUtils.FONT_SMALL);
        lblPricingPreview.setForeground(UIUtils.TEXT_MUTED);
        selectionCard.add(lblPricingPreview, gbc);

        leftPanel.add(selectionCard, BorderLayout.NORTH);

        // Order Cart Table
        String[] cartCols = {"Product ID", "Product Name", "Quantity", "Base Unit Rate", "Tier Discount", "Effective Rate", "Line Total", "Savings"};
        cartModel = new DefaultTableModel(cartCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable = new JTable(cartModel);
        UIUtils.formatTable(cartTable);

        JScrollPane cartScroll = new JScrollPane(cartTable);
        cartScroll.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR));
        cartScroll.getViewport().setBackground(Color.WHITE);

        JPanel tableContainer = new JPanel(new BorderLayout(5, 5));
        tableContainer.setBackground(UIUtils.BG_LIGHT);
        tableContainer.add(new JLabel("Order Items Cart:"), BorderLayout.NORTH);
        tableContainer.add(cartScroll, BorderLayout.CENTER);

        JButton btnRemoveItem = UIUtils.createSecondaryButton("Remove Selected Item");
        btnRemoveItem.addActionListener(e -> handleRemoveItem());
        JPanel cartBottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cartBottom.setBackground(UIUtils.BG_LIGHT);
        cartBottom.add(btnRemoveItem);
        tableContainer.add(cartBottom, BorderLayout.SOUTH);

        leftPanel.add(tableContainer, BorderLayout.CENTER);
        contentPanel.add(leftPanel, BorderLayout.CENTER);

        // Right Panel (Order Summary & Checkout)
        JPanel rightPanel = UIUtils.createCardPanel();
        rightPanel.setPreferredSize(new Dimension(320, 0));
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        JLabel lblSummaryTitle = new JLabel("Order & Pricing Summary");
        lblSummaryTitle.setFont(UIUtils.FONT_HEADER);
        lblSummaryTitle.setForeground(UIUtils.TEXT_PRIMARY);
        rightPanel.add(lblSummaryTitle);
        rightPanel.add(Box.createVerticalStrut(15));

        lblGrossTotal = createSummaryRow(rightPanel, "Gross Total (Base):", "₹0.00", UIUtils.TEXT_PRIMARY);
        lblBulkSavings = createSummaryRow(rightPanel, "Tiered Bulk Savings:", "-₹0.00", UIUtils.SUCCESS_GREEN);
        lblSubtotal = createSummaryRow(rightPanel, "Taxable Subtotal:", "₹0.00", UIUtils.TEXT_PRIMARY);
        lblTax = createSummaryRow(rightPanel, "GST / Tax (5%):", "₹0.00", UIUtils.TEXT_PRIMARY);

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Short.MAX_VALUE, 1));
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(sep);
        rightPanel.add(Box.createVerticalStrut(10));

        lblFinalTotal = createSummaryRow(rightPanel, "Final Net Payable:", "₹0.00", UIUtils.ACCENT_BLUE);
        lblFinalTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));

        rightPanel.add(Box.createVerticalStrut(25));

        JButton btnPlaceOrder = UIUtils.createSuccessButton("✓ Submit & Place Order");
        btnPlaceOrder.setMaximumSize(new Dimension(Short.MAX_VALUE, 42));
        btnPlaceOrder.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPlaceOrder.addActionListener(e -> handlePlaceOrder());
        rightPanel.add(btnPlaceOrder);

        rightPanel.add(Box.createVerticalStrut(10));

        JButton btnClearCart = UIUtils.createSecondaryButton("Clear Order");
        btnClearCart.setMaximumSize(new Dimension(Short.MAX_VALUE, 36));
        btnClearCart.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnClearCart.addActionListener(e -> clearCart());
        rightPanel.add(btnClearCart);

        rightPanel.add(Box.createVerticalGlue());

        contentPanel.add(rightPanel, BorderLayout.EAST);
        add(contentPanel, BorderLayout.CENTER);

        refreshDropdowns();
    }

    private JLabel createSummaryRow(JPanel parent, String labelText, String defaultValue, Color valColor) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Short.MAX_VALUE, 24));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UIUtils.FONT_REGULAR);
        lbl.setForeground(UIUtils.TEXT_MUTED);

        JLabel val = new JLabel(defaultValue);
        val.setFont(UIUtils.FONT_REGULAR_BOLD);
        val.setForeground(valColor);

        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);

        parent.add(row);
        parent.add(Box.createVerticalStrut(8));
        return val;
    }

    public void refreshDropdowns() {
        comboRetailer.removeAllItems();
        for (Retailer r : repository.getAllRetailers()) {
            comboRetailer.addItem(r);
        }

        comboProduct.removeAllItems();
        for (Product p : repository.getAllProducts()) {
            comboProduct.addItem(p);
        }

        updateRetailerCreditDisplay();
        updatePricingPreview();
    }

    private void updateRetailerCreditDisplay() {
        Retailer r = (Retailer) comboRetailer.getSelectedItem();
        if (r != null) {
            lblRetailerCreditInfo.setText(String.format("Available Credit: ₹%.2f (Limit: ₹%.2f)",
                    r.getAvailableCredit(), r.getCreditLimit()));
        } else {
            lblRetailerCreditInfo.setText("No retailer selected.");
        }
    }

    private void updatePricingPreview() {
        Product p = (Product) comboProduct.getSelectedItem();
        if (p == null) return;

        try {
            int qty = Integer.parseInt(txtQuantity.getText().trim());
            if (qty <= 0) {
                lblPricingPreview.setText("Enter a positive quantity (MOQ: " + p.getMinOrderQuantity() + ").");
                return;
            }

            double discount = p.getDiscountPercent(qty);
            double effectiveUnit = p.getEffectiveUnitPrice(qty);
            double lineTotal = effectiveUnit * qty;
            double savings = (p.getBasePrice() - effectiveUnit) * qty;

            lblPricingPreview.setText(String.format(
                    "<html>Bulk Preview: <b>%.1f%% Tier Discount</b> applied! Unit: ₹%.2f (Base: ₹%.2f) | Total: ₹%.2f | You Save: <font color='#10B981'>₹%.2f</font></html>",
                    discount, effectiveUnit, p.getBasePrice(), lineTotal, savings));
        } catch (NumberFormatException e) {
            lblPricingPreview.setText("Enter a valid integer quantity (Stock: " + p.getStockQuantity() + ").");
        }
    }

    private void handleAddToCart() {
        Product p = (Product) comboProduct.getSelectedItem();
        if (p == null) {
            JOptionPane.showMessageDialog(this, "Please select a product.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(txtQuantity.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid whole number for quantity.", "Invalid Quantity", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (qty < p.getMinOrderQuantity()) {
            JOptionPane.showMessageDialog(this,
                    "Quantity is below Minimum Order Quantity (MOQ) of " + p.getMinOrderQuantity() + " units for this item.",
                    "MOQ Requirement", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check if already in cart
        int existingQtyInCart = 0;
        for (OrderItem item : currentCartItems) {
            if (item.getProduct().getProductId().equals(p.getProductId())) {
                existingQtyInCart += item.getQuantity();
            }
        }

        if (existingQtyInCart + qty > p.getStockQuantity()) {
            JOptionPane.showMessageDialog(this,
                    "Total requested (" + (existingQtyInCart + qty) + ") exceeds available warehouse stock (" + p.getStockQuantity() + ").",
                    "Stock Exceeded", JOptionPane.ERROR_MESSAGE);
            return;
        }

        OrderItem newItem = new OrderItem(p, qty);
        currentCartItems.add(newItem);

        cartModel.addRow(new Object[]{
                p.getProductId(),
                p.getName(),
                newItem.getQuantity(),
                String.format("₹%.2f", newItem.getBaseUnitPrice()),
                String.format("%.1f%%", newItem.getDiscountPercent()),
                String.format("₹%.2f", newItem.getEffectiveUnitPrice()),
                String.format("₹%.2f", newItem.getLineTotal()),
                String.format("₹%.2f", newItem.getSavings())
        });

        calculateTotals();
    }

    private void handleRemoveItem() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow >= 0) {
            currentCartItems.remove(selectedRow);
            cartModel.removeRow(selectedRow);
            calculateTotals();
        } else {
            JOptionPane.showMessageDialog(this, "Select an item from the cart to remove.", "Selection Required", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void calculateTotals() {
        double gross = 0.0;
        double savings = 0.0;
        double subtotal = 0.0;

        for (OrderItem item : currentCartItems) {
            gross += item.getOriginalTotalWithoutDiscount();
            savings += item.getSavings();
            subtotal += item.getLineTotal();
        }

        double tax = subtotal * 0.05; // 5% GST
        double finalTotal = subtotal + tax;

        lblGrossTotal.setText(String.format("₹%.2f", gross));
        lblBulkSavings.setText(String.format("-₹%.2f", savings));
        lblSubtotal.setText(String.format("₹%.2f", subtotal));
        lblTax.setText(String.format("₹%.2f", tax));
        lblFinalTotal.setText(String.format("₹%.2f", finalTotal));
    }

    private void handlePlaceOrder() {
        Retailer retailer = (Retailer) comboRetailer.getSelectedItem();
        if (retailer == null) {
            JOptionPane.showMessageDialog(this, "Please select a retailer.", "Missing Retailer", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentCartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Order cart is empty! Add products to proceed.", "Empty Cart", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Order order = orderService.placeOrder(retailer, currentCartItems, 0.05);

            JOptionPane.showMessageDialog(this,
                    "Order " + order.getOrderId() + " placed successfully!\nTax invoice generated.",
                    "Order Successful", JOptionPane.INFORMATION_MESSAGE);

            Invoice inv = repository.getInvoiceByOrderId(order.getOrderId());
            if (inv != null) {
                InvoiceDialog dialog = new InvoiceDialog((Frame) SwingUtilities.getWindowAncestor(this), inv);
                dialog.setVisible(true);
            }

            clearCart();
            updateRetailerCreditDisplay();

            if (onOrderPlacedCallback != null) {
                onOrderPlacedCallback.run();
            }

        } catch (BusinessRuleException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Order Placement Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void clearCart() {
        currentCartItems.clear();
        cartModel.setRowCount(0);
        calculateTotals();
    }
}
