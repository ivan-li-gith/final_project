package org.example;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class CardsView extends JPanel {
    private static final String[] CARD_VALUES = {"0", "½", "1", "2", "3", "5", "8", "10", "20", "40", "100", "?"};
    private final Map<String, JButton> cardButtons = new HashMap<>();
    private final CardsController controller;

    public CardsView(CardsController controller) {
        this.controller = controller;
        // This was the missing call!
        initializeUI();
    }

    public String[] getCardValues() {
        return CARD_VALUES;
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 245, 245));
        add(createCardGrid(), BorderLayout.CENTER);
    }

    private JPanel createCardGrid() {
        JPanel grid = new JPanel(new GridLayout(4, 3, 10, 10));
        grid.setBackground(Color.WHITE);
        for (String value : CARD_VALUES) {
            JButton card = createCardButton(value);
            cardButtons.put(value, card);
            grid.add(card);
        }
        return grid;
    }

    private JButton createCardButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);

        Border defaultBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        );

        Border hoverBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(30, 144, 255), 3),
                BorderFactory.createEmptyBorder(7, 12, 7, 12)
        );

        button.setBorder(defaultBorder);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Tell the controller the value of the card that was clicked.
                controller.handleVote(text);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // The view handles its own hover effect.
                button.setBorder(hoverBorder);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // The view resets its own hover effect.
                button.setBorder(defaultBorder);
            }
        });
        return button;
    }

    /**
     * This method is called by the controller to update the background color of all cards.
     * @param colorMap A map where the key is the card value and the value is the new Color.
     */
    public void updateCardColors(Map<String, Color> colorMap) {
        cardButtons.forEach((value, button) -> {
            button.setBackground(colorMap.getOrDefault(value, Color.WHITE));
        });
    }
}