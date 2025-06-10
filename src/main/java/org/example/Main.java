package org.example;

import javax.swing.*;

public class Main extends JFrame {
    public static void main(String[] args) {
        // show an option to allow user to choose who they are
        Object[] options = { "Moderator", "Participant" };
        int choice = JOptionPane.showOptionDialog(null, "Choose your role:", "Role Selection",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        // start threads based on selection
        if (choice == 0) {
            Thread moderatorThread = new Thread(new Publisher());
            moderatorThread.start();
            SwingUtilities.invokeLater(() -> new UserSelection(true));
        } else if (choice == 1) {
            Thread participantThread = new Thread(new Subscriber());
            participantThread.start();
            SwingUtilities.invokeLater(() -> new UserSelection(false));
        } else {
            System.exit(0); // quit if user closes window
        }
    }
}
