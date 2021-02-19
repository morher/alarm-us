package net.morher.alarmus.handler;

public interface CoordinatorContext {
    boolean initiateGame(String gameName);

    void startGame();

    void reportTaskComplete(String taskToken);
}
