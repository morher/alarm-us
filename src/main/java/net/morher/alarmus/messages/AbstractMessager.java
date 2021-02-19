package net.morher.alarmus.messages;

public class AbstractMessager<M> implements MessageListener<M> {
    private MessageBroker<M> broker;

    public final void connect(MessageBroker<M> broker) {
        replaceBroker(broker);
    }

    public final void disconnect() {
        replaceBroker(null);
    }

    private final synchronized void replaceBroker(MessageBroker<M> newBroker) {
        // TODO Auto-generated method stub
        if (this.broker != null) {
            this.broker.removeMessageListener(this);
        }
        this.broker = newBroker;
        if (this.broker != null) {
            this.broker.addMessageListener(this);
        }
    }

    protected void sendMessage(M message) {
        MessageBroker<M> broker = this.broker;
        if (broker != null) {
            broker.sendEvent(message);
        }
    }

    @Override
    public void onMessage(M message) {
        // Override to react to messages
    }

}
