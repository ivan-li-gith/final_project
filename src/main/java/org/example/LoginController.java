package org.example;

public class LoginController {
    private final UserSelection window;
    private String name;
    private final Repository repo = Repository.getInstance();

    public LoginController(UserSelection window) {
        this.window = window;
    }

    // adds the user's name to the repo and switches GUI to room creation
    public void enterRoom(String name) {
        this.name = name;
        repo.addParticipant(name);
        switchGUI();
    }

    private void switchGUI() {
        RoomController roomController = new RoomController(window, name);
        RoomView roomView = new RoomView(roomController);
        window.setTitle("Room Creation");
        window.setContentPane(roomView);
        window.setSize(500, 500);
        window.revalidate();
        window.repaint();
    }
}
