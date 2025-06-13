package org.example;

import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * The network communication client for a Participant.
 * This class is responsible for subscribing to state updates from the Moderator
 * (via the Publisher) and for publishing this client's own actions, such as
 * joining the room, casting a vote, and finishing a vote. It listens for
 * messages on the MQTT topic to keep its local repository in sync with the
 * main room state.
 *
 * @author Aadi Dhanda
 * @version 1.0
 */

public class Subscriber implements Runnable, PropertyChangeListener, MqttCallback {
    private static final Logger logger = LoggerFactory.getLogger(Subscriber.class);
    private final static String BROKER = "tcp://broker.hivemq.com:1883";
    private final static String TOPIC = "cal-poly/csc/309/new2";
    // adding the UUID because for some reason it causes the messages to infinitely send
    private final static String CLIENT_ID = "jgs-subscriber-" + java.util.UUID.randomUUID().toString();

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
        if (evt.getPropertyName().equals("participants")) {
            sendParticipants();
        }

        if (evt.getPropertyName().equals("finishedVoting")) {
            sendFinishedVoting();
            sendCardNumber();
        }
    }

    private void sendParticipants() {
        List<Participant> participants = repo.getParticipants();
        if (!participants.isEmpty()) {
            Participant p = participants.get(participants.size() - 1);  // send the latest added participant
            publish("participant:" + p.getName());
        }
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
                logger.info("Subscriber published message");
            } catch (MqttException e) {
                logger.error("Subscriber failed to publish message");
            }
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        String content = new String(mqttMessage.getPayload());

        if (content.startsWith("participants:")) {
            String[] names = content.substring("participants:".length()).split(",");
            for (String name : names) {
                if (!name.isEmpty()) {
                    repo.addParticipant(name);
                }
            }
            logger.info("Subscriber received full participant list");
        } else if (content.startsWith("room:")) {
            String message = content.substring(5);
            repo.addRoomName(message);
            logger.info("Subscriber received room name");
        } else if (content.startsWith("stories:")) {
            String[] parts = content.substring(8).split(",");
            for (String part : parts) {
                repo.addStory(part);
            }
            logger.info("Subscriber received full stories list");
        } else if (content.startsWith("selectedStory:")) {
            String selected = content.substring("selectedStory:".length()).trim();
            if (!selected.isEmpty()) {
                repo.setSelectedStory(selected);
                logger.info("Subscriber received the selected story");
            }
        } else if (content.startsWith("votingStarted:")) {
            String flag = content.substring("votingStarted:".length()).trim();
            if (flag.equals("true")) {
                repo.setVotingStarted(true);
                logger.info("Subscriber knows voting is started");
            }
        } else if (content.startsWith("finishedVoting:")) {
            String name = content.substring("finishedVoting:".length());
            repo.markFinishedVoting(name);
            logger.info("Subscriber finished voting");
        } else if (content.startsWith("cardNumber:")) {
            String[] parts = content.substring("cardNumber:".length()).trim().split(":");
            if (parts.length == 2) {
                String name = parts[0];
                String vote = parts[1];
                repo.updateVote(name, vote);
                repo.addCollectedVote(vote);
                logger.info("Subscriber received vote from publisher");
            }
        }
    }

    @Override
    public void connectionLost(Throwable throwable) {}

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {}
}
