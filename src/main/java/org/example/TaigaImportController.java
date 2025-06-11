package org.example;

public class TaigaImportController {
    private final UserSelection window;
    private final String name;
    private final Repository repo = Repository.getInstance();

    public TaigaImportController(UserSelection window, String name) {
        this.window = window;
        this.name = name;
    }

    public void handleTaigaLogin(String username, String password, String projectSlug) {
        repo.loadStoriesFromTaiga(username, password, projectSlug);
        switchGUI();
    }

    private void switchGUI() {
        DashboardView dashboardView = new DashboardView(window, name);
        window.setContentPane(dashboardView);
        window.setTitle("Planning Poker - Dashboard");
        window.setSize(1000, 700);
        window.setLocationRelativeTo(null);
        window.revalidate();
        window.repaint();
    }
}
