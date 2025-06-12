package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VotingView extends JPanel {
    private final VotingController controller;
    private final DefaultListModel<String> participantModel = new DefaultListModel<>();
    private final JLabel timerLabel;
    private final JButton startButton;
    private final JButton finishVotingButton;

    public VotingView(VotingController controller, boolean isModerator) {
        this.controller = controller;

        Color panelBg = new Color(255, 240, 240); // A softer red
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(300, 600));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(panelBg);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(panelBg);

        // Timer
        JPanel timerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timerPanel.setBackground(panelBg);
        timerLabel = new JLabel("00:00");
        timerPanel.add(timerLabel);
        content.add(timerPanel);

        // Participant List
        JList<String> participantList = new JList<>(participantModel);
        participantList.setBackground(panelBg);
        JScrollPane participantScroll = new JScrollPane(participantList);
        participantScroll.setBorder(BorderFactory.createTitledBorder("Participants"));
        participantScroll.getViewport().setBackground(panelBg);
        content.add(participantScroll);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 0, 10));
        buttonPanel.setBackground(panelBg);

//        buttonPanel.add(createButton("Add Story", controller::handleAddStory));
//        buttonPanel.add(createButton("Reset Session", controller::handleResetSession));


        startButton = createButton("Start Voting", controller::handleStartVoting);

        if (isModerator) {
            buttonPanel.add(startButton);
        } else {
            startButton.setEnabled(false);
        }


        finishVotingButton = createButton("Finish Voting", controller::handleFinishVoting);
        finishVotingButton.setVisible(false);
        buttonPanel.add(finishVotingButton);

        content.add(buttonPanel);
        add(content, BorderLayout.NORTH);
    }

    private JButton createButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.addActionListener(e -> action.run());
        return button;
    }

    public void updateTimer(String time) {
        timerLabel.setText(time);
    }

    public void updateParticipantList(List<String> participants) {
        participantModel.clear();
        for (String participant : participants) {
            participantModel.addElement(participant);
        }
    }

    public void setStartButtonVisible(boolean visible) {
        startButton.setVisible(visible);
    }

    public void setFinishVotingButtonVisible(boolean visible) {
        finishVotingButton.setVisible(visible);
        finishVotingButton.setEnabled(visible);
    }
}