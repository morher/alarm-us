package net.morher.alarmus.scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene {
    private final String name;
    private final Map<String, SceneFrame> frames = new HashMap<>();
    private final List<SceneAction> actions = new ArrayList<SceneAction>();
    private String defaultFrame;
    private SceneForward forward;

    public Scene(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<SceneAction> getActions() {
        return actions;
    }

    public SceneForward getForward() {
        return forward;
    }

    public Scene addFrame(SceneFrame frame) {
        frames.put(frame.getId(), frame);
        if (defaultFrame == null) {
            defaultFrame = frame.getId();
        }
        return this;
    }

    public SceneFrame getFrame(String frameId) {
        return frames.get(frameId);
    }

    public String getDefaultFrame() {
        return defaultFrame;
    }
}
