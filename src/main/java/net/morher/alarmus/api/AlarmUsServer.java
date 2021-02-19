package net.morher.alarmus.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.javalin.Javalin;
import io.javalin.core.JavalinConfig;
import net.morher.alarmus.game.GameCoordinator;
import net.morher.alarmus.handler.InitiateGameHook;
import net.morher.alarmus.handler.StartGameHook;
import net.morher.alarmus.messages.MessageBroker;

public class AlarmUsServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmUsServer.class);
    private final GameCoordinator coordinator = new GameCoordinator()
            .addClientEventHandler(new InitiateGameHook())
            .addClientEventHandler(new StartGameHook());
    private final ApiHandler apiHandler;
    private String staticFilesPath;

    public AlarmUsServer() {
        apiHandler = new ApiHandler();

    }

    public AlarmUsServer configure(ServerConfig config) {
        staticFilesPath = config.filesPath;

        Javalin app = Javalin.create(this::configJavalin).start(config.port);
        app.ws("/api/socket", apiHandler::configWs);
        app.get("/api/hook/:hook", apiHandler::handleWebhook);

        return this;
    }

    public AlarmUsServer connectAndStart(MessageBroker<ServerMessage> broker) {
        apiHandler.connect(broker);
        return this;
    }

    private void configJavalin(JavalinConfig config) {
        config.addStaticFiles(staticFilesPath);
    }
}
