package org.example;

import java.beans.PropertyChangeSupport;
import java.util.*;

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
        // Check if the participant already exists
        for (Participant p : participants) {
            if (p.getName().equalsIgnoreCase(name)) {
                return; // Don't add duplicates
            }
        }

        // If not found, add the participant and notify listeners
        participants.add(new Participant(name));
        firePropertyChange("participants", null, participants);
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void addRoomName(String name) {
        if (!name.equals(this.roomName)) {  // prevent re-firing
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

        finishedVoting.clear();        // ← ADD THIS LINE
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

    public Map<String, Integer> getVoteCounts() {
        Map<String, Integer> countMap = new HashMap<>();
        for (String vote : getAllVotes()) {
            countMap.put(vote, countMap.getOrDefault(vote, 0) + 1);
        }
        return countMap;
    }


    public int calculateVoteSum() {
        int sum = 0;
        int count = 0;
        Random rand = new Random();

        for (String vote : getAllVotes()) {
            try {
                if (vote.equals("½")) {
                    sum += 1;  // treat ½ as 1, or change this if you want to round down to 0
                    count++;
                } else if (vote.equals("?")) {
                    int randomValue = rand.nextInt(10) + 1;  // Random integer between 1 and 10
                    sum += randomValue;
                    count++;
                } else {
                    sum += (int) Double.parseDouble(vote);  // Converts "2.0" to 2
                    count++;
                }
            } catch (NumberFormatException ignored) {}
        }

        return count == 0 ? 0 : sum / count;  // integer division gives int average
    }
}
