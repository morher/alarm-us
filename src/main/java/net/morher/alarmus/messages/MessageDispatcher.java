package net.morher.alarmus.messages;

public interface MessageDispatcher<M> {
    void sendEvent(M event);
}
