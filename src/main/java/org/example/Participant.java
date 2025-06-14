package org.example;

/**
 * Represents a single participant in the planning poker session.
 * This class holds the participant's name and their current vote for a story.
 *
 * @author Ivan Li
 * @version 1.0
 */

public class Participant {
    private final String name;
    private String vote;

    public Participant(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getVote() {
        return vote;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }

    public boolean hasVoted() {
        return vote != null;
    }
}
