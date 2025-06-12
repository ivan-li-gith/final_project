package org.example;

import org.eclipse.paho.client.mqttv3.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class Publisher implements Runnable, PropertyChangeListener, MqttCallback {
    private final static String BROKER = "tcp://broker.hivemq.com:1883";
    private final static String TOPIC = "cal-poly/csc/309/ivan";
    private final static String CLIENT_ID = "jgs-publisher";

    private MqttClient client;
    Repository repo = Repository.getInstance();

    @Override
    public void run() {
        try {
            client = new MqttClient(BROKER, CLIENT_ID);
            client.setCallback(this);
            client.connect();
            client.subscribe(TOPIC);
            repo.addPropertyChangeListener(this);
            System.out.println("Connected to BROKER: " + BROKER);
            System.out.println("Subscribed to TOPIC: " + TOPIC);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "participants":
                sendParticipants();
                break;
            case "stories":
                sendStories();
                break;
            case "room":
                sendRoom();
                break;
            case "selectedStory":
                sendSelectedStory();
                break;
            case "votingStarted":
                sendVotingStarted();
                break;
            case "finishedVoting":
                sendFinishedVoting();
                sendCardNumber();
                break;
        }
    }

    private void sendParticipants() {
        List<Participant> participants = repo.getParticipants();
        if (!participants.isEmpty()) {
            Participant p = participants.get(participants.size() - 1);  // send the latest added participant
            publish("participant:" + p.getName());
        }
    }

    private void sendStories() {
        publish("stories:" + String.join(",", repo.getStories()));
    }

    private void sendRoom() {
        publish("room:" + repo.getRoomName());
    }

    private void sendSelectedStory() {
        String selected = repo.getSelectedStory();
        if (selected != null && !selected.isEmpty()) {
            publish("selectedStory:" + selected);
        }
    }

    private void sendVotingStarted() {
        publish("votingStarted:true");
    }

    private void sendFinishedVoting() {
        ArrayList<String> finished = repo.getFinishedVoting();
        if (!finished.isEmpty()) {
            String latest = finished.get(finished.size() - 1);
            publish("finishedVoting:" + latest);
        }
    }

    private void sendCardNumber() {
        for (Participant p : repo.getParticipants()) {
            if (p.hasVoted()) {
                publish("cardNumber:" + p.getName() + ":" + p.getVote());
            }
        }
    }

    private void publish(String content) {
        if (client != null && client.isConnected()) {
            try {
                MqttMessage message = new MqttMessage(content.getBytes());
                client.publish(TOPIC, message);
                System.out.println("Published: " + content);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        String content = new String(mqttMessage.getPayload());

        if (content.startsWith("participant:")) {
            String name = content.substring("participant:".length()).trim();
            if (!name.isEmpty()) {
                repo.addParticipant(name);
                System.out.println("Received participant: " + name);
            }
        }

        if (content.startsWith("room:")) {
            String message = content.substring(5);
            repo.addRoomName(message);
            System.out.println("Got message and updated repo: " + repo.getRoomName());
        }

        if (content.startsWith("stories:")) {
            String[] parts = content.substring(8).split(",");
            for (String part : parts) {
                repo.addStory(part);
            }
            System.out.println("Got message and updated repo: " + String.join(",", repo.getStories()));
        }

        if (content.startsWith("finishedVoting:")) {
            String name = content.substring("finishedVoting:".length());
            repo.markFinishedVoting(name);
        }

        if (content.startsWith("cardNumber:")) {
            String[] parts = content.substring("cardNumber:".length()).trim().split(":");
            if (parts.length == 2) {
                String name = parts[0];
                String vote = parts[1];

                repo.updateVote(name, vote);
                repo.addCollectedVote(vote);
                System.out.println("Received vote from " + name + ": " + vote);
            }
        }
    }

    @Override
    public void connectionLost(Throwable throwable) {}

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {}
}
