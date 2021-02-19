package net.morher.alarmus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.javalin.Javalin;
import io.javalin.core.JavalinConfig;
import net.morher.alarmus.api.ApiHandler;
import net.morher.alarmus.api.ServerMessage;
import net.morher.alarmus.handler.InitiateGameHook;
import net.morher.alarmus.handler.StartGameHook;
import net.morher.alarmus.messages.LoopbackMessageBroker;
import net.morher.alarmus.messages.MessageBroker;
import net.morher.alarmus.plugins.wiz.WizLightPlugin;
import net.morher.alarmus.scene.SceneManager;

public class AlarmUsServer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmUsServer.class);
    private final GameCoordinator coordinator = new GameCoordinator()
            .addClientEventHandler(new InitiateGameHook())
            .addClientEventHandler(new StartGameHook());
    private final ApiHandler apiHandler;
    private final MessageBroker<ServerMessage> broker = new LoopbackMessageBroker<>();

    public static void main(String[] args) {

        AlarmUsServer server = new AlarmUsServer();
        // server.gameInitiater.initiate("default");
        server.run();

        // Config files:
        // - scenes.yaml - defines the scene and animations (auto-reloading?)
        // - game.yaml - defines the game setup (loaded when game is initiated)
        // - wiz-lights.yaml - defines wiz-light scene actions
        // - webhooks.yaml - defines webhooks scene actions
        // - resources.yaml - Where to find sounds++

    }

    public AlarmUsServer() {
        apiHandler = new ApiHandler();
        apiHandler.connect(broker);
        coordinator.connect(broker);
        SceneManager sceneManager = new SceneManager();
        sceneManager.connect(broker);
        sceneManager.start();

        new WizLightPlugin().connect(broker);
    }

    @Override
    public void run() {
        Javalin app = Javalin.create(this::configJavalin).start(9080);
        app.ws("/api/socket", apiHandler::configWs);
        app.get("/api/hook/:hook", apiHandler::handleWebhook);

        coordinator.run();
    }

    private void configJavalin(JavalinConfig config) {
        config.addStaticFiles("/public");
    }
}
