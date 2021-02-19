package net.morher.alarmus.scene;

import net.morher.alarmus.api.ServerMessage;

public class SceneAction implements Comparable<SceneAction> {
    private String action;
    private String attribute;
    private String attributeValue;
    private long timeShiftMs;

    public SceneAction(String action, long timeShiftMs) {
        this.action = action;
        this.timeShiftMs = timeShiftMs;
    }

    public SceneAction(String attribute, String attributeValue, long timeShiftMs) {
        this.attribute = attribute;
        this.attributeValue = attributeValue;
        this.timeShiftMs = timeShiftMs;
    }

    public SceneAction timeShiftMs(long timeShiftMs) {
        this.timeShiftMs = timeShiftMs;
        return this;
    }

    public long getTimeShiftMs() {
        return timeShiftMs;
    }

    public void performAction(ServerMessage message) {
        if (action != null) {
            message.getSceneActions().add(action);
        }
        if (attribute != null) {
            message.getAttributes().put(attribute, attributeValue);
        }
    }

    @Override
    public int compareTo(SceneAction other) {
        return Long.compare(this.timeShiftMs, other.timeShiftMs);
    }

}
