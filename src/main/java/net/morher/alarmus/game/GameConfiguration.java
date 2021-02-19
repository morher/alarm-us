package net.morher.alarmus.game;

public interface GameConfiguration {

    long getStartDelayMs();

    long calculateGameTimeout(long startMillis);

    long calculateCrunchTime(long startMillis, long gameTimeoutMillis);

}