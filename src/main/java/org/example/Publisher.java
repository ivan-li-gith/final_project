package org.example;

import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * The network communication client for the Moderator.
 * This class is responsible for publishing all state changes (like new stories,
 * new participants, or a new selected story) to the MQTT broker. It acts as the
 * single source of truth for the room's state. It also listens for messages
 * from participants (e.g., when they join or cast a vote) to keep the central
 * repository updated.
 *
 * @author Ivan Li
 * @version 1.0
 */

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

    // send over entire list of participants
    private void sendParticipants() {
        List<String> names = new ArrayList<>();
        for (Participant p : repo.getParticipants()) {
            names.add(p.getName());
        }

        if (!names.isEmpty()) {
            publish("participants:" + String.join(",", names));
        }
    }

    // send over all the stories
    private void sendStories() {
        publish("stories:" + String.join(",", repo.getStories()));
    }

    // send over room name
    private void sendRoom() {
        publish("room:" + repo.getRoomName());
    }

    // notify which story is selected to vote on
    private void sendSelectedStory() {
        String selected = repo.getSelectedStory();
        if (selected != null && !selected.isEmpty()) {
            publish("selectedStory:" + selected);
        }
    }

    // let subscriber know voting has started
    private void sendVotingStarted() {
        publish("votingStarted:true");
    }

    // let subscriber know that voting has ended for this person
    private void sendFinishedVoting() {
        ArrayList<String> finished = repo.getFinishedVoting();
        if (!finished.isEmpty()) {
            String latest = finished.get(finished.size() - 1);
            publish("finishedVoting:" + latest);
        }
    }

    // sends over value of card
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

                // retain stories and room to let people who join late what is going on
                if (content.startsWith("room:") || content.startsWith("stories:") || content.startsWith("selectedStory:")) {
                    message.setRetained(true);
                } else {
                    message.setRetained(false);
                }

                client.publish(TOPIC, message);
                logger.info("Publisher published message");
            } catch (MqttException e) {
                logger.error("Publisher failed to publish message");
            }
        }
    }


    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        String content = new String(mqttMessage.getPayload());

        if (content.startsWith("participant:")) {
            String name = content.substring("participant:".length()).trim();
            if (!name.isEmpty()) {
                // check if participant is new
                boolean isNew = repo.getParticipants().stream().noneMatch(p -> p.getName().equalsIgnoreCase(name));
                repo.addParticipant(name);

                // if the participant was new send them the current room state
                if (isNew) {
                    logger.info("New participant joined. Broadcasting current state");
                    sendRoom();
                    sendStories();
                    sendSelectedStory();
                    sendParticipants();
                }
            }
        } else if (content.startsWith("cardNumber:")) {
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

    @Override
    public void connectionLost(Throwable throwable) {}

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {}
}
