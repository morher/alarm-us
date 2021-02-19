package net.morher.alarmus.handler;

import net.morher.alarmus.api.ServerMessage;

public class InitiateGameHook implements ClientMessageHandler {

    private static final String DEFAULT_GAME_NAME = "default";
    private static final String WEBHOOK_PREFIX = "initiateGame";
    private static final int PARAMETERIZED_PREFIX_LENTH = WEBHOOK_PREFIX.length() + 1;

    @Override
    public void handleClientEvent(ServerMessage event, CoordinatorContext ctx) {
        if (event.getHook() != null && event.getHook().startsWith(WEBHOOK_PREFIX)) {
            String gameName = event.getHook().length() > PARAMETERIZED_PREFIX_LENTH
                    ? event.getHook().substring(PARAMETERIZED_PREFIX_LENTH)
                    : DEFAULT_GAME_NAME;

            ctx.initiateGame(gameName);
        }
    }

}
