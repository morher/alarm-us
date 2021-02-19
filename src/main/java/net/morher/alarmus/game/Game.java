package net.morher.alarmus.game;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import net.morher.alarmus.state.GamePhase;
import net.morher.alarmus.utils.Completable;

public class Game implements Runnable {
    private final GameConfiguration config;
    private final TasksState tasksState;
    private final GameListener listener;
    private final Completable startedLatch = new Completable();
    private long gameTimeoutMillis = 0;
    private long crunchTimeMillis = 0;
    private Thread gameThread;

    public Game(GameConfiguration config, TasksState tasksState, GameListener listener) {
        this.config = config;
        this.tasksState = tasksState;
        this.listener = listener;
    }

    public void reportTaskCompleteByTaskId(String taskId) {
        tasksState.reportTaskCompleteByTaskId(taskId);
        listener.onTasksUpdated(tasksState.getPlayers());
    }

    public void reportTaskCompleteByToken(String taskToken) {
        tasksState.reportTaskCompleteByToken(taskToken);
        listener.onTasksUpdated(tasksState.getPlayers());
    }

    public void start() {
        startedLatch.complete();
    }

    public void abandon() {
        Thread gameThread = this.gameThread;
        if (gameThread != null) {
            gameThread.interrupt();
        }
    }

    @Override
    public void run() {
        try {
            gameThread = Thread.currentThread();

            // PREGAME
            listener.onGameStateUpdate(GamePhase.PREGAME);
            listener.onTasksUpdated(tasksState.getPlayers());
            startedLatch.await();

            // STARTING
            long startMillis = System.currentTimeMillis();
            listener.onGameStateUpdate(GamePhase.STARTING);
            gameTimeoutMillis = config.calculateGameTimeout(startMillis);
            crunchTimeMillis = config.calculateCrunchTime(startMillis, gameTimeoutMillis);
            listener.onGameStarted(ZonedDateTime.ofInstant(Instant.ofEpochMilli(gameTimeoutMillis), ZoneId.systemDefault()));
            Thread.sleep(config.getStartDelayMs());

            // CRITICAL
            listener.onGameStateUpdate(GamePhase.CRITICAL);
            tasksState.criticalTasks().awaitUntil(gameTimeoutMillis);

            // TASKS
            if (System.currentTimeMillis() < crunchTimeMillis
                    && !tasksState.allTasks().isComplete()) {
                listener.onGameStateUpdate(GamePhase.TASKS);
                tasksState.allTasks().awaitUntil(crunchTimeMillis);
            }

            // CRUNCH
            if (System.currentTimeMillis() < gameTimeoutMillis
                    && !tasksState.allTasks().isComplete()) {
                listener.onGameStateUpdate(GamePhase.CRUNCH);
                tasksState.allTasks().awaitUntil(gameTimeoutMillis);
            }

            // GAME OVER
            // - Game done: Check if all tasks are done
            if (tasksState.allTasks().isComplete()) {
                listener.onGameStateUpdate(GamePhase.WON);

            } else {
                listener.onGameStateUpdate(GamePhase.LOST);

            }
            Thread.sleep(1800 * 1000);

        } catch (InterruptedException e) {
            // Game aborted

        } finally {
            gameThread = null;
            // Game ended
            listener.onGameStateUpdate(GamePhase.DONE);
            listener.onGameEnded();
        }
    }
}