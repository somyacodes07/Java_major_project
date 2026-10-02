package com.wholesale.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * UI Utilities to provide a modern, sleek, and consistent design across all views.
 */
public class UIUtils {
    // Color Palette
    public static final Color PRIMARY_NAVY = new Color(15, 23, 42); // Slate 900
    public static final Color ACCENT_BLUE = new Color(37, 99, 235); // Blue 600
    public static final Color ACCENT_BLUE_HOVER = new Color(29, 78, 216); // Blue 700
    public static final Color SUCCESS_GREEN = new Color(16, 185, 129); // Emerald 500
    public static final Color WARNING_AMBER = new Color(245, 158, 11); // Amber 500
    public static final Color DANGER_RED = new Color(239, 68, 68); // Red 500

    public static final Color BG_LIGHT = new Color(248, 250, 252); // Slate 50
    public static final Color CARD_BG = Color.WHITE;
    public static final Color BORDER_COLOR = new Color(226, 232, 240); // Slate 200
    public static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    public static final Color TEXT_MUTED = new Color(100, 116, 139);

    // Modern Fonts
    public static final Font FONT_HEADER_LARGE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_REGULAR_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_MONO = new Font("Consolas", Font.PLAIN, 12);

    public static JButton createStyledButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(FONT_REGULAR_BOLD);
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(8, 16, 8, 16));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    public static JButton createPrimaryButton(String text) {
        return createStyledButton(text, ACCENT_BLUE, Color.WHITE);
    }

    public static JButton createSecondaryButton(String text) {
        JButton btn = createStyledButton(text, Color.WHITE, TEXT_PRIMARY);
        btn.setBorder(new CompoundBorder(new LineBorder(BORDER_COLOR, 1, true), new EmptyBorder(7, 15, 7, 15)));
        return btn;
    }

    public static JButton createSuccessButton(String text) {
        return createStyledButton(text, SUCCESS_GREEN, Color.WHITE);
    }

    public static JButton createDangerButton(String text) {
        return createStyledButton(text, DANGER_RED, Color.WHITE);
    }

    public static JPanel createCardPanel() {
        JPanel card = new JPanel();
        card.setBackground(CARD_BG);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));
        return card;
    }

    public static JTextField createStyledTextField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setFont(FONT_REGULAR);
        tf.setBackground(Color.WHITE);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(ACCENT_BLUE);
        tf.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return tf;
    }

    public static JPasswordField createStyledPasswordField(int columns) {
        JPasswordField pf = new JPasswordField(columns);
        pf.setFont(FONT_REGULAR);
        pf.setBackground(Color.WHITE);
        pf.setForeground(TEXT_PRIMARY);
        pf.setCaretColor(ACCENT_BLUE);
        pf.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return pf;
    }

    public static void formatTable(JTable table) {
        table.setFont(FONT_REGULAR);
        table.setRowHeight(32);
        table.setGridColor(BORDER_COLOR);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setSelectionForeground(ACCENT_BLUE);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_SUBHEADER);
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(TEXT_PRIMARY);
        header.setPreferredSize(new Dimension(0, 36));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        leftRenderer.setBorder(new EmptyBorder(0, 8, 0, 8));
    }
}
