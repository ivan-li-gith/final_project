package org.example;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class VotingController implements PropertyChangeListener {
    private final VotingView view;
    private final UserSelection window;
    private final Repository repo = Repository.getInstance();
    private final String myName;
    private final boolean isModerator;
    private Timer timer;
    private int seconds = 0;

    public VotingController(UserSelection window, String myName, boolean isModerator) {
        this.window = window;
        this.myName = myName;
        this.isModerator = isModerator;
        this.view = new VotingView(this, isModerator);
        repo.addPropertyChangeListener(this);
        refreshParticipants(); // Initial load
    }

    public VotingView getView() {
        return view;
    }

    public void handleStartVoting() {
        if (repo.getSelectedStory() == null) {
            JOptionPane.showMessageDialog(view, "Select a story first!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!repo.isVotingStarted()) {
            repo.setVotingStarted(true);
            view.setStartButtonVisible(false);
            view.setFinishVotingButtonVisible(true);
            startTimer();
        }
    }

    public void handleFinishVoting() {
        view.setFinishVotingButtonVisible(false);
        repo.markFinishedVoting(myName);
        checkAndShowResults();
    }

    private void checkAndShowResults() {
        if (repo.getFinishedVoting().size() == repo.getParticipants().size()) {
            new ResultsDialog(repo.getVotesWithNames(), repo.calculateVoteSum()).setVisible(true);
            repo.accumulateScoreForStory();
            repo.completeCurrentStory();
            view.setFinishVotingButtonVisible(false);
            view.setStartButtonVisible(true);
            stopTimer();

            // Check AFTER the story has been removed
            if (repo.getStories().isEmpty()) {
                new ResultsDialog(repo.getPlayerScores(), 0).setTitle("Final Score Summary");
                new ResultsDialog(repo.getPlayerScores(), 0).setVisible(true);
            }
        }
    }

    private void refreshParticipants() {
        List<String> participantNames = new ArrayList<>();
        for (Participant p : repo.getParticipants()) {
            participantNames.add(p.getName());
        }
        view.updateParticipantList(participantNames);
    }

    private void startTimer() {
        stopTimer(); // Ensure no multiple timers are running
        timer = new Timer(1000, e -> {
            seconds++;
            view.updateTimer(String.format("%02d:%02d", seconds / 60, seconds % 60));
        });
        timer.start();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
        seconds = 0;
        view.updateTimer("00:00");
    }

    public void showFinishVotingButton() {
        view.setFinishVotingButtonVisible(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "participants":
            case "selectedStory": // Also refresh when a new story is selected to clear checks
                refreshParticipants();
                break;
            case "finishedVoting":
                refreshParticipants(); // Update the checkmarks
                checkAndShowResults();
                break;
            case "votingStarted":
                if (repo.isVotingStarted()) {
                    view.setStartButtonVisible(false);
                    view.setFinishVotingButtonVisible(true);
                    startTimer();
                }
                break;
        }
    }
}