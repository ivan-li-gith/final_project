package org.example;

public class CreateRoomNanny {
    private final Main main;
    private final String myName;
    private final Repository repo = Repository.getInstance();

    public CreateRoomNanny(Main main, String myName) {
        this.main = main;
        this.myName = myName;
    }

    public void createRoom(String roomName, String selectedMode) {
        repo.addRoomName(roomName);
        repo.addRoomMode(selectedMode);
        switchGUI();
    }

    private void switchGUI() {
        main.setTitle("Planning Poker - Dashboard");
        DashboardPanel dashboardPanel = new DashboardPanel(main, myName);
        main.setContentPane(dashboardPanel);
        main.setSize(1000, 700);
        main.setLocationRelativeTo(null);
        main.revalidate();
        main.repaint();
    }
}
