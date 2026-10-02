package com.wholesale.ui;

import com.wholesale.model.Invoice;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

/**
 * Clean modal dialog displaying a complete commercial wholesale tax invoice.
 */
public class InvoiceDialog extends JDialog {
    private final Invoice invoice;

    public InvoiceDialog(Frame owner, Invoice invoice) {
        super(owner, "Official Commercial Invoice - " + invoice.getInvoiceId(), true);
        this.invoice = invoice;
        setSize(780, 620);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        initComponents();
    }

    private void initComponents() {
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(UIUtils.BG_LIGHT);
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Invoice Text View
        JTextArea txtInvoice = new JTextArea(invoice.generateFormattedSummary());
        txtInvoice.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtInvoice.setEditable(false);
        txtInvoice.setBackground(Color.WHITE);
        txtInvoice.setCaretPosition(0);
        txtInvoice.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(txtInvoice);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR));
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Action Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        actionPanel.setBackground(UIUtils.BG_LIGHT);

        JButton btnCopy = UIUtils.createSecondaryButton("Copy Invoice Text");
        btnCopy.addActionListener(e -> {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                    new StringSelection(txtInvoice.getText()), null);
            JOptionPane.showMessageDialog(this, "Invoice text copied to clipboard!", "Copied", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnPrint = UIUtils.createPrimaryButton("Print Invoice");
        btnPrint.addActionListener(e -> {
            try {
                boolean complete = txtInvoice.print();
                if (complete) {
                    JOptionPane.showMessageDialog(this, "Invoice printing complete.", "Print Result", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Print error: " + ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnClose = UIUtils.createSecondaryButton("Close");
        btnClose.addActionListener(e -> dispose());

        actionPanel.add(btnCopy);
        actionPanel.add(btnPrint);
        actionPanel.add(btnClose);

        contentPanel.add(actionPanel, BorderLayout.SOUTH);
        add(contentPanel);
    }
}
