package org.example;

import javax.swing.*;
import java.awt.*;

/**
 * The view component for the Taiga project import screen, used by the Moderator.
 * This class creates and displays the user interface for entering Taiga credentials
 * (username, password) and the project slug. User actions are forwarded to the
 * TaigaImportController to handle the API login and story fetching process.
 *
 * @author Aadi Dhanda
 * @version 1.0
 */

public class TaigaImportView extends JPanel {
    public TaigaImportView(TaigaImportController taigaImportController) {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(240, 245, 250));
        setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // connection to taiga label
        JLabel titleLabel = new JLabel("Connect to Taiga!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(33, 74, 125));

        // username input
        JTextField usernameField = new JTextField("ili02@calpoly.edu");
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        usernameField.setHorizontalAlignment(SwingConstants.CENTER);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        // password input
        JPasswordField passwordField = new JPasswordField("Calejissmc4@");
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordField.setHorizontalAlignment(SwingConstants.CENTER);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        // project slug input
        JTextField projectSlugField = new JTextField("zaxbie-lab2");
        projectSlugField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        projectSlugField.setHorizontalAlignment(SwingConstants.CENTER);
        projectSlugField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        projectSlugField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        // enter button
        JButton enterButton = new JButton("Enter");
        enterButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        enterButton.setBackground(new Color(173, 216, 230));
        enterButton.setFocusPainted(false);
        enterButton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

        // enter button action
        enterButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String((passwordField).getPassword());
            String projectSlug = projectSlugField.getText().trim();

            if (!username.isEmpty() && !password.isEmpty() && !projectSlug.isEmpty()) {
                taigaImportController.handleTaigaLogin(username, password, projectSlug);
            } else {
                JOptionPane.showMessageDialog(this, "Please enter username and password to continue.",
                        "Input Required", JOptionPane.WARNING_MESSAGE);
            }
        });

        // creating the form view
        JPanel formPanel = new JPanel();
        formPanel.setBackground(new Color(240, 245, 250));
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(30));
        formPanel.add(projectSlugField);
        formPanel.add(Box.createVerticalStrut(30));
        formPanel.add(enterButton);
        add(formPanel, BorderLayout.CENTER);
    }
}

