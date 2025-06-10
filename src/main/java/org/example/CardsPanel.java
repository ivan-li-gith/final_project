package org.example;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class CardsPanel extends JPanel {
    private static final String[] CARD_VALUES = {"0", "½", "1", "2", "3", "5", "8", "10", "20", "40", "100", "?"};
    private final Map<String, JButton> cardButtons = new HashMap<>();
    private final Repository repo = Repository.getInstance();
    private final EastPanel eastPanel;
    private final String myName;

    public CardsPanel(EastPanel eastPanel, String myName) {
        this.eastPanel = eastPanel;
        this.myName = myName;
        initializeUI();
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
                handleVote(button);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isVoted(button)) {
                    button.setBackground(new Color(230, 230, 230));
                    button.setBorder(hoverBorder);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!isVoted(button)) {
                    button.setBackground(Color.WHITE);
                    button.setBorder(defaultBorder);
                } else {
                    button.setBackground(getCardColor(text));
                    button.setBorder(defaultBorder);
                }
            }
        });

        return button;
    }

    private void handleVote(JButton button) {
        if (repo.getSelectedStory() == null) {
            JOptionPane.showMessageDialog(this, "Please select a story before voting!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!repo.isVotingStarted()) {
            JOptionPane.showMessageDialog(this, "Press 'Start Voting' before submitting a vote!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        repo.updateVote(myName, button.getText());
        updateCardColors();
        eastPanel.showFinishVotingButton();
    }

    private void updateCardColors() {
        cardButtons.forEach((value, button) -> {
            button.setBackground(getCardColor(value));
        });
    }

    private Color getCardColor(String value) {
        long voteCount = repo.getAllVotes().stream()
                .filter(v -> v.equals(value))
                .count();
        return voteCount > 0 ? new Color(173, 216, 230, (int) (200 * Math.min(voteCount / 3.0, 1))) : Color.WHITE;
    }

    private boolean isVoted(JButton button) {
        return !button.getBackground().equals(Color.WHITE);
    }
}
