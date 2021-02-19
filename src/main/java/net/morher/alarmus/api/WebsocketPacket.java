package net.morher.alarmus.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class WebsocketPacket {
    private ServerMessage message;

    public WebsocketPacket() {
    }

    public WebsocketPacket(ServerMessage message) {
        this.message = message;
    }

    public ServerMessage getMessage() {
        return message;
    }

    public void setMessage(ServerMessage message) {
        this.message = message;
    }
}
