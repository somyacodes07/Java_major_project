package com.wholesale.ui;

import com.wholesale.model.Retailer;
import com.wholesale.service.DataRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel to manage Retailer records (View, Search, Add, Edit, Delete).
 * Demonstrates: CRUD operations, input validation, and HashMap / ArrayList synchronization.
 */
public class RetailerPanel extends JPanel {
    private final DataRepository repository;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;

    public RetailerPanel() {
        this.repository = DataRepository.getInstance();
        setLayout(new BorderLayout(15, 15));
        setBackground(UIUtils.BG_LIGHT);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initHeader();
        initTable();
        loadRetailerData();
    }

    private void initHeader() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(UIUtils.BG_LIGHT);

        // Title and description
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setBackground(UIUtils.BG_LIGHT);
        JLabel lblTitle = new JLabel("Retailer Directory & Credit Profiles");
        lblTitle.setFont(UIUtils.FONT_HEADER);
        lblTitle.setForeground(UIUtils.TEXT_PRIMARY);

        JLabel lblDesc = new JLabel("Manage registered retail partners, contact details, and approved credit limits.");
        lblDesc.setFont(UIUtils.FONT_SMALL);
        lblDesc.setForeground(UIUtils.TEXT_MUTED);

        titlePanel.add(lblTitle);
        titlePanel.add(lblDesc);
        topPanel.add(titlePanel, BorderLayout.WEST);

        // Action Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        toolbar.setBackground(UIUtils.BG_LIGHT);

        txtSearch = UIUtils.createStyledTextField(14);
        txtSearch.setToolTipText("Search by Retailer ID, Name or City");
        JButton btnSearch = UIUtils.createSecondaryButton("Search");
        btnSearch.addActionListener(e -> filterRetailerData(txtSearch.getText().trim()));

        JButton btnReset = UIUtils.createSecondaryButton("Reset");
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            loadRetailerData();
        });

        JButton btnAdd = UIUtils.createPrimaryButton("+ Add Retailer");
        btnAdd.addActionListener(e -> showAddEditDialog(null));

        JButton btnEdit = UIUtils.createSecondaryButton("Edit Selected");
        btnEdit.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select a retailer to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String retailerId = (String) tableModel.getValueAt(selectedRow, 0);
            Retailer r = repository.getRetailer(retailerId);
            if (r != null) {
                showAddEditDialog(r);
            }
        });

        JButton btnDelete = UIUtils.createDangerButton("Delete");
        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select a retailer to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String retailerId = (String) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete Retailer " + retailerId + "?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                repository.deleteRetailer(retailerId);
                loadRetailerData();
            }
        });

        toolbar.add(new JLabel("Search:"));
        toolbar.add(txtSearch);
        toolbar.add(btnSearch);
        toolbar.add(btnReset);
        toolbar.add(Box.createHorizontalStrut(10));
        toolbar.add(btnAdd);
        toolbar.add(btnEdit);
        toolbar.add(btnDelete);

        topPanel.add(toolbar, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
    }

    private void initTable() {
        String[] columns = {"ID", "Business / Store Name", "Contact Person", "Phone", "Email", "City", "Credit Limit (₹)", "Outstanding (₹)", "Available Credit (₹)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        UIUtils.formatTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadRetailerData() {
        tableModel.setRowCount(0);
        List<Retailer> list = repository.getAllRetailers();
        for (Retailer r : list) {
            tableModel.addRow(new Object[]{
                    r.getRetailerId(),
                    r.getBusinessName(),
                    r.getContactPerson(),
                    r.getPhone(),
                    r.getEmail(),
                    r.getCity(),
                    String.format("%.2f", r.getCreditLimit()),
                    String.format("%.2f", r.getCurrentOutstandingBalance()),
                    String.format("%.2f", r.getAvailableCredit())
            });
        }
    }

    private void filterRetailerData(String query) {
        if (query.isEmpty()) {
            loadRetailerData();
            return;
        }
        String lower = query.toLowerCase();
        tableModel.setRowCount(0);
        for (Retailer r : repository.getAllRetailers()) {
            if (r.getRetailerId().toLowerCase().contains(lower) ||
                    r.getBusinessName().toLowerCase().contains(lower) ||
                    r.getCity().toLowerCase().contains(lower) ||
                    r.getContactPerson().toLowerCase().contains(lower)) {
                tableModel.addRow(new Object[]{
                        r.getRetailerId(),
                        r.getBusinessName(),
                        r.getContactPerson(),
                        r.getPhone(),
                        r.getEmail(),
                        r.getCity(),
                        String.format("%.2f", r.getCreditLimit()),
                        String.format("%.2f", r.getCurrentOutstandingBalance()),
                        String.format("%.2f", r.getAvailableCredit())
                });
            }
        }
    }

    private void showAddEditDialog(Retailer existing) {
        boolean isEdit = existing != null;
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                isEdit ? "Edit Retailer Profile" : "Register New Retailer", true);
        dialog.setSize(440, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 12));
        formPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        formPanel.setBackground(Color.WHITE);

        JTextField txtId = UIUtils.createStyledTextField(15);
        JTextField txtName = UIUtils.createStyledTextField(15);
        JTextField txtContact = UIUtils.createStyledTextField(15);
        JTextField txtPhone = UIUtils.createStyledTextField(15);
        JTextField txtEmail = UIUtils.createStyledTextField(15);
        JTextField txtCity = UIUtils.createStyledTextField(15);
        JTextField txtCredit = UIUtils.createStyledTextField(15);

        if (isEdit) {
            txtId.setText(existing.getRetailerId());
            txtId.setEditable(false);
            txtName.setText(existing.getBusinessName());
            txtContact.setText(existing.getContactPerson());
            txtPhone.setText(existing.getPhone());
            txtEmail.setText(existing.getEmail());
            txtCity.setText(existing.getCity());
            txtCredit.setText(String.valueOf(existing.getCreditLimit()));
        } else {
            // Auto generate ID
            txtId.setText("RET-" + String.format("%03d", repository.getAllRetailers().size() + 1));
            txtCredit.setText("500000");
        }

        formPanel.add(new JLabel("Retailer ID:"));
        formPanel.add(txtId);
        formPanel.add(new JLabel("Business Name:"));
        formPanel.add(txtName);
        formPanel.add(new JLabel("Contact Person:"));
        formPanel.add(txtContact);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(txtPhone);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(txtEmail);
        formPanel.add(new JLabel("City:"));
        formPanel.add(txtCity);
        formPanel.add(new JLabel("Credit Limit (₹):"));
        formPanel.add(txtCredit);

        dialog.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        btnPanel.setBackground(UIUtils.BG_LIGHT);
        JButton btnCancel = UIUtils.createSecondaryButton("Cancel");
        btnCancel.addActionListener(e -> dialog.dispose());

        JButton btnSave = UIUtils.createPrimaryButton(isEdit ? "Update" : "Save Retailer");
        btnSave.addActionListener(e -> {
            String id = txtId.getText().trim();
            String name = txtName.getText().trim();
            String contact = txtContact.getText().trim();
            String phone = txtPhone.getText().trim();
            String email = txtEmail.getText().trim();
            String city = txtCity.getText().trim();
            String creditStr = txtCredit.getText().trim();

            if (id.isEmpty() || name.isEmpty() || contact.isEmpty() || phone.isEmpty() || city.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all required fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double credit;
            try {
                credit = Double.parseDouble(creditStr);
                if (credit <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Credit limit must be a positive amount.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Credit limit must be a valid number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (isEdit) {
                existing.setBusinessName(name);
                existing.setContactPerson(contact);
                existing.setPhone(phone);
                existing.setEmail(email);
                existing.setCity(city);
                existing.setCreditLimit(credit);
                repository.updateRetailer(existing);
            } else {
                if (repository.getRetailer(id) != null) {
                    JOptionPane.showMessageDialog(dialog, "Retailer ID already exists!", "Duplicate ID", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Retailer r = new Retailer(id, name, contact, phone, email, city, credit);
                repository.addRetailer(r);
            }

            loadRetailerData();
            dialog.dispose();
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
