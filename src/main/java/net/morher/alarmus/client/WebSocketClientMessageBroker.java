package net.morher.alarmus.client;

import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.morher.alarmus.messages.MessageBroker;
import net.morher.alarmus.utils.Completable;

public class WebSocketClientMessageBroker<M> extends MessageBroker<M> implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketClientMessageBroker.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Client client;
    private final Class<M> messageType;
    private Completable disconnected = new Completable();

    public WebSocketClientMessageBroker(URI serverUri, Class<M> messageType) {
        this.client = new Client(serverUri);
        this.messageType = messageType;
    }

    @Override
    public void run() {
        try {
            LOGGER.debug("Connecting...");
            disconnected = new Completable();
            client.connectBlocking();

            while (true) {
                disconnected.await();

                if (!client.isClosed()) {
                    client.closeBlocking();
                }

                Thread.sleep(10000);

                disconnected = new Completable();
                LOGGER.debug("Reconnecting...");
                client.reconnectBlocking();
            }
        } catch (Exception e) {
            LOGGER.error("WebSocket client failed", e);
        }
    }

    @Override
    public void sendEvent(M message) {
        if (client.isOpen()) {
            try {
                client.send(objectMapper.writeValueAsString(message));
            } catch (JsonProcessingException e) {

                LOGGER.warn("Failed to serialize message", message);
            }
        }

    }

    private class Client extends WebSocketClient {

        public Client(URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            LOGGER.info("WebSocket connected");
        }

        @Override
        public void onMessage(String message) {
            try {
                forwardMessage(objectMapper.readValue(message, messageType));

            } catch (Exception e) {
                LOGGER.warn("Received invalid message: '{}'", message, e);
            }
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            disconnected.complete();
        }

        @Override
        public void onError(Exception ex) {
            LOGGER.warn("Unknown WebSocket Client error", ex);
        }

    }
}
