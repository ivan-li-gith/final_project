package org.example;

public class StoriesNanny {
    private final Main main;
    private final String myName;
    private final Repository repo = Repository.getInstance();

    public StoriesNanny(Main main, String myName) {
        this.main = main;
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
        main.setTitle("Planning Poker - Dashboard");
        DashboardPanel dashboardPanel = new DashboardPanel(main, myName);
        main.setContentPane(dashboardPanel);
        main.setSize(1000, 700);
        main.setLocationRelativeTo(null);
        main.revalidate();
        main.repaint();
    }
}
