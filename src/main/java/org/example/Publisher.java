package org.example;

import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class Publisher implements Runnable, PropertyChangeListener, MqttCallback {
    private static final Logger logger = LoggerFactory.getLogger(Publisher.class);
    private final static String BROKER = "tcp://broker.hivemq.com:1883";
    private final static String TOPIC = "cal-poly/csc/309/new2";
    private final static String CLIENT_ID = "jgs-publisher-planit";

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
            logger.info("Connected to BROKER and Subscribed to TOPIC");
        } catch (MqttException e) {
            logger.error("Publisher failed to connect or subscribe.");
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
        // Collect all participant names into a list
        List<String> names = new ArrayList<>();
        for (Participant p : repo.getParticipants()) {
            names.add(p.getName());
        }

        // Publish a single message with all names, joined by commas
        if (!names.isEmpty()) {
            publish("participants:" + String.join(",", names));
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
                // ** REPLACE OLD RETAIN LOGIC WITH THIS **
                // Retain messages that define the essential state of the room.
                if (content.startsWith("room:") || content.startsWith("stories:") || content.startsWith("selectedStory:")) {
                    message.setRetained(true);
                } else {
                    message.setRetained(false); // Do not retain transient messages
                }
                client.publish(TOPIC, message);
                logger.info("Published message");
            } catch (MqttException e) {
                logger.error("Failed to publish message");
            }
        }
    }


    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        String content = new String(mqttMessage.getPayload());

        if (content.startsWith("participant:")) {
            String name = content.substring("participant:".length()).trim();
            if (!name.isEmpty()) {
                // Check if this participant is actually new before doing anything
                boolean isNew = repo.getParticipants().stream().noneMatch(p -> p.getName().equalsIgnoreCase(name));

                // addParticipant will fire a propertyChange event which correctly
                // notifies all clients of the new participant.
                repo.addParticipant(name);

                // If the participant was new, send them the current room state.
                if (isNew) {
                    logger.info("New participant '{}' joined. Broadcasting current state.", name);
                    sendRoom();
                    sendStories();
                    sendSelectedStory(); // Also send the currently selected story if there is one
                    sendParticipants(); // <-- ADD THIS LINE to send the full list
                }
            }
        } else if (content.startsWith("cardNumber:")) { // Use else-if for clarity
            String[] parts = content.substring("cardNumber:".length()).trim().split(":");
            if (parts.length == 2) {
                String name = parts[0];
                String vote = parts[1];
                repo.updateVote(name, vote);
                repo.addCollectedVote(vote);
                logger.info("Received vote from subscriber");
            }
        } else if (content.startsWith("finishedVoting:")) {
            String name = content.substring("finishedVoting:".length());
            repo.markFinishedVoting(name);
            logger.info("Received finished voting signal from: {}", name);
        }
    }

    @Override
    public void connectionLost(Throwable throwable) {}

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {}
}
