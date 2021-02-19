package net.morher.alarmus.handler;

import net.morher.alarmus.api.ServerMessage;

public class StartGameHook implements ClientMessageHandler {

    private static final String START_GAME_WEBHOOK = "startGame";

    @Override
    public void handleClientEvent(ServerMessage event, CoordinatorContext ctx) {
        if (START_GAME_WEBHOOK.equals(event.getHook())) {
            ctx.startGame();
        }
    }

}
