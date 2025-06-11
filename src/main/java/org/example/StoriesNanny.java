package org.example;

public class StoriesNanny {
    private final UserSelection window;
    private final String myName;
    private final Repository repo = Repository.getInstance();

    public StoriesNanny(UserSelection main, String myName) {
        this.window = main;
        this.myName = myName;
    }

    // Save stories and allow adding more
    public void saveAndAddNew(String text) {
        String[] lines = text.split("\\n");
        for (String line : lines) {
            if (!line.isBlank()) repo.addStory(line.trim());
        }
        switchGUI();
    }

    // Save stories and go back to dashboard
    public void saveAndClose(String text) {
        String[] lines = text.split("\\n");
        for (String line : lines) {
            if (!line.isBlank()) repo.addStory(line.trim());
        }
        switchGUI();
    }

    // Just go back to dashboard
    public void cancel() {
        System.out.println("Canceled story entry.");
        switchGUI();
    }

    private void switchGUI() {
        window.setTitle("Planning Poker - Dashboard");
        DashboardView dashboardView = new DashboardView(window, myName);
        window.setContentPane(dashboardView);
        window.setSize(1000, 700);
        window.setLocationRelativeTo(null);
        window.revalidate();
        window.repaint();
    }
}
