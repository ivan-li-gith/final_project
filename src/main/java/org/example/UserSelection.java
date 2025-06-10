package org.example;

import javax.swing.*;
import java.awt.*;

public class UserSelection extends JFrame {
    private final Repository repo = Repository.getInstance();

    public UserSelection(boolean isModerator) {
        // draw the frane
        setTitle("Planning Poker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        if (isModerator) {
            setTitle("Planning Poker - Moderator Login");
            LoginController loginController = new LoginController(this);
            LoginView loginView = new LoginView(loginController);
            setContentPane(loginView);
        } else {
            setTitle("Planning Poker - Participant");
            showWaitingScreen();
            repo.addPropertyChangeListener("room", evt -> {
                SwingUtilities.invokeLater(() -> {
                    setTitle("Planning Poker - Participant Login");
                    LoginController loginController = new LoginController(this);
                    LoginView loginView = new LoginView(loginController);
                    setContentPane(loginView);
                    revalidate();
                    repaint();
                });
            });
        }
        setVisible(true);
    }

    // waiting screen for participants waiting for the moderator to make a room
    private void showWaitingScreen() {
        JPanel waitingPanel = new JPanel(new BorderLayout());
        waitingPanel.setBackground(new Color(240, 245, 250));
        JLabel waitingLabel = new JLabel("Waiting for a publisher to create a room...", SwingConstants.CENTER);
        waitingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        waitingPanel.add(waitingLabel, BorderLayout.CENTER);
        setContentPane(waitingPanel);
    }
}
