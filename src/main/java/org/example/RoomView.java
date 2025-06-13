package org.example;

import javax.swing.*;
import java.awt.*;

/**
 * The view component for the room creation screen, used only by the Moderator.
 * This class is responsible for creating and displaying the UI elements that
 * allow the Moderator to enter a name for the room and select a voting deck mode.
 * User actions are forwarded to the RoomController.
 *
 * @author Aadi Dhanda
 * @version 1.0
 */

public class RoomView extends JPanel {
    public RoomView(RoomController roomController) {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(240, 245, 250));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Title
        JLabel title = new JLabel("Create New Room", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(33, 74, 125));
        add(title, BorderLayout.NORTH);

        // Center Input Area
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(240, 245, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name Field
        JLabel nameLabel = new JLabel("Room Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField nameField = new JTextField("CSC307");
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        // Mode ComboBox
        JLabel modeLabel = new JLabel("Deck Mode:");
        modeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        String[] options = {"Scrum", "Fibonacci", "Sequential", "Hours", "T-shirt", "Custom deck"};
        JComboBox<String> comboBox = new JComboBox<>(options);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(modeLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(comboBox, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Create Button
        JButton createButton = new JButton("Create Room");
        createButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        createButton.setBackground(new Color(200, 220, 240));
        createButton.setFocusPainted(false);
        createButton.setPreferredSize(new Dimension(140, 35));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 245, 250));
        buttonPanel.add(createButton);
        add(buttonPanel, BorderLayout.SOUTH);

        createButton.addActionListener(e ->
                roomController.createRoom(nameField.getText(), (String) comboBox.getSelectedItem())
        );
    }

}
