package com.wholesale.ui;

import com.wholesale.exception.BusinessRuleException;
import com.wholesale.model.Invoice;
import com.wholesale.model.Order;
import com.wholesale.model.OrderStatus;
import com.wholesale.service.DataRepository;
import com.wholesale.service.OrderService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Order Tracking & Status Management Panel.
 * Tracks invoices, updates delivery status (Placed -> Processing -> Dispatched -> Delivered/Cancelled),
 * and provides invoice generation.
 */
public class OrderTrackingPanel extends JPanel {
    private final DataRepository repository;
    private final OrderService orderService;
    private final Runnable onDataChangedCallback;

    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> comboFilterStatus;
    private JTextField txtSearch;

    public OrderTrackingPanel(Runnable onDataChangedCallback) {
        this.repository = DataRepository.getInstance();
        this.orderService = new OrderService();
        this.onDataChangedCallback = onDataChangedCallback;

        setLayout(new BorderLayout(15, 15));
        setBackground(UIUtils.BG_LIGHT);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initHeader();
        initTable();
        loadOrderData();
    }

    private void initHeader() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(UIUtils.BG_LIGHT);

        // Header Title
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setBackground(UIUtils.BG_LIGHT);
        JLabel lblTitle = new JLabel("Order Tracking & Delivery Status");
        lblTitle.setFont(UIUtils.FONT_HEADER);
        lblTitle.setForeground(UIUtils.TEXT_PRIMARY);

        JLabel lblDesc = new JLabel("Monitor wholesale shipments, update fulfillment stages, and access commercial invoices.");
        lblDesc.setFont(UIUtils.FONT_SMALL);
        lblDesc.setForeground(UIUtils.TEXT_MUTED);

        titlePanel.add(lblTitle);
        titlePanel.add(lblDesc);
        topPanel.add(titlePanel, BorderLayout.WEST);

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        toolbar.setBackground(UIUtils.BG_LIGHT);

        toolbar.add(new JLabel("Status:"));
        comboFilterStatus = new JComboBox<>(new String[]{"All Orders", "Placed", "In Processing", "Dispatched", "Delivered", "Cancelled"});
        comboFilterStatus.setFont(UIUtils.FONT_REGULAR);
        comboFilterStatus.addActionListener(e -> applyFilter());
        toolbar.add(comboFilterStatus);

        txtSearch = UIUtils.createStyledTextField(12);
        txtSearch.setToolTipText("Filter by Order ID or Retailer Name");
        JButton btnSearch = UIUtils.createSecondaryButton("Search");
        btnSearch.addActionListener(e -> applyFilter());
        toolbar.add(txtSearch);
        toolbar.add(btnSearch);

        JButton btnUpdateStatus = UIUtils.createPrimaryButton("Update Status");
        btnUpdateStatus.addActionListener(e -> handleUpdateStatus());
        toolbar.add(btnUpdateStatus);

        JButton btnViewInvoice = UIUtils.createSecondaryButton("View Invoice");
        btnViewInvoice.addActionListener(e -> handleViewInvoice());
        toolbar.add(btnViewInvoice);

        topPanel.add(toolbar, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
    }

    private void initTable() {
        String[] columns = {"Order ID", "Date & Time", "Retailer", "Total Units", "Volume Savings (₹)", "Final Amount (₹)", "Delivery Status", "Shipment / Tracking Notes"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        UIUtils.formatTable(table);

        table.getColumnModel().getColumn(0).setPreferredWidth(90);
        table.getColumnModel().getColumn(1).setPreferredWidth(125);
        table.getColumnModel().getColumn(2).setPreferredWidth(180);
        table.getColumnModel().getColumn(3).setPreferredWidth(85);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);
        table.getColumnModel().getColumn(6).setPreferredWidth(110);
        table.getColumnModel().getColumn(7).setPreferredWidth(230);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadOrderData() {
        applyFilter();
    }

    private void applyFilter() {
        String filter = (String) comboFilterStatus.getSelectedItem();
        String query = txtSearch.getText().trim().toLowerCase();

        tableModel.setRowCount(0);
        List<Order> list = repository.getAllOrders();

        for (Order o : list) {
            boolean matchesStatus = filter == null || filter.equals("All Orders") || o.getStatus().getDisplayName().equalsIgnoreCase(filter);
            boolean matchesSearch = query.isEmpty() ||
                    o.getOrderId().toLowerCase().contains(query) ||
                    o.getRetailer().getBusinessName().toLowerCase().contains(query);

            if (matchesStatus && matchesSearch) {
                tableModel.addRow(new Object[]{
                        o.getOrderId(),
                        o.getOrderDate(),
                        o.getRetailer().getBusinessName(),
                        o.getTotalUnits(),
                        String.format("₹%.2f", o.getTotalSavings()),
                        String.format("₹%.2f", o.getFinalTotal()),
                        o.getStatus().getDisplayName(),
                        o.getDeliveryNotes()
                });
            }
        }
    }

    private void handleUpdateStatus() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an order to update status.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String orderId = (String) tableModel.getValueAt(selectedRow, 0);
        Order order = repository.getOrder(orderId);
        if (order == null) return;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Update Delivery Status - " + orderId, true);
        dialog.setSize(440, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 15));
        form.setBorder(new EmptyBorder(25, 25, 20, 25));
        form.setBackground(Color.WHITE);

        form.add(new JLabel("Order ID:"));
        form.add(new JLabel(order.getOrderId() + " (" + order.getRetailer().getBusinessName() + ")"));

        form.add(new JLabel("New Status:"));
        JComboBox<OrderStatus> comboStatus = new JComboBox<>(OrderStatus.values());
        comboStatus.setSelectedItem(order.getStatus());
        form.add(comboStatus);

        form.add(new JLabel("Tracking / Notes:"));
        JTextField txtNotes = UIUtils.createStyledTextField(15);
        txtNotes.setText(order.getDeliveryNotes());
        form.add(txtNotes);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        btnPanel.setBackground(UIUtils.BG_LIGHT);
        JButton btnCancel = UIUtils.createSecondaryButton("Cancel");
        btnCancel.addActionListener(e -> dialog.dispose());

        JButton btnSave = UIUtils.createPrimaryButton("Update");
        btnSave.addActionListener(e -> {
            OrderStatus newStatus = (OrderStatus) comboStatus.getSelectedItem();
            try {
                orderService.updateOrderStatus(orderId, newStatus, txtNotes.getText().trim());
                loadOrderData();
                if (onDataChangedCallback != null) {
                    onDataChangedCallback.run();
                }
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Order " + orderId + " updated successfully!", "Status Updated", JOptionPane.INFORMATION_MESSAGE);
            } catch (BusinessRuleException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void handleViewInvoice() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an order to view its invoice.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String orderId = (String) tableModel.getValueAt(selectedRow, 0);
        Invoice inv = repository.getInvoiceByOrderId(orderId);
        if (inv != null) {
            InvoiceDialog dialog = new InvoiceDialog((Frame) SwingUtilities.getWindowAncestor(this), inv);
            dialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Invoice not found for order " + orderId, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
