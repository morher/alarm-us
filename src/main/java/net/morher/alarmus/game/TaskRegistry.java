package net.morher.alarmus.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.morher.alarmus.state.Player;
import net.morher.alarmus.state.Task;
import net.morher.alarmus.utils.Completable;

public class TaskRegistry implements TasksState {
    private final Completable criticalTasks = new Completable();
    private final Completable allTasks = new Completable();
    private final List<Player> players = new ArrayList<>();

    public void disconnectPlayer(String playerId) {
        for (Player player : players) {
            if (player.getId().equals(playerId)) {
                player.setEnabled(false);
            }
        }
        updateTasks();
    }

    public void addPlayer(String id, String playerName, String color) {
        if (findPlayer(id).isPresent()) {
            throw new IllegalArgumentException("Player with id '" + id + "' already added");
        }
        Player player = new Player(id, playerName);
        player.setColor(color);
        players.add(player);
    }

    public void addCriticalTask(String playerId, Task task) {
        Player player = findPlayer(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player '" + playerId + "' not found"));

        player.getCriticalTasks().add(task);
    }

    public void addTask(String playerId, Task task) {
        Player player = findPlayer(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player '" + playerId + "' not found"));

        player.getTasks().add(task);
    }

    private Optional<Player> findPlayer(String playerId) {
        for (Player player : players) {
            if (playerId.equals(player.getId())) {
                return Optional.of(player);
            }
        }
        return Optional.empty();
    }

    @Override
    public Completable criticalTasks() {
        return criticalTasks;
    }

    @Override
    public Completable allTasks() {
        return allTasks;
    }

    @Override
    public List<Player> getPlayers() {
        return players;
    }

    @Override
    public void reportTaskCompleteByTaskId(String taskId) {
        for (Player player : players) {
            if (player.isEnabled()) {
                for (Task task : player.getCriticalTasks()) {
                    if (taskId.equals(task.getId())) {
                        task.setCompleted(true);
                    }
                }
                for (Task task : player.getTasks()) {
                    if (taskId.equals(task.getId())) {
                        task.setCompleted(true);
                    }
                }
            }
        }
        updateTasks();
    }

    @Override
    public void reportTaskCompleteByToken(String taskToken) {
        for (Player player : players) {
            if (player.isEnabled()) {
                for (Task task : player.getCriticalTasks()) {
                    if (taskToken.equals(task.getCompleteToken())) {
                        task.setCompleted(true);
                    }
                }
                for (Task task : player.getTasks()) {
                    if (taskToken.equals(task.getCompleteToken())) {
                        task.setCompleted(true);
                    }
                }
            }
        }
        updateTasks();
    }

    private void updateTasks() {
        if (!hasUncompletedCriticalTask()) {
            criticalTasks.complete();

            if (!hasUncompletedTask()) {
                allTasks.complete();
            }
        }
    }

    private boolean hasUncompletedCriticalTask() {
        for (Player player : players) {
            if (player.isEnabled()) {
                for (Task task : player.getCriticalTasks()) {
                    if (!task.isCompleted()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean hasUncompletedTask() {
        for (Player player : players) {
            if (player.isEnabled()) {
                for (Task task : player.getTasks()) {
                    if (!task.isCompleted()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
