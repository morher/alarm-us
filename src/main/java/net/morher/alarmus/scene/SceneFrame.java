package net.morher.alarmus.scene;

import java.util.ArrayList;
import java.util.Collection;

public class SceneFrame {
    private String id;
    private String nextFrame;
    private long nextFrameAfterMs;
    private Collection<SceneAction> actions = new ArrayList<>();

    public SceneFrame() {
        this("default");
    }

    public SceneFrame(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getNextFrame() {
        return nextFrame;
    }

    public void setNextFrame(String nextFrame) {
        this.nextFrame = nextFrame;
    }

    public long getNextFrameAfterMs() {
        return nextFrameAfterMs;
    }

    public void setNextFrameAfterMs(long nextFrameAfterMs) {
        this.nextFrameAfterMs = nextFrameAfterMs;
    }

    public SceneFrame nextFrame(String nextFrame, int nextFrameAfterMs) {
        this.nextFrame = nextFrame;
        this.nextFrameAfterMs = nextFrameAfterMs;
        return this;
    }

    public Collection<SceneAction> getActions() {
        return actions;
    }

    public SceneFrame changeAttribute(String attribute, String value) {
        return changeAttribute(attribute, value, 0);
    }

    public SceneFrame changeAttribute(String attribute, String value, long timeShiftMs) {
        getActions().add(new SceneAction(attribute, value, timeShiftMs));
        return this;
    }

    public SceneFrame withAction(String action) {
        return withAction(action, 0);
    }

    public SceneFrame withAction(String action, long timeShiftMs) {
        getActions().add(new SceneAction(action, timeShiftMs));
        return this;
    }

}
