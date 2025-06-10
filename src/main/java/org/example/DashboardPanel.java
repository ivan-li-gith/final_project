package org.example;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {
    public DashboardPanel(Main main, String myName) {
        setLayout(new BorderLayout(10, 10));

        JLabel roomTitle = new JLabel("Room: " + Repository.getInstance().getRoomName());
        roomTitle.setFont(new Font("Arial", Font.BOLD, 18));
        roomTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(roomTitle, BorderLayout.NORTH);

        SouthPanel southPanel = new SouthPanel();
        EastPanel eastPanel = new EastPanel(main, southPanel, myName);
        CardsPanel cardsPanel = new CardsPanel(eastPanel, myName);

        add(cardsPanel, BorderLayout.CENTER);
        add(eastPanel, BorderLayout.EAST);
        add(southPanel, BorderLayout.SOUTH);

        eastPanel.refreshParticipants();
    }
}
