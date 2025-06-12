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

        // cards, story, and participants views
//        CardsPanel cardsPanel = new CardsPanel(eastPanel, myName);

        StoryController storyController = new StoryController(isModerator);
        add(storyController.getStoryView(), BorderLayout.SOUTH);

//        EastPanel eastPanel = new EastPanel(window, storyController.getStoryView(), myName);
//        add(eastPanel, BorderLayout.EAST);

        VotingController votingController = new VotingController(window, myName, isModerator);
        add(votingController.getView(), BorderLayout.EAST);


        CardsController cardsController = new CardsController(votingController, myName);
        add(cardsController.getView(), BorderLayout.CENTER);





        add(roomTitle, BorderLayout.NORTH);
//        add(cardsPanel, BorderLayout.CENTER);
//        add(eastPanel, BorderLayout.EAST);
//        eastPanel.refreshParticipants();
    }
}
