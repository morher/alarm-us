package net.morher.alarmus.game;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

public class SimpleGameConfiguration implements GameConfiguration {
    private Duration startDelay = Duration.ofSeconds(5);
    private Duration crunchLength = Duration.ofMinutes(1);
    private LocalTime timeout = LocalTime.of(8, 0);
    private Duration gameLength = Duration.ofMinutes(5); // Use if timeout is not set, or time has passed (today).

    @Override
    public long getStartDelayMs() {
        return startDelay != null
                ? startDelay.toMillis()
                : 0;
    }

    public void setStartDelay(Duration startDelay) {
        this.startDelay = startDelay;
    }

    public long getCrunchLengthMs() {
        return crunchLength.toMillis();
    }

    public void setCrunchLength(Duration crunchLength) {
        this.crunchLength = crunchLength;
    }

    @Override
    public long calculateGameTimeout(long startMillis) {
        if (timeout != null) {
            LocalDateTime localTimeout = timeout.atDate(LocalDate.now());
            if (!localTimeout.isBefore(LocalDateTime.now())) {
                return localTimeout
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli();
            }
        }
        return startMillis + gameLength.toMillis();
    }

    @Override
    public long calculateCrunchTime(long startMillis, long gameTimeoutMillis) {
        return gameTimeoutMillis - crunchLength.toMillis();
    }
}
