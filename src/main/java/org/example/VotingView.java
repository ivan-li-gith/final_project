package org.example;

import javax.swing.*;
import java.awt.*;

public class VotingView extends JPanel {
    private final Repository repo = Repository.getInstance();

    public VotingView(UserSelection window, String myName) {
        setLayout(new BorderLayout(10, 10));

        JLabel roomTitle = new JLabel("Room: " + repo.getRoomName());
        roomTitle.setFont(new Font("Arial", Font.BOLD, 18));
        roomTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(roomTitle, BorderLayout.NORTH);

        SouthPanel southPanel = new SouthPanel();
        EastPanel eastPanel = new EastPanel(window, southPanel, myName);
        CardsPanel cardsPanel = new CardsPanel(eastPanel, myName);

        add(cardsPanel, BorderLayout.CENTER);
        add(eastPanel, BorderLayout.EAST);
        add(southPanel, BorderLayout.SOUTH);
        eastPanel.refreshParticipants();
    }
}
