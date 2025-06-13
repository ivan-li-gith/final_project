package org.example;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Controller for the story list view. This class manages the state of the
 * active and completed story lists. It handles the Moderator's selection of a
 * story to be voted on and updates the view in response to changes in the
 * central repository.
 *
 * @author Aadi Dhanda
 * @version 1.0
 */

public class StoryController implements PropertyChangeListener{
    private final StoryView storyView;
    private final Repository repo = Repository.getInstance();
    private final boolean isModerator;

    public StoryController(boolean isModerator) {
        this.isModerator = isModerator;
        this.storyView = new StoryView(this, isModerator);
        repo.addPropertyChangeListener(this);
        loadStories();
    }

    public StoryView getStoryView() {
        return storyView;
    }

    public void loadStories() {
        storyView.updateStoryLists(repo.getStories(), repo.getCompletedStories());
    }

    public void handleStorySelection(String story) {
        repo.setSelectedStory(story);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("stories".equals(evt.getPropertyName()) || "completedStories".equals(evt.getPropertyName())) {
            storyView.updateStoryLists(repo.getStories(), repo.getCompletedStories());
        }

        if ("selectedStory".equals(evt.getPropertyName())) {
            storyView.setSelection(repo.getSelectedStory());
        }
    }
}
