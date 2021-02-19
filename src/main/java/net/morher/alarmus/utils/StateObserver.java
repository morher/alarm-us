package net.morher.alarmus.utils;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class StateObserver<T> {
    private T value;
    private Completable initiation = new Completable();

    public void newState(T value) {
        this.value = value;
        initiation.complete();
    }

    public boolean awaitNewState() throws InterruptedException {
        initiation.await();
        return value != null;
    }

    public boolean awaitNewState(long timeout, TimeUnit unit) throws InterruptedException {
        initiation.await(timeout, unit);
        return value != null;
    }

    public boolean awaitNewState(Duration duration) throws InterruptedException {
        return duration != null
                ? awaitNewState(duration.toMillis(), TimeUnit.MILLISECONDS)
                : awaitNewState();
    }

    public synchronized T getNewState() {
        T newState = value;
        value = null;
        initiation = new Completable();
        return newState;
    }

    public boolean isUpdated() {
        return initiation.isComplete();
    }

}
