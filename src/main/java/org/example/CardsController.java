package org.example;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

public class CardsController implements PropertyChangeListener {
    private final CardsView cardView;
    private final VotingController votingController;
    private final String name;
    private final Repository repo = Repository.getInstance();

    public CardsController(VotingController votingController, String name) {
        this.votingController = votingController;
        this.name = name;
        this.cardView = new CardsView(this);
        repo.addPropertyChangeListener(this);
    }

    public CardsView getView() {
        return cardView;
    }

    // tells users to select a story and press start button and shows finish button when a card is selected
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
        votingController.showFinishVotingButton();
    }

    private void updateCardColors() {
        Map<String, Color> colorMap = new HashMap<>();
        for (String cardValue : cardView.getCardValues()) {
            colorMap.put(cardValue, getCardColor(cardValue));
        }
        cardView.updateCardColors(colorMap);
    }

    // clicked = blue else it is white
    private Color getCardColor(String value) {
        long voteCount = repo.getAllVotes().stream()
                .filter(v -> v.equals(value))
                .count();
        if (voteCount > 0) {
            return new Color(173, 216, 230, (int) (200 * Math.min(voteCount / 3.0, 1)));
        }
        return Color.WHITE;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("selectedStory".equals(evt.getPropertyName()) || "participants".equals(evt.getPropertyName())) {
            updateCardColors();
        }
    }
}