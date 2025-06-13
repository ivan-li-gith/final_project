package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * The view component that displays the lists of active and completed stories.
 * This class uses a JTabbedPane to separate stories that are pending a vote
 * from those that have already been estimated. It allows the Moderator to select
 * a story from the active list to begin a voting round.
 *
 * @author Aadi Dhanda
 * @version 1.0
 */

public class StoryView extends JPanel{
    private final StoryController controller;
    private final DefaultListModel<String> activeStoryModel = new DefaultListModel<>();
    private final DefaultListModel<String> completedStoryModel = new DefaultListModel<>();
    private final JList<String> activeStoryList;

    public StoryView(StoryController controller, boolean isModerator) {
        this.controller = controller;
        this.activeStoryList = new JList<>(activeStoryModel);

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 150));

        JList<String> completedStoryList = new JList<>(completedStoryModel);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Active Stories", new JScrollPane(activeStoryList));
        tabs.addTab("Completed Stories", new JScrollPane(completedStoryList));
        add(tabs, BorderLayout.CENTER);

        activeStoryList.setEnabled(isModerator);
        activeStoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // only moderator can select stories to vote
        if (isModerator) {
            activeStoryList.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    String selectedStory = activeStoryList.getSelectedValue();
                    if (selectedStory != null) {
                        controller.handleStorySelection(selectedStory);
                    }
                }
            });
        }
    }

    // update the active and completed after every vote
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

    public void setSelection(String storyToSelect) {
        activeStoryList.setSelectedValue(storyToSelect, true);
    }
}
