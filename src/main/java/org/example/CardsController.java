package org.example;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

public class CardsController implements PropertyChangeListener {
    private final CardsView cardView;
    private final EastPanel eastPanel;
    private final String name;
    private final Repository repo = Repository.getInstance();

    public CardsController(EastPanel eastPanel, String name) {
        this.eastPanel = eastPanel;
        this.name = name;
        this.cardView = new CardsView(this);
        repo.addPropertyChangeListener(this);
    }

    public CardsView getView() {
        return cardView;
    }

    public void handleVote(String voteValue) {
        if (repo.getSelectedStory() == null) {
            JOptionPane.showMessageDialog(cardView, "Please select a story before voting!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!repo.isVotingStarted()) {
            JOptionPane.showMessageDialog(cardView, "Press 'Start Voting' before submitting a vote!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        repo.updateVote(name, voteValue);
        updateCardColors();
        eastPanel.showFinishVotingButton();
    }

    private void updateCardColors() {
        Map<String, Color> colorMap = new HashMap<>();
        // Make sure all cards have a color in the map, even if it's white
        for (String cardValue : cardView.getCardValues()) {
            colorMap.put(cardValue, getCardColor(cardValue));
        }
        cardView.updateCardColors(colorMap);
    }

    private Color getCardColor(String value) {
        long voteCount = repo.getAllVotes().stream()
                .filter(v -> v.equals(value))
                .count();
        if (voteCount > 0) {
            // Calculate a semi-transparent blue
            return new Color(173, 216, 230, (int) (200 * Math.min(voteCount / 3.0, 1)));
        }
        return Color.WHITE;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // If votes change for any reason (e.g., a new story is selected), update the colors
        if ("selectedStory".equals(evt.getPropertyName()) || "participants".equals(evt.getPropertyName())) {
            updateCardColors();
        }
    }
}