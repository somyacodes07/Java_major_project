package com.wholesale.ui;

import com.wholesale.model.Product;
import com.wholesale.service.DataRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel to manage Product inventory and inspect Tiered Wholesale Pricing rules.
 */
public class ProductPanel extends JPanel {
    private final DataRepository repository;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;

    public ProductPanel() {
        this.repository = DataRepository.getInstance();
        setLayout(new BorderLayout(15, 15));
        setBackground(UIUtils.BG_LIGHT);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initHeader();
        initTable();
        loadProductData();
    }

    private void initHeader() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(UIUtils.BG_LIGHT);

        // Title and description
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setBackground(UIUtils.BG_LIGHT);
        JLabel lblTitle = new JLabel("Product Inventory & Tiered Wholesale Pricing");
        lblTitle.setFont(UIUtils.FONT_HEADER);
        lblTitle.setForeground(UIUtils.TEXT_PRIMARY);

        JLabel lblDesc = new JLabel("Manage stock levels, minimum order quantities (MOQ), and volume discount tiers.");
        lblDesc.setFont(UIUtils.FONT_SMALL);
        lblDesc.setForeground(UIUtils.TEXT_MUTED);

        titlePanel.add(lblTitle);
        titlePanel.add(lblDesc);
        topPanel.add(titlePanel, BorderLayout.WEST);

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        toolbar.setBackground(UIUtils.BG_LIGHT);

        txtSearch = UIUtils.createStyledTextField(14);
        txtSearch.setToolTipText("Search by Product ID, Name or Category");
        JButton btnSearch = UIUtils.createSecondaryButton("Search");
        btnSearch.addActionListener(e -> filterProductData(txtSearch.getText().trim()));

        JButton btnReset = UIUtils.createSecondaryButton("Reset");
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            loadProductData();
        });

        JButton btnAdd = UIUtils.createPrimaryButton("+ Add Product");
        btnAdd.addActionListener(e -> showAddEditDialog(null));

        JButton btnRestock = UIUtils.createSuccessButton("Restock Inventory");
        btnRestock.addActionListener(e -> handleRestock());

        JButton btnEdit = UIUtils.createSecondaryButton("Edit");
        btnEdit.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select a product to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String productId = (String) tableModel.getValueAt(selectedRow, 0);
            Product p = repository.getProduct(productId);
            if (p != null) {
                showAddEditDialog(p);
            }
        });

        JButton btnDelete = UIUtils.createDangerButton("Delete");
        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select a product to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String productId = (String) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete Product " + productId + "?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                repository.deleteProduct(productId);
                loadProductData();
            }
        });

        toolbar.add(new JLabel("Search:"));
        toolbar.add(txtSearch);
        toolbar.add(btnSearch);
        toolbar.add(btnReset);
        toolbar.add(Box.createHorizontalStrut(10));
        toolbar.add(btnAdd);
        toolbar.add(btnRestock);
        toolbar.add(btnEdit);
        toolbar.add(btnDelete);

        topPanel.add(toolbar, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
    }

    private void initTable() {
        String[] columns = {"Product ID", "Product Name", "Category", "Base Price (₹)", "Stock Available", "Min Order Qty", "Bulk Volume Discount Tiers"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        UIUtils.formatTable(table);

        table.getColumnModel().getColumn(0).setPreferredWidth(90);
        table.getColumnModel().getColumn(1).setPreferredWidth(230);
        table.getColumnModel().getColumn(2).setPreferredWidth(130);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(90);
        table.getColumnModel().getColumn(6).setPreferredWidth(260);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadProductData() {
        tableModel.setRowCount(0);
        List<Product> list = repository.getAllProducts();
        for (Product p : list) {
            tableModel.addRow(new Object[]{
                    p.getProductId(),
                    p.getName(),
                    p.getCategory(),
                    String.format("%.2f", p.getBasePrice()),
                    p.getStockQuantity(),
                    p.getMinOrderQuantity(),
                    p.getTierSummary()
            });
        }
    }

    private void filterProductData(String query) {
        if (query.isEmpty()) {
            loadProductData();
            return;
        }
        String lower = query.toLowerCase();
        tableModel.setRowCount(0);
        for (Product p : repository.getAllProducts()) {
            if (p.getProductId().toLowerCase().contains(lower) ||
                    p.getName().toLowerCase().contains(lower) ||
                    p.getCategory().toLowerCase().contains(lower)) {
                tableModel.addRow(new Object[]{
                        p.getProductId(),
                        p.getName(),
                        p.getCategory(),
                        String.format("%.2f", p.getBasePrice()),
                        p.getStockQuantity(),
                        p.getMinOrderQuantity(),
                        p.getTierSummary()
                });
            }
        }
    }

    private void handleRestock() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a product to restock.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String productId = (String) tableModel.getValueAt(selectedRow, 0);
        Product p = repository.getProduct(productId);
        if (p == null) return;

        String input = JOptionPane.showInputDialog(this,
                "Enter additional bulk units to add to inventory for:\n" + p.getName() + " (Current: " + p.getStockQuantity() + ")",
                "Restock Inventory", JOptionPane.QUESTION_MESSAGE);

        if (input != null && !input.trim().isEmpty()) {
            try {
                int addQty = Integer.parseInt(input.trim());
                if (addQty <= 0) {
                    JOptionPane.showMessageDialog(this, "Restock quantity must be positive.", "Invalid Quantity", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                p.addStock(addQty);
                repository.updateProduct(p);
                loadProductData();
                JOptionPane.showMessageDialog(this, "Successfully added " + addQty + " units to " + p.getName(), "Inventory Updated", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid whole number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAddEditDialog(Product existing) {
        boolean isEdit = existing != null;
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                isEdit ? "Edit Product Details" : "Add New Wholesale Product", true);
        dialog.setSize(440, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 12));
        formPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        formPanel.setBackground(Color.WHITE);

        JTextField txtId = UIUtils.createStyledTextField(15);
        JTextField txtName = UIUtils.createStyledTextField(15);
        JTextField txtCat = UIUtils.createStyledTextField(15);
        JTextField txtPrice = UIUtils.createStyledTextField(15);
        JTextField txtStock = UIUtils.createStyledTextField(15);

        if (isEdit) {
            txtId.setText(existing.getProductId());
            txtId.setEditable(false);
            txtName.setText(existing.getName());
            txtCat.setText(existing.getCategory());
            txtPrice.setText(String.valueOf(existing.getBasePrice()));
            txtStock.setText(String.valueOf(existing.getStockQuantity()));
        } else {
            txtId.setText("PRD-" + (100 + repository.getAllProducts().size() + 1));
            txtCat.setText("General Wholesale");
            txtStock.setText("100");
        }

        formPanel.add(new JLabel("Product ID:"));
        formPanel.add(txtId);
        formPanel.add(new JLabel("Product Name:"));
        formPanel.add(txtName);
        formPanel.add(new JLabel("Category:"));
        formPanel.add(txtCat);
        formPanel.add(new JLabel("Base Price / Unit (₹):"));
        formPanel.add(txtPrice);
        formPanel.add(new JLabel("Warehouse Stock:"));
        formPanel.add(txtStock);

        dialog.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        btnPanel.setBackground(UIUtils.BG_LIGHT);
        JButton btnCancel = UIUtils.createSecondaryButton("Cancel");
        btnCancel.addActionListener(e -> dialog.dispose());

        JButton btnSave = UIUtils.createPrimaryButton(isEdit ? "Update" : "Save Product");
        btnSave.addActionListener(e -> {
            String id = txtId.getText().trim();
            String name = txtName.getText().trim();
            String category = txtCat.getText().trim();
            String priceStr = txtPrice.getText().trim();
            String stockStr = txtStock.getText().trim();

            if (id.isEmpty() || name.isEmpty() || category.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double price = Double.parseDouble(priceStr);
                int stock = Integer.parseInt(stockStr);

                if (price <= 0 || stock < 0) {
                    JOptionPane.showMessageDialog(dialog, "Price must be > 0 and stock cannot be negative.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (isEdit) {
                    existing.setName(name);
                    existing.setCategory(category);
                    existing.setBasePrice(price);
                    existing.setStockQuantity(stock);
                    repository.updateProduct(existing);
                } else {
                    if (repository.getProduct(id) != null) {
                        JOptionPane.showMessageDialog(dialog, "Product ID already exists!", "Duplicate ID", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    Product p = new Product(id, name, category, price, stock, 10);
                    repository.addProduct(p);
                }

                loadProductData();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numeric values for price and stock.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
