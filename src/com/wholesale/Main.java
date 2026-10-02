package com.wholesale;

import com.wholesale.ui.LoginFrame;

import javax.swing.*;

/**
 * Application Entry Point for Wholesale Order Processing System (Case Study 119).
 */
public class Main {
    public static void main(String[] args) {
        // Set native system look and feel for crisp rendering
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fallback to default Swing Look & Feel
        }

        // Launch on Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
