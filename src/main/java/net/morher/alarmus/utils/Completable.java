package net.morher.alarmus.utils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Completable {
    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    public void complete() {
        if (!isComplete()) {
            countDownLatch.countDown();
        }
    }

    public boolean isComplete() {
        return countDownLatch.getCount() <= 0;
    }

    public void await() throws InterruptedException {
        countDownLatch.await();
    }

    public void await(long timeout, TimeUnit unit) throws InterruptedException {
        countDownLatch.await(timeout, unit);
    }

    public void awaitUntil(long timeMillis) throws InterruptedException {
        long wait = timeMillis - System.currentTimeMillis();
        countDownLatch.await(wait, TimeUnit.MILLISECONDS);
    }
}
