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
        initializeUI();
    }

    public String[] getCardValues() {
        return CARD_VALUES;
    }

    // draws out the cards and places it in the center
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 245, 245));
        add(createCardGrid(), BorderLayout.CENTER);
    }

    // draws the card grid
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

    // creates all the cards and makes it into buttons for people to click on
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
                controller.handleVote(text); // tells controller the value of the card clicked
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBorder(hoverBorder); // hover effect
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBorder(defaultBorder); // no hover effect
            }
        });
        return button;
    }

    // update background color and all cards
    public void updateCardColors(Map<String, Color> colorMap) {
        cardButtons.forEach((value, button) -> {
            button.setBackground(colorMap.getOrDefault(value, Color.WHITE));
        });
    }
}