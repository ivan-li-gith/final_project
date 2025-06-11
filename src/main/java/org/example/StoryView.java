package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StoryView extends JPanel{
    private final StoryController controller;
    private final DefaultListModel<String> activeStoryModel = new DefaultListModel<>();
    private final DefaultListModel<String> completedStoryModel = new DefaultListModel<>();

    public StoryView(StoryController controller) {
        this.controller = controller;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 150));

        JList<String> activeStoryList = new JList<>(activeStoryModel);
        JList<String> completedStoryList = new JList<>(completedStoryModel);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Active Stories", new JScrollPane(activeStoryList));
        tabs.addTab("Completed Stories", new JScrollPane(completedStoryList));

        add(tabs, BorderLayout.CENTER);

        activeStoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        activeStoryList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedStory = activeStoryList.getSelectedValue();
                if (selectedStory != null) {
                    controller.handleStorySelection(selectedStory);
                }
            }
        });
    }

    public void updateStoryLists(List<String> activeStories, List<String> completedStories) {
        activeStoryModel.clear();
        for (String story : activeStories) {
            activeStoryModel.addElement(story);
        }

        completedStoryModel.clear();
        for (String story : completedStories) {
            completedStoryModel.addElement(story);
        }
    }
}
