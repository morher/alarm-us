package net.morher.alarmus.messages;

public interface MessageForwarder<M> {
    void forwardMessage(M message);
}
