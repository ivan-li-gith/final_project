package org.example;

import javax.swing.*;
import java.awt.*;

public class StoriesPanel extends JPanel {

    public StoriesPanel(StoriesNanny storiesNanny) {
        setLayout(new BorderLayout());
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(240, 245, 250));  // Light background
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title Label
        JLabel titleLabel = new JLabel("📝 Create a New Story", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(33, 74, 125));
        add(titleLabel, BorderLayout.NORTH);

        // Story Text Area with titled border
        JTextArea storyTextArea = new JTextArea();
        storyTextArea.setLineWrap(true);
        storyTextArea.setWrapStyleWord(true);
        storyTextArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        storyTextArea.setBackground(new Color(255, 255, 255));
        storyTextArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Enter story lines (one per line)"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JScrollPane scrollPane = new JScrollPane(storyTextArea);
        scrollPane.setPreferredSize(new Dimension(450, 200));
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel with BoxLayout (horizontal alignment)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBackground(new Color(240, 245, 250));

        JButton saveAddNewButton = new JButton("💾 Save & Add New");
        JButton saveCloseButton = new JButton("✅ Save & Close");
        JButton cancelButton = new JButton("❌ Cancel");

        // Style the buttons
        JButton[] buttons = {saveAddNewButton, saveCloseButton, cancelButton};
        for (JButton btn : buttons) {
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            btn.setBackground(new Color(200, 220, 240));
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
            buttonPanel.add(btn);
        }

        add(buttonPanel, BorderLayout.SOUTH);


        saveAddNewButton.addActionListener(e -> storiesNanny.saveAndAddNew(storyTextArea.getText()));
        saveCloseButton.addActionListener(e -> storiesNanny.saveAndClose(storyTextArea.getText()));
        cancelButton.addActionListener(e -> storiesNanny.cancel());
    }
}
