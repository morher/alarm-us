package net.morher.alarmus.game;

import java.util.List;

import net.morher.alarmus.state.Player;
import net.morher.alarmus.utils.Completable;

public interface TasksState {

    Completable criticalTasks();

    Completable allTasks();

    List<Player> getPlayers();

    void reportTaskCompleteByTaskId(String taskId);

    void reportTaskCompleteByToken(String taskToken);
}