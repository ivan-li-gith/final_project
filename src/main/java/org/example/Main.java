package org.example;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    public Main() {
        setTitle("Planning Poker - Login - Publisher");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        LoginNanny loginNanny = new LoginNanny(this);
        LoginPanel loginPanel = new LoginPanel(loginNanny);
        setContentPane(loginPanel);
        setVisible(true);
    }

    public static void main(String[] args) {
        Thread publisherThread = new Thread(new Publisher());
        publisherThread.start();
        SwingUtilities.invokeLater(Main::new);
    }
}
