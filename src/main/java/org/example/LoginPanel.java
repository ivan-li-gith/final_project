package org.example;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    public LoginPanel(LoginNanny joinRoomNanny) {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(240, 245, 250));
        setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // Title label
        JLabel titleLabel = new JLabel("Let's Start!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(33, 74, 125));

        // Subtitle label
        JLabel subtitleLabel = new JLabel("Join the Room:", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitleLabel.setForeground(new Color(90, 90, 90));

        // Name text field with rounded border
        JTextField nameField = new JTextField("Enter your name");
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        nameField.setHorizontalAlignment(SwingConstants.CENTER);
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));


        // Enter button
        JButton enterButton = new JButton("Enter");
        enterButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        enterButton.setBackground(new Color(173, 216, 230));
        enterButton.setFocusPainted(false);
        enterButton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

        // Button action
        enterButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (!name.isEmpty() && !name.equals("Enter your name")) {
                joinRoomNanny.enterRoom(name);
            } else {
                JOptionPane.showMessageDialog(this, "Please enter your name to continue.",
                        "Input Required", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Center panel for form content
        JPanel formPanel = new JPanel();
        formPanel.setBackground(new Color(240, 245, 250));
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(subtitleLabel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(nameField);
        formPanel.add(Box.createVerticalStrut(30));
        formPanel.add(enterButton);

        add(formPanel, BorderLayout.CENTER);
    }

}