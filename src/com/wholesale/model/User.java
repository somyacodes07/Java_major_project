package com.wholesale.model;

/**
 * Represents system users who can log in to the wholesale processing system.
 * Demonstrates: OOP Encapsulation (private fields, public getters/setters).
 */
public class User {
    private String username;
    private String password;
    private String fullName;
    private String role; // e.g. "Distributor Admin", "Sales Manager"

    public User(String username, String password, String fullName, String role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public boolean validatePassword(String inputPassword) {
        return this.password != null && this.password.equals(inputPassword);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
