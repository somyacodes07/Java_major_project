package com.wholesale.model;

/**
 * Represents a registered Retailer ordering bulk goods from the wholesale distributor.
 * Demonstrates: OOP Encapsulation, Business Field Constraints.
 */
public class Retailer {
    private String retailerId;
    private String businessName;
    private String contactPerson;
    private String phone;
    private String email;
    private String city;
    private double creditLimit;
    private double currentOutstandingBalance;

    public Retailer(String retailerId, String businessName, String contactPerson,
                    String phone, String email, String city, double creditLimit) {
        this.retailerId = retailerId;
        this.businessName = businessName;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.email = email;
        this.city = city;
        this.creditLimit = creditLimit;
        this.currentOutstandingBalance = 0.0;
    }

    public String getRetailerId() {
        return retailerId;
    }

    public void setRetailerId(String retailerId) {
        this.retailerId = retailerId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(double creditLimit) {
        this.creditLimit = creditLimit;
    }

    public double getCurrentOutstandingBalance() {
        return currentOutstandingBalance;
    }

    public void addBalance(double amount) {
        this.currentOutstandingBalance += amount;
    }

    public void reduceBalance(double amount) {
        this.currentOutstandingBalance = Math.max(0, this.currentOutstandingBalance - amount);
    }

    public double getAvailableCredit() {
        return creditLimit - currentOutstandingBalance;
    }

    @Override
    public String toString() {
        return retailerId + " - " + businessName + " (" + city + ")";
    }
}
