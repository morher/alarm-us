package net.morher.alarmus.messages;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class MessageBroker<M> implements MessageDispatcher<M> {
    private final Set<MessageListener<? super M>> listeners = ConcurrentHashMap.newKeySet();

    public MessageBroker<M> addMessageListener(MessageListener<? super M> listener) {
        this.listeners.add(listener);
        return this;
    }

    public MessageBroker<M> removeMessageListener(MessageListener<? super M> listener) {
        this.listeners.add(listener);
        return this;
    }

    protected Collection<MessageListener<? super M>> getListeners() {
        return listeners;
    }

    protected void forwardMessage(M message) {
        for (MessageListener<? super M> listener : getListeners()) {
            listener.onMessage(message);
        }
    }
}
