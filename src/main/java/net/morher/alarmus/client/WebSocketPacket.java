package net.morher.alarmus.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WebSocketPacket<M> {
    private M message;

    public WebSocketPacket() {
    }

    public WebSocketPacket(M message) {
        this.message = message;
    }

    public M getMessage() {
        return message;
    }

    public void setMessage(M message) {
        this.message = message;
    }
}
