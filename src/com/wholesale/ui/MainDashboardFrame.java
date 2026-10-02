package com.wholesale.ui;

import com.wholesale.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main Application Dashboard hosting all modules in a modern tabbed layout.
 */
public class MainDashboardFrame extends JFrame {
    private final User currentUser;

    private JTabbedPane tabbedPane;
    private CreateOrderPanel createOrderPanel;
    private OrderTrackingPanel orderTrackingPanel;
    private RetailerPanel retailerPanel;
    private ProductPanel productPanel;
    private ReportsPanel reportsPanel;

    public MainDashboardFrame(User user) {
        this.currentUser = user;
        setTitle("Wholesale Order Processing System - Case Study 119");
        setSize(1180, 750);
        setMinimumSize(new Dimension(1000, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(UIUtils.BG_LIGHT);

        // Top Navigation Header
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setBackground(UIUtils.PRIMARY_NAVY);
        topHeader.setBorder(new EmptyBorder(14, 24, 14, 24));

        // Branding
        JPanel brandPanel = new JPanel(new GridLayout(2, 1));
        brandPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Wholesale Order Processing System");
        lblTitle.setFont(UIUtils.FONT_HEADER);
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("B.Tech CSE - Case Study No. 119 | ITM Skills University");
        lblSub.setFont(UIUtils.FONT_SMALL);
        lblSub.setForeground(new Color(148, 163, 184));

        brandPanel.add(lblTitle);
        brandPanel.add(lblSub);
        topHeader.add(brandPanel, BorderLayout.WEST);

        // User Info & Logout
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setOpaque(false);

        JLabel lblUser = new JLabel("<html><div style='text-align: right; color: white;'><b>" +
                currentUser.getFullName() + "</b><br><span style='color: #94A3B8; font-size: 10px;'>" +
                currentUser.getRole() + "</span></div></html>");
        lblUser.setFont(UIUtils.FONT_REGULAR);

        JButton btnLogout = UIUtils.createDangerButton("Logout");
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
            }
        });

        userPanel.add(lblUser);
        userPanel.add(btnLogout);
        topHeader.add(userPanel, BorderLayout.EAST);

        mainContainer.add(topHeader, BorderLayout.NORTH);

        // Tabbed Pane Setup
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIUtils.FONT_SUBHEADER);
        tabbedPane.setBackground(Color.WHITE);

        // Initialize Child Panels
        Runnable refreshAllViews = this::refreshAllTabs;

        createOrderPanel = new CreateOrderPanel(refreshAllViews);
        orderTrackingPanel = new OrderTrackingPanel(refreshAllViews);
        retailerPanel = new RetailerPanel();
        productPanel = new ProductPanel();
        reportsPanel = new ReportsPanel();

        tabbedPane.addTab("🛒 Place Bulk Order", createOrderPanel);
        tabbedPane.addTab("🚚 Order & Delivery Tracking", orderTrackingPanel);
        tabbedPane.addTab("👥 Retailer Directory", retailerPanel);
        tabbedPane.addTab("📦 Product Catalog & Pricing", productPanel);
        tabbedPane.addTab("📊 Reports & Analytics", reportsPanel);

        // Refresh panel data on tab change
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            switch (selectedIndex) {
                case 0:
                    createOrderPanel.refreshDropdowns();
                    break;
                case 1:
                    orderTrackingPanel.loadOrderData();
                    break;
                case 2:
                    retailerPanel.loadRetailerData();
                    break;
                case 3:
                    productPanel.loadProductData();
                    break;
                case 4:
                    reportsPanel.refreshReportData();
                    break;
            }
        });

        mainContainer.add(tabbedPane, BorderLayout.CENTER);

        // Bottom Status Bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(Color.WHITE);
        statusBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UIUtils.BORDER_COLOR),
                new EmptyBorder(6, 20, 6, 20)
        ));

        JLabel lblStatus = new JLabel("● System Online | Engine: Java Swing + Java Collections (HashMap & ArrayList) | Tiered Bulk Pricing Active");
        lblStatus.setFont(UIUtils.FONT_SMALL);
        lblStatus.setForeground(UIUtils.TEXT_MUTED);

        JLabel lblCopyright = new JLabel("Case Study 119 Prototype");
        lblCopyright.setFont(UIUtils.FONT_SMALL);
        lblCopyright.setForeground(UIUtils.TEXT_MUTED);

        statusBar.add(lblStatus, BorderLayout.WEST);
        statusBar.add(lblCopyright, BorderLayout.EAST);

        mainContainer.add(statusBar, BorderLayout.SOUTH);

        setContentPane(mainContainer);
    }

    private void refreshAllTabs() {
        createOrderPanel.refreshDropdowns();
        orderTrackingPanel.loadOrderData();
        retailerPanel.loadRetailerData();
        productPanel.loadProductData();
        reportsPanel.refreshReportData();
    }
}
