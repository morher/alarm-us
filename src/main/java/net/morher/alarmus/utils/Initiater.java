package net.morher.alarmus.utils;

import java.util.concurrent.TimeUnit;

public class Initiater<T> {
    private T value;
    private Completable initiation = new Completable();

    public synchronized boolean initiate(T value) {
        if (!initiation.isComplete()) {
            this.value = value;
            initiation.complete();
            return true;
        }
        return false;
    }

    public T awaitInitiation() throws InterruptedException {
        initiation.await();
        return value;
    }

    public T awaitInitiation(long timeout, TimeUnit unit) throws InterruptedException {
        initiation.await(timeout, unit);
        return value;
    }

    public synchronized void reset() {
        value = null;
        initiation = new Completable();
    }
}
