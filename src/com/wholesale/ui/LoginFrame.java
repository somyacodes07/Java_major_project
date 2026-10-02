package com.wholesale.ui;

import com.wholesale.model.User;
import com.wholesale.service.DataRepository;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Beautiful and user-friendly login interface.
 */
public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkShowPassword;
    private JLabel lblError;

    public LoginFrame() {
        setTitle("Wholesale Order Processing System - Login");
        setSize(460, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIUtils.BG_LIGHT);

        // Header Panel (Branding)
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(UIUtils.PRIMARY_NAVY);
        headerPanel.setBorder(new EmptyBorder(30, 25, 25, 25));

        JLabel lblBadge = new JLabel("CASE STUDY NO. 119");
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblBadge.setForeground(new Color(148, 163, 184)); // Slate 400
        lblBadge.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("Wholesale Order Processing");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Distributor & Retailer Management Portal");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(203, 213, 225));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(lblBadge);
        headerPanel.add(Box.createVerticalStrut(6));
        headerPanel.add(lblTitle);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(lblSubtitle);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form Card Panel
        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(new CompoundBorder(
                new EmptyBorder(25, 35, 25, 35),
                new LineBorder(UIUtils.BORDER_COLOR, 1, true)
        ));

        JPanel innerForm = new JPanel();
        innerForm.setLayout(new BoxLayout(innerForm, BoxLayout.Y_AXIS));
        innerForm.setBackground(Color.WHITE);
        innerForm.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblLoginPrompt = new JLabel("Sign In to Your Account");
        lblLoginPrompt.setFont(UIUtils.FONT_HEADER);
        lblLoginPrompt.setForeground(UIUtils.TEXT_PRIMARY);
        lblLoginPrompt.setAlignmentX(Component.LEFT_ALIGNMENT);
        innerForm.add(lblLoginPrompt);
        innerForm.add(Box.createVerticalStrut(15));

        // Username
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(UIUtils.FONT_REGULAR_BOLD);
        lblUser.setForeground(UIUtils.TEXT_PRIMARY);
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        innerForm.add(lblUser);
        innerForm.add(Box.createVerticalStrut(5));

        txtUsername = UIUtils.createStyledTextField(20);
        txtUsername.setText("admin"); // convenient default
        txtUsername.setMaximumSize(new Dimension(Short.MAX_VALUE, 36));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        innerForm.add(txtUsername);
        innerForm.add(Box.createVerticalStrut(12));

        // Password
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(UIUtils.FONT_REGULAR_BOLD);
        lblPass.setForeground(UIUtils.TEXT_PRIMARY);
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        innerForm.add(lblPass);
        innerForm.add(Box.createVerticalStrut(5));

        txtPassword = UIUtils.createStyledPasswordField(20);
        txtPassword.setText("admin123"); // convenient default
        txtPassword.setMaximumSize(new Dimension(Short.MAX_VALUE, 36));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        innerForm.add(txtPassword);
        innerForm.add(Box.createVerticalStrut(8));

        // Show password checkbox
        chkShowPassword = new JCheckBox("Show Password");
        chkShowPassword.setFont(UIUtils.FONT_SMALL);
        chkShowPassword.setBackground(Color.WHITE);
        chkShowPassword.setFocusPainted(false);
        chkShowPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        chkShowPassword.addActionListener(e -> {
            if (chkShowPassword.isSelected()) {
                txtPassword.setEchoChar((char) 0);
            } else {
                txtPassword.setEchoChar('•');
            }
        });
        innerForm.add(chkShowPassword);
        innerForm.add(Box.createVerticalStrut(12));

        // Error message label
        lblError = new JLabel(" ");
        lblError.setFont(UIUtils.FONT_SMALL);
        lblError.setForeground(UIUtils.DANGER_RED);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);
        innerForm.add(lblError);
        innerForm.add(Box.createVerticalStrut(8));

        // Login Button
        JButton btnLogin = UIUtils.createPrimaryButton("Login Now");
        btnLogin.setMaximumSize(new Dimension(Short.MAX_VALUE, 38));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.addActionListener(this::handleLogin);
        innerForm.add(btnLogin);
        innerForm.add(Box.createVerticalStrut(12));

        // Demo credentials hint
        JLabel lblHint = new JLabel("<html><center>Demo: <b>admin</b> / <b>admin123</b> &nbsp;|&nbsp; <b>sales</b> / <b>sales123</b></center></html>");
        lblHint.setFont(UIUtils.FONT_SMALL);
        lblHint.setForeground(UIUtils.TEXT_MUTED);
        lblHint.setAlignmentX(Component.CENTER_ALIGNMENT);
        innerForm.add(lblHint);

        formCard.add(innerForm);
        mainPanel.add(formCard, BorderLayout.CENTER);

        // Bind Enter key to login
        getRootPane().setDefaultButton(btnLogin);

        setContentPane(mainPanel);
    }

    private void handleLogin(ActionEvent e) {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Please enter both username and password.");
            return;
        }

        User user = DataRepository.getInstance().authenticateUser(username, password);
        if (user != null) {
            lblError.setText(" ");
            // Open Main Dashboard
            SwingUtilities.invokeLater(() -> {
                MainDashboardFrame dashboard = new MainDashboardFrame(user);
                dashboard.setVisible(true);
                this.dispose();
            });
        } else {
            lblError.setText("Invalid username or password. Please try again.");
        }
    }
}
