package net.morher.alarmus.api;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.javalin.http.Context;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import net.morher.alarmus.AlarmUsServer;
import net.morher.alarmus.messages.AbstractMessager;
import net.morher.alarmus.messages.MessageListener;
import net.morher.alarmus.state.GamePhase;

public class ApiHandler extends AbstractMessager<ServerMessage> implements WsConnectHandler, WsCloseHandler, WsMessageHandler, MessageListener<ServerMessage> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmUsServer.class);
    private final Set<WsContext> sessions = ConcurrentHashMap.newKeySet();
    private final ServerMessage agregatedMessage = new ServerMessage()
            .withPhase(GamePhase.IDLE);

    public void configWs(WsHandler wsHandler) {
        wsHandler.onConnect(this);
        wsHandler.onClose(this);
        wsHandler.onMessage(this);
    }

    @Override
    public void handleConnect(WsConnectContext ctx) throws Exception {
        sessions.add(ctx);

        // Send agregated status on connect.
        ctx.send(new WebsocketPacket(agregatedMessage));
        System.out.println("Connect! " + ctx.session.getRemoteAddress() + " - " + sessions.size());

    }

    @Override
    public void handleClose(WsCloseContext ctx) throws Exception {
        sessions.remove(ctx);
        System.out.println("Close! " + sessions.size());
    }

    @Override
    public void handleMessage(WsMessageContext ctx) throws Exception {
        WebsocketPacket packet = null;
        try {
            packet = ctx.message(WebsocketPacket.class);
            LOGGER.trace("Received websocket packet: {}", ctx.message());
            if (packet.getMessage() != null) {
                sendMessage(packet.getMessage());
            }

        } catch (Exception e) {
            LOGGER.error("Invalid websocket received: {}", ctx.message());
            return;
        }

    }

    public void handleWebhook(@NotNull Context ctx) {
        // String token = ctx.queryParam("token");
        // TODO: Auth...

        String hook = ctx.pathParam("hook");
        ServerMessage event = new ServerMessage();
        event.setHook(hook);

        sendMessage(event);
    }

    @Override
    public void onMessage(ServerMessage message) {
        WebsocketPacket packet = new WebsocketPacket();
        packet.setMessage(message);
        for (WsContext session : sessions) {
            if (session.session.isOpen()) {
                session.send(packet);
            }
        }
        updateAgregatedMessage(message);
    }

    private void updateAgregatedMessage(ServerMessage message) {
        if (message.getPlayers() != null) {
            agregatedMessage.withPlayers(message.getPlayers());
        }
        if (message.hasAttributes()) {
            agregatedMessage.getAttributes().putAll(message.getAttributes());
        }
        if (message.getPhase() != null) {
            agregatedMessage.withPhase(message.getPhase());
            if (GamePhase.IDLE.equals(message.getPhase())) {
                agregatedMessage.withPlayers(null);
                agregatedMessage.setAttributes(null);
            }
        }
    }

}
