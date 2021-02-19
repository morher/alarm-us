package net.morher.alarmus.messages;

public interface MessageListener<M> {
    void onMessage(M message);
}
