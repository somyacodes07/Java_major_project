package com.wholesale.ui;

import com.wholesale.model.Order;
import com.wholesale.model.OrderStatus;
import com.wholesale.model.Product;
import com.wholesale.model.Retailer;
import com.wholesale.service.DataRepository;
import com.wholesale.service.OrderService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Executive Reports & Analytics Panel.
 * Generates wholesale distribution business metrics, status distribution, and retailer rankings.
 */
public class ReportsPanel extends JPanel {
    private final DataRepository repository;
    private final OrderService orderService;

    private JLabel lblTotalOrders;
    private JLabel lblTotalRevenue;
    private JLabel lblTotalSavings;
    private JLabel lblActiveRetailers;
    private JTextArea txtDetailedReport;

    public ReportsPanel() {
        this.repository = DataRepository.getInstance();
        this.orderService = new OrderService();

        setLayout(new BorderLayout(15, 15));
        setBackground(UIUtils.BG_LIGHT);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        refreshReportData();
    }

    private void initComponents() {
        // Header
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setBackground(UIUtils.BG_LIGHT);
        JLabel lblTitle = new JLabel("Wholesale Performance & Order Summaries");
        lblTitle.setFont(UIUtils.FONT_HEADER);
        lblTitle.setForeground(UIUtils.TEXT_PRIMARY);

        JLabel lblDesc = new JLabel("Consolidated distribution metrics, volume discount impact, and business audit summaries.");
        lblDesc.setFont(UIUtils.FONT_SMALL);
        lblDesc.setForeground(UIUtils.TEXT_MUTED);

        headerPanel.add(lblTitle);
        headerPanel.add(lblDesc);
        add(headerPanel, BorderLayout.NORTH);

        // Center Content
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setBackground(UIUtils.BG_LIGHT);

        // KPI Cards Row
        JPanel kpiRow = new JPanel(new GridLayout(1, 4, 15, 0));
        kpiRow.setBackground(UIUtils.BG_LIGHT);
        kpiRow.setPreferredSize(new Dimension(0, 95));

        lblTotalOrders = new JLabel("0");
        lblTotalRevenue = new JLabel("₹0.00");
        lblTotalSavings = new JLabel("₹0.00");
        lblActiveRetailers = new JLabel("0");

        kpiRow.add(createKpiCard("TOTAL BULK ORDERS", lblTotalOrders, UIUtils.ACCENT_BLUE));
        kpiRow.add(createKpiCard("GROSS REVENUE", lblTotalRevenue, UIUtils.SUCCESS_GREEN));
        kpiRow.add(createKpiCard("VOLUME SAVINGS GRANTED", lblTotalSavings, UIUtils.WARNING_AMBER));
        kpiRow.add(createKpiCard("ACTIVE RETAILERS", lblActiveRetailers, UIUtils.TEXT_PRIMARY));

        centerPanel.add(kpiRow, BorderLayout.NORTH);

        // Text Report Area
        JPanel reportCard = UIUtils.createCardPanel();
        reportCard.setLayout(new BorderLayout(10, 10));

        JPanel reportHeader = new JPanel(new BorderLayout());
        reportHeader.setBackground(Color.WHITE);
        JLabel lblReportTitle = new JLabel("Business Distribution Summary Log");
        lblReportTitle.setFont(UIUtils.FONT_SUBHEADER);
        lblReportTitle.setForeground(UIUtils.TEXT_PRIMARY);

        JButton btnRefresh = UIUtils.createSecondaryButton("Refresh Analytics");
        btnRefresh.addActionListener(e -> refreshReportData());

        reportHeader.add(lblReportTitle, BorderLayout.WEST);
        reportHeader.add(btnRefresh, BorderLayout.EAST);
        reportCard.add(reportHeader, BorderLayout.NORTH);

        txtDetailedReport = new JTextArea();
        txtDetailedReport.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtDetailedReport.setEditable(false);
        txtDetailedReport.setBackground(Color.WHITE);
        txtDetailedReport.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(txtDetailedReport);
        scroll.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR));
        reportCard.add(scroll, BorderLayout.CENTER);

        centerPanel.add(reportCard, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createKpiCard(String label, JLabel valueLabel, Color valueColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(UIUtils.BORDER_COLOR, 1, true),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(UIUtils.TEXT_MUTED);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(valueColor);

        card.add(lbl);
        card.add(Box.createVerticalStrut(6));
        card.add(valueLabel);
        return card;
    }

    public void refreshReportData() {
        List<Order> orders = repository.getAllOrders();
        List<Retailer> retailers = repository.getAllRetailers();
        List<Product> products = repository.getAllProducts();

        double revenue = orderService.getTotalRevenue();
        double savings = orderService.getTotalDiscountGranted();

        lblTotalOrders.setText(String.valueOf(orders.size()));
        lblTotalRevenue.setText(String.format("₹%.2f", revenue));
        lblTotalSavings.setText(String.format("₹%.2f", savings));
        lblActiveRetailers.setText(String.valueOf(retailers.size()));

        // Detailed Report Text
        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================================\n");
        sb.append("                     WHOLESALE ORDER PROCESSING SYSTEM - AUDIT REPORT                   \n");
        sb.append("========================================================================================\n\n");

        sb.append("1. FULFILLMENT & DELIVERY STATUS BREAKDOWN:\n");
        sb.append("----------------------------------------------------------------------------------------\n");
        Map<OrderStatus, Integer> statusCount = new HashMap<>();
        for (OrderStatus s : OrderStatus.values()) statusCount.put(s, 0);
        for (Order o : orders) {
            statusCount.put(o.getStatus(), statusCount.get(o.getStatus()) + 1);
        }
        for (OrderStatus s : OrderStatus.values()) {
            sb.append(String.format("   • %-15s : %d orders\n", s.getDisplayName(), statusCount.get(s)));
        }

        sb.append("\n2. RETAILER ACCOUNTS & CREDIT EXPOSURE:\n");
        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(String.format("   %-10s %-28s %-15s %-15s %-15s\n", "ID", "Retailer Business", "Credit Limit", "Outstanding", "Available Credit"));
        for (Retailer r : retailers) {
            sb.append(String.format("   %-10s %-28s ₹%-14.2f ₹%-14.2f ₹%-14.2f\n",
                    r.getRetailerId(),
                    truncate(r.getBusinessName(), 28),
                    r.getCreditLimit(),
                    r.getCurrentOutstandingBalance(),
                    r.getAvailableCredit()));
        }

        sb.append("\n3. WAREHOUSE INVENTORY HEALTH & LOW STOCK ALERT (THRESHOLD <= 300 UNITS):\n");
        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(String.format("   %-10s %-32s %-12s %-10s %-15s\n", "Prod ID", "Product Description", "Stock Units", "MOQ", "Status"));
        for (Product p : products) {
            String stockStatus = p.getStockQuantity() <= 300 ? "LOW STOCK ALERT!" : "Adequate";
            sb.append(String.format("   %-10s %-32s %-12d %-10d %-15s\n",
                    p.getProductId(),
                    truncate(p.getName(), 32),
                    p.getStockQuantity(),
                    p.getMinOrderQuantity(),
                    stockStatus));
        }

        sb.append("\n========================================================================================\n");
        sb.append("                           END OF EXECUTIVE AUDIT REPORT                                \n");
        sb.append("========================================================================================\n");

        txtDetailedReport.setText(sb.toString());
        txtDetailedReport.setCaretPosition(0);
    }

    private String truncate(String text, int max) {
        if (text.length() <= max) return text;
        return text.substring(0, max - 3) + "...";
    }
}
