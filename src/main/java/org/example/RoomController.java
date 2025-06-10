package org.example;

public class RoomController {
    private final UserSelection window;
    private String name;
    private final Repository repo = Repository.getInstance();

    public RoomController(UserSelection window, String name) {
        this.window = window;
        this.name = name;
    }

    // adds room name/mode to repo and switches it to the dashboard
    public void createRoom(String roomName, String selectedMode) {
        repo.addRoomName(roomName);
        repo.addRoomMode(selectedMode);
        switchGUI();
    }

    private void switchGUI() {
        TaigaImportController taigaImportController = new TaigaImportController(window, name);
        TaigaImportView taigaImportView = new TaigaImportView(taigaImportController);
        window.setContentPane(taigaImportView);
        window.setTitle("Planning Poker - Dashboard");
        window.setSize(1000, 700);
        window.setLocationRelativeTo(null);
        window.revalidate();
        window.repaint();
    }
}
