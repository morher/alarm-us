package net.morher.alarmus.handler;

import net.morher.alarmus.api.ServerMessage;

public interface ClientMessageHandler {
    void handleClientEvent(ServerMessage event, CoordinatorContext ctx);
}
