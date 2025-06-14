package org.example;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the voting panel which includes the participant list and voting controls.
 * This class manages the voting process, including starting the vote, tracking the timer,
 * handling the "Finish Voting" action from users, and determining when to display
 * the final results. It listens for changes in the central repository to keep the
 * UI in sync with the application state.
 *
 * @author Aadi Dhanda
 * @version 1.0
 */

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
        refreshParticipants();
    }

    public VotingView getView() {
        return view;
    }

    // to ensure they select the story first and have all the buttons show up when voting is started
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

    // shows the results of the vote
    private void checkAndShowResults() {
        if (repo.getFinishedVoting().size() == repo.getParticipants().size()) {
            new ResultsDialog(repo.getVotesWithNames(), repo.calculateVoteSum()).setVisible(true);
            repo.accumulateScoreForStory();
            repo.completeCurrentStory();
            view.setFinishVotingButtonVisible(false);
            view.setStartButtonVisible(true);
            stopTimer();

            // check after the story has been removed
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
        stopTimer(); // ensure no multiple timers are running
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
            case "selectedStory":
                refreshParticipants();
                break;
            case "finishedVoting":
                refreshParticipants();
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