package org.example;

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
