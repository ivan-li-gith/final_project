package org.example;

import javax.swing.*;

/**
 * The main entry point for the Planning Poker application.
 * This class is responsible for initializing the application by first prompting
 * the user to select their role (Moderator or Participant). Based on the user's
 * choice, it launches the appropriate background thread for network communication
 * (Publisher for Moderator, Subscriber for Participant) and then constructs the
 * initial user interface on the Swing Event Dispatch Thread.
 *
 * @author Ivan Li
 * @version 1.0
 */

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
