package org.example;

import javax.swing.*;
import java.awt.*;

public class DashboardView extends JPanel {
    private final Repository repo = Repository.getInstance();

    public DashboardView(UserSelection window, String myName, boolean isModerator) {
        setLayout(new BorderLayout(10, 10));

        // room title
        JLabel roomTitle = new JLabel("Room: " + repo.getRoomName());
        roomTitle.setFont(new Font("Arial", Font.BOLD, 18));
        roomTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(roomTitle, BorderLayout.NORTH);

        // active and completed stories
        StoryController storyController = new StoryController(isModerator);
        add(storyController.getStoryView(), BorderLayout.SOUTH);

        // start/ finish voting button and participant list
        VotingController votingController = new VotingController(window, myName, isModerator);
        add(votingController.getView(), BorderLayout.EAST);

        // cards
        CardsController cardsController = new CardsController(votingController, myName);
        add(cardsController.getView(), BorderLayout.CENTER);
    }
}
