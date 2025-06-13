package org.example;

/**
 * Controller for the login screen. This class handles the logic for a user
 * entering their name and joining the planning poker session. It adds the
 * new participant to the central repository and then transitions the UI to the
 * appropriate next screen based on whether the user is a Moderator or a Participant.
 *
 * @author Ivan Li
 * @version 1.0
 */

public class LoginController {
    private final UserSelection window;
    private String name;
    private final boolean isModerator;
    private final Repository repo = Repository.getInstance();

    public LoginController(UserSelection window, boolean isModerator) {
        this.window = window;
        this.isModerator = isModerator;
    }

    // adds the user's name to the repo and switches GUI to room creation
    public void enterRoom(String name) {
        this.name = name;
        repo.addParticipant(name);
        switchGUI();
    }

    private void switchGUI() {
        if (isModerator) {
            // moderators move to making a room
            window.setTitle("Room Creation");
            RoomController roomController = new RoomController(window, name, isModerator);
            RoomView roomView = new RoomView(roomController);
            window.setContentPane(roomView);
            window.setSize(500, 500);
        } else {
            // participants move to the dashboard view
            window.setTitle("Planning Poker Dashboard - Participant");
            DashboardView dashboardView = new DashboardView(window, name, isModerator);
            window.setContentPane(dashboardView);
            window.setSize(1000, 700);
        }

        window.setLocationRelativeTo(null);
        window.revalidate();
        window.repaint();
    }
}
