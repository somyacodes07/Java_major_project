package com.wholesale.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Represents the official commercial billing invoice for a wholesale order.
 * Generates structured invoice summaries and tracks payment status.
 */
public class Invoice {
    private String invoiceId;
    private Order order;
    private String invoiceDate;
    private String dueDate;
    private String paymentStatus; // "PAID", "PENDING", "CREDIT_ACCOUNT"

    public Invoice(String invoiceId, Order order) {
        this.invoiceId = invoiceId;
        this.order = order;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        this.invoiceDate = sdf.format(now);

        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.DAY_OF_MONTH, 30); // 30-day wholesale credit term
        this.dueDate = sdf.format(cal.getTime());

        this.paymentStatus = "PENDING";
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public Order getOrder() {
        return order;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    /**
     * Generates a formatted text summary representation of the invoice.
     */
    public String generateFormattedSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================================\n");
        sb.append("                          WHOLESALE COMMERCIAL TAX INVOICE                             \n");
        sb.append("========================================================================================\n");
        sb.append(String.format("Invoice No   : %-25s Date     : %s\n", invoiceId, invoiceDate));
        sb.append(String.format("Order Ref    : %-25s Due Date : %s\n", order.getOrderId(), dueDate));
        sb.append(String.format("Status       : %-25s Payment  : %s\n", order.getStatus(), paymentStatus));
        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append("BILL TO (RETAILER):\n");
        Retailer r = order.getRetailer();
        sb.append(String.format("Name: %s (ID: %s)\n", r.getBusinessName(), r.getRetailerId()));
        sb.append(String.format("Contact: %s | Phone: %s | Email: %s\n", r.getContactPerson(), r.getPhone(), r.getEmail()));
        sb.append(String.format("City: %s | Credit Limit: ₹%.2f | Balance: ₹%.2f\n", r.getCity(), r.getCreditLimit(), r.getCurrentOutstandingBalance()));
        sb.append("========================================================================================\n");
        sb.append(String.format("%-10s %-28s %-8s %-12s %-10s %-12s\n",
                "Prod ID", "Description", "Qty", "Base Rate", "Discount", "Line Total"));
        sb.append("----------------------------------------------------------------------------------------\n");

        for (OrderItem item : order.getItems()) {
            sb.append(String.format("%-10s %-28s %-8d ₹%-11.2f %-9.1f%% ₹%-11.2f\n",
                    item.getProduct().getProductId(),
                    truncate(item.getProduct().getName(), 28),
                    item.getQuantity(),
                    item.getBaseUnitPrice(),
                    item.getDiscountPercent(),
                    item.getLineTotal()));
        }

        sb.append("========================================================================================\n");
        sb.append(String.format("%60s : ₹%12.2f\n", "Original Gross Total", order.getOriginalTotalWithoutDiscount()));
        sb.append(String.format("%60s : ₹%12.2f (Tiered Volume Savings)\n", "Total Bulk Discount", order.getTotalSavings()));
        sb.append(String.format("%60s : ₹%12.2f\n", "Taxable Subtotal", order.getSubtotal()));
        sb.append(String.format("%60s : ₹%12.2f (%.1f%% Wholesale GST)\n", "GST / Tax", order.getTaxAmount(), order.getTaxRate() * 100));
        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(String.format("%60s : ₹%12.2f\n", "FINAL NET PAYABLE", order.getFinalTotal()));
        sb.append("========================================================================================\n");
        sb.append("Delivery Status: ").append(order.getStatus().getDisplayName()).append("\n");
        sb.append("Delivery Notes : ").append(order.getDeliveryNotes()).append("\n");
        sb.append("========================================================================================\n");
        sb.append("               Thank you for your bulk business! Goods once sold are warranty protected.\n");
        sb.append("========================================================================================\n");
        return sb.toString();
    }

    private String truncate(String val, int maxLen) {
        if (val.length() <= maxLen) return val;
        return val.substring(0, maxLen - 3) + "...";
    }
}
