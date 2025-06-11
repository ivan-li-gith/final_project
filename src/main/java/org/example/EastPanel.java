package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EastPanel extends JPanel {
    private final UserSelection window;
    private final StoryView southPanel;
    private final Repository repo = Repository.getInstance();
    private final DefaultListModel<String> participantModel = new DefaultListModel<>();
    private Timer timer;
    private int seconds = 0;
    private JLabel timerLabel;
    private JButton startButton;
    private JButton finishVotingButton;
    private final String myName;

    public EastPanel(UserSelection main, StoryView southPanel, String myName) {
        this.window = main;
        this.southPanel = southPanel;
        this.myName = myName;

        repo.addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case "participants":
                    refreshParticipants();
                    break;
                case "finishedVoting":
                    checkAndShowResults();  // <-- This line is critical!
                    break;
            }
        });

        initializeUI();
        refreshParticipants();
    }

    private void initializeUI() {
        Color redBg = new Color(255, 127, 127);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(300, 600));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(redBg);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(redBg);

        // Timer
        JPanel timerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timerPanel.setBackground(redBg);
        timerLabel = new JLabel("00:00");
        timerPanel.add(timerLabel);
        content.add(timerPanel);

        // Participant List
        JList<String> participantList = new JList<>(participantModel);
        participantList.setBackground(redBg);
        JScrollPane participantScroll = new JScrollPane(participantList);
        participantScroll.setBorder(BorderFactory.createTitledBorder("Participants"));
        participantScroll.getViewport().setBackground(redBg);
        content.add(participantScroll);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 0, 10));
        buttonPanel.setBackground(redBg);

        buttonPanel.add(createButton("Add Story", this::openStoryPanel));
        buttonPanel.add(createButton("Reset All", this::resetSession));

        startButton = new JButton("Start Voting");
        startButton.addActionListener(e -> {
            if (repo.getSelectedStory() == null) {
                JOptionPane.showMessageDialog(this, "Select a story first!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!repo.isVotingStarted()) {
                repo.setVotingStarted(true);
                startButton.setVisible(false);
                showFinishVotingButton();
                startTimer();
            }
        });
        buttonPanel.add(startButton);

        finishVotingButton = new JButton("Finish Voting");
        finishVotingButton.setVisible(false);
        finishVotingButton.addActionListener(e -> {
            finishVotingButton.setEnabled(false);
            repo.markFinishedVoting(myName);
            checkAndShowResults();
        });

        buttonPanel.add(finishVotingButton);
        content.add(buttonPanel);
        add(content, BorderLayout.NORTH);
    }

    private JButton createButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.addActionListener(e -> action.run());
        return button;
    }

    public void showFinishVotingButton() {
        finishVotingButton.setVisible(true);
        finishVotingButton.setEnabled(true);
    }

    private void checkAndShowResults() {
        if (repo.getFinishedVoting().size() == repo.getParticipants().size()) {
            new ResultsDialog(repo.getVoteCounts(), repo.calculateVoteSum()).setVisible(true);
            repo.completeCurrentStory();
//            southPanel.refreshStoryLists();
            finishVotingButton.setVisible(false);
            if (startButton != null) {
                startButton.setVisible(true);
            }
            stopTimer();
        }
    }

    private void startTimer() {
        timer = new Timer(1000, e -> {
            seconds++;
            timerLabel.setText(String.format("%02d:%02d", seconds / 60, seconds % 60));
        });
        timer.start();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
        seconds = 0;
        timerLabel.setText("00:00");
    }

    public void refreshParticipants() {
        participantModel.clear();
        for (Participant p : repo.getParticipants()) {
            participantModel.addElement(p.getName() + (p.hasVoted() ? " (✓)" : ""));
        }
    }

    private void openStoryPanel() {
        StoriesNanny storiesNanny = new StoriesNanny(window, myName);
        StoriesPanel storiesPanel = new StoriesPanel(storiesNanny);
        window.setTitle("Add Story");
        window.setContentPane(storiesPanel);
        window.setSize(600, 400);
        window.setLocationRelativeTo(null);
        window.revalidate();
        window.repaint();
    }

    private void resetSession() {
        repo.getParticipants().clear();
        stopTimer();
        participantModel.clear();
        finishVotingButton.setVisible(false);
        startButton.setVisible(true);
    }
}
