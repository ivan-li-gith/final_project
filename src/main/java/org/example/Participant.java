package org.example;

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
