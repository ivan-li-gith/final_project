package org.example;

import java.beans.PropertyChangeSupport;
import java.util.*;
import org.json.JSONArray;

/**
 * A Singleton class that acts as the central data repository for the application.
 * It holds all shared state, such as participants, stories, and voting information.
 * This class extends PropertyChangeSupport to allow other parts of the application
 * (like controllers and views) to listen for and react to changes in the data.
 *
 * @author Ivan Li
 * @version 1.0
 */

public class Repository extends PropertyChangeSupport {
    public static Repository instance;
    private final ArrayList<Participant> participants = new ArrayList<>();
    private final ArrayList<String> storiesList = new ArrayList<>();
    private final ArrayList<String> completedStories = new ArrayList<>();
    private final ArrayList<String> finishedVoting = new ArrayList<>();
    private String roomName, mode;
    private String selectedStory = null;
    private boolean votingStarted = false;
    private final List<String> collectedVotes = new ArrayList<>();
    private final Map<String, Integer> playerScores = new HashMap<>();


    public Repository() {
        super(new Object());
    }

    public static Repository getInstance() {
        if (instance == null) {
            instance = new Repository();
        }
        return instance;
    }

    public void addParticipant(String name) {
        for (Participant p : participants) {
            if (p.getName().equalsIgnoreCase(name)) {
                return;
            }
        }
        participants.add(new Participant(name));
        firePropertyChange("participants", null, participants);
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void addRoomName(String name) {
        if (!name.equals(this.roomName)) {
            this.roomName = name;
            firePropertyChange("room", null, roomName);
        }
    }

    public void addRoomMode(String mode) {
        this.mode = mode;
        firePropertyChange("mode", null, mode);
    }

    public String getRoomName() {
        return roomName;
    }

    public void addStory(String story) {
        if (!storiesList.contains(story)) {
            storiesList.add(story);
            firePropertyChange("stories", null, storiesList);
        }
    }

    public List<String> getStories() {
        return storiesList;
    }

    public List<String> getCompletedStories() {
        return completedStories;
    }

    public void setSelectedStory(String story) {
        String old = this.selectedStory;
        this.selectedStory = story;

        for (Participant p : participants) {
            p.setVote(null);  // clear votes
        }

        finishedVoting.clear();
        firePropertyChange("selectedStory", old, story);
    }

    public String getSelectedStory() {
        return selectedStory;
    }

    public void updateVote(String name, String value) {
        for (Participant p : participants) {
            if (p.getName().equals(name)) {
                p.setVote(value);
                break;
            }
        }
    }

    public List<String> getAllVotes() {
        List<String> votes = new ArrayList<>();
        for (Participant p : participants) {
            if (p.hasVoted()) votes.add(p.getVote());
        }
        return votes;
    }

    public boolean isVotingStarted() {
        return votingStarted;
    }

    public void setVotingStarted(boolean started) {
        boolean old = this.votingStarted;
        this.votingStarted = started;
        firePropertyChange("votingStarted", old, started);
    }

    public void completeCurrentStory() {
        if (selectedStory != null) {
            storiesList.remove(selectedStory);
            completedStories.add(selectedStory);
            selectedStory = null;
            votingStarted = false;
            firePropertyChange("stories", null, storiesList);
        }
    }

    public void markFinishedVoting(String name) {
        if (!finishedVoting.contains(name)) {
            finishedVoting.add(name);
            firePropertyChange("finishedVoting", null, finishedVoting);
        }
    }

    public ArrayList<String> getFinishedVoting() {
        return finishedVoting;
    }

    public void addCollectedVote(String vote) {
        if (vote != null && !vote.isEmpty()) {
            collectedVotes.add(vote);
        }
    }

    // calculate for display after every vote
    public int calculateVoteSum() {
        int sum = 0;
        int count = 0;
        Random rand = new Random();

        for (String vote : getAllVotes()) {
            try {
                if (vote.equals("½")) {
                    sum += 1;  // treat as 1
                    count++;
                } else if (vote.equals("?")) {
                    int randomValue = rand.nextInt(10) + 1;  // random integer between 1 and 10
                    sum += randomValue;
                    count++;
                } else {
                    sum += (int) Double.parseDouble(vote);
                    count++;
                }
            } catch (NumberFormatException ignored) {}
        }

        return count == 0 ? 0 : sum / count;
    }

    // load stories from taiga
    public void loadStoriesFromTaiga(String username, String password, String projectSlug) {
        try{
            // accessing the taiga
            String token = TaigaStoryFetcher.loginAndGetToken(username, password);
            int projectId = TaigaStoryFetcher.getProjectId(token, projectSlug);
            JSONArray stories = TaigaStoryFetcher.fetchUserStories(token, projectId);

            // adds stories from the backlog to the array
            for (int i = 0; i < stories.length(); i++) {
                String story = stories.getJSONObject(i).getString("subject");
                this.addStory(story);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // add all scores for viewing how people vote
    public void accumulateScoreForStory() {
        for (Participant p : participants) {
            if (p.hasVoted()) {
                String vote = p.getVote();
                int score = parseVoteValue(vote);
                playerScores.put(p.getName(), playerScores.getOrDefault(p.getName(), 0) + score);
            }
        }
    }

    private int parseVoteValue(String vote) {
        try {
            if (vote.equals("½")) return 1;
            if (vote.equals("?")) return new Random().nextInt(10) + 1;
            return (int) Double.parseDouble(vote);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public Map<String, Integer> getPlayerScores() {
        return playerScores;
    }

    public Map<String, Integer> getVotesWithNames() {
        Map<String, Integer> result = new HashMap<>();
        for (Participant p : participants) {
            if (p.hasVoted()) {
                int score = parseVoteValue(p.getVote());
                result.put(p.getName() + ": " + p.getVote(), score);
            }
        }
        return result;
    }
}
