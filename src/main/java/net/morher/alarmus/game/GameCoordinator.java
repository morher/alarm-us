package net.morher.alarmus.game;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.morher.alarmus.api.ServerMessage;
import net.morher.alarmus.handler.ClientMessageHandler;
import net.morher.alarmus.handler.CoordinatorContext;
import net.morher.alarmus.messages.AbstractMessager;
import net.morher.alarmus.messages.MessageBroker;
import net.morher.alarmus.scene.SceneListener;
import net.morher.alarmus.state.GamePhase;
import net.morher.alarmus.state.Player;
import net.morher.alarmus.utils.Initiater;

public class GameCoordinator extends AbstractMessager<ServerMessage> implements SceneListener, GameListener, Runnable {
    private static final String COMPLETE_TASK_HOOK_PREFIX = "completeTask:";
    private static final Logger LOGGER = LoggerFactory.getLogger(GameCoordinator.class);
    private final Context ctx = new Context();
    private List<ClientMessageHandler> eventHandlers = new ArrayList<>();
    private final Initiater<String> gameInitiater = new Initiater<>();
    private boolean running;
    private Game currentGame;

    public GameCoordinator addClientEventHandler(ClientMessageHandler handler) {
        this.eventHandlers.add(handler);
        return this;
    }

    public GameCoordinator configure(GameConfig game) {
        return this;
    }

    public void connectAndStart(MessageBroker<ServerMessage> broker) {
        connect(broker);
        new Thread(this, "GameCoordinator")
                .start();
    }

    @Override
    public void onTasksUpdated(List<Player> players) {
        sendMessage(new ServerMessage().withPlayers(players));
    }

    @Override
    public void onGameStateUpdate(GamePhase updatedState) {
        sendMessage(new ServerMessage().withPhase(updatedState));
    }

    @Override
    public void onGameStarted(ZonedDateTime gameTimeout) {
        String timeoutStr = gameTimeout.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        sendMessage(new ServerMessage()
                .withAttribute("gameTimeout", timeoutStr));

    }

    @Override
    public void onGameEnded() {
        sendMessage(new ServerMessage().withPhase(GamePhase.IDLE));
    }

    @Override
    public void onMessage(ServerMessage message) {
        for (ClientMessageHandler handler : eventHandlers) {
            handler.handleClientEvent(message, ctx);
        }
        handleHooks(message);
    }

    private void handleHooks(ServerMessage message) {
        String hook = message.getHook();
        if (hook != null && !hook.isBlank()) {
            LOGGER.info("Received hook: " + hook);
            handleCurrentGameHooks(hook);
        }
    }

    private void handleCurrentGameHooks(String hook) {
        Game currentGame = this.currentGame;
        if (currentGame != null) {
            currentGame.reportTaskCompleteByToken(hook);
            handleCompleteTaskHook(hook, currentGame);
            handleAbandonGameHook(hook, currentGame);
        }
    }

    private void handleAbandonGameHook(String hook, Game currentGame) {
        if ("abandonGame".equals(hook)) {
            currentGame.abandon();
        }
    }

    private void handleCompleteTaskHook(String hook, Game currentGame) {
        if (hook.startsWith(COMPLETE_TASK_HOOK_PREFIX)) {
            String taskId = hook.substring(COMPLETE_TASK_HOOK_PREFIX.length());
            currentGame.reportTaskCompleteByTaskId(taskId);
        }
    }

    @Override
    public void run() {
        running = true;

        while (running) {
            try {
                String gameName = gameInitiater.awaitInitiation();

                System.out.println("Start game: " + gameName);
                currentGame = new GameLoader()
                        .load(this, gameName);
                currentGame.run();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

            } catch (Exception e) {
                LOGGER.error("Exception while running game", e);

            } finally {
                currentGame = null;
                gameInitiater.reset();
            }
        }
    }

    private class Context implements CoordinatorContext {

        @Override
        public boolean initiateGame(String gameName) {
            return gameInitiater.initiate(gameName);
        }

        @Override
        public void startGame() {
            Game currentGame = GameCoordinator.this.currentGame;
            if (currentGame != null) {
                currentGame.start();
            }
        }

        @Override
        public void reportTaskComplete(String taskToken) {
            Game currentGame = GameCoordinator.this.currentGame;
            if (currentGame != null) {
                currentGame.reportTaskCompleteByToken(taskToken);
            }
        }

    }
}