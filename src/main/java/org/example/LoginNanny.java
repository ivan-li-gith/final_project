package org.example;

public class LoginNanny {
    private final Main main;
    private final Repository repo = Repository.getInstance();
    private String myName;

    public LoginNanny(Main main) {
        this.main = main;
    }

    public void enterRoom(String name) {
        this.myName = name;
        repo.addParticipant(name);
        switchGUI();
    }

    private void switchGUI() {
        main.setTitle("Room");
        CreateRoomNanny createRoomNanny = new CreateRoomNanny(main, myName);
        CreateRoomPanel createRoomPanel = new CreateRoomPanel(createRoomNanny);
        main.setContentPane(createRoomPanel);
        main.setSize(500, 500);
        main.revalidate();
        main.repaint();
    }
}
