package org.example;

/**
 * Controller for the Taiga import screen. This class handles the logic
 * for logging into the Taiga API, fetching user stories from a specified
 * project, and loading them into the central repository. After fetching the
 * stories, it transitions the UI to the main dashboard view.
 *
 * @author Ivan Li
 * @version 1.0
 */

public class TaigaImportController {
    private final UserSelection window;
    private final String name;
    private final Repository repo = Repository.getInstance();
    private final boolean isModerator;

    public TaigaImportController(UserSelection window, String name, boolean isModerator) {
        this.window = window;
        this.name = name;
        this.isModerator = isModerator;
    }

    // loads the taiga stories from login info
    public void handleTaigaLogin(String username, String password, String projectSlug) {
        repo.loadStoriesFromTaiga(username, password, projectSlug);
        switchGUI();
    }

    private void switchGUI() {
        DashboardView dashboardView = new DashboardView(window, name, isModerator);
        window.setContentPane(dashboardView);
        window.setTitle("Planning Poker - Dashboard");
        window.setSize(1000, 700);
        window.setLocationRelativeTo(null);
        window.revalidate();
        window.repaint();
    }
}
