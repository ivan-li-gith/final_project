package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SouthPanel extends JPanel {
    private final Repository repo = Repository.getInstance();
    private final DefaultListModel<String> activeStoryModel = new DefaultListModel<>();
    private final JList<String> activeStoryList = new JList<>(activeStoryModel);
    private final DefaultListModel<String> completedStoryModel = new DefaultListModel<>();
    private final JList<String> completedStoryList = new JList<>(completedStoryModel);

    public SouthPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 150));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Active Stories", createScrollPane(activeStoryList));
        tabs.addTab("Completed Stories", createScrollPane(completedStoryList));

        add(tabs, BorderLayout.CENTER);
        loadStoriesFromRepository();
        setupStorySelection();
    }

    private JScrollPane createScrollPane(JComponent component) {
        return new JScrollPane(component);
    }

    private void loadStoriesFromRepository() {
        activeStoryModel.clear();
        for (String story : repo.getStories()) {
            activeStoryModel.addElement(story);
        }

        completedStoryModel.clear();
        for (String story : repo.getCompletedStories()) {
            completedStoryModel.addElement(story);
        }
    }

    private void setupStorySelection() {
        activeStoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        activeStoryList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = activeStoryList.getSelectedValue();
                if (selected != null) {
                    repo.setSelectedStory(selected);
                }
            }
        });
    }

    public void refreshStoryLists() {
        loadStoriesFromRepository();
    }
}
