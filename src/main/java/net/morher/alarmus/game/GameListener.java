package net.morher.alarmus.game;

import java.time.ZonedDateTime;
import java.util.List;

import net.morher.alarmus.state.GamePhase;
import net.morher.alarmus.state.Player;

public interface GameListener {
    void onGameStateUpdate(GamePhase updatedState);

    void onGameStarted(ZonedDateTime startTime);

    void onGameEnded();

    void onTasksUpdated(List<Player> players);

}
