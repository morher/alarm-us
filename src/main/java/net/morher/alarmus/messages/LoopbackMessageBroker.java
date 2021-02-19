package net.morher.alarmus.messages;

public class LoopbackMessageBroker<M> extends MessageBroker<M> {
    @Override
    public void sendEvent(M message) {
        forwardMessage(message);
    }

}
