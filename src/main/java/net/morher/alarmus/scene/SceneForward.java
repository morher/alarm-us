package net.morher.alarmus.scene;

public class SceneForward {
    private final String sceneName;
    private final long forwardAfterMs;

    public SceneForward(String sceneName, long forwardAfterMs) {
        this.sceneName = sceneName;
        this.forwardAfterMs = forwardAfterMs;
    }

    public String getSceneName() {
        return sceneName;
    }

    public long getForwardAfterMs() {
        return forwardAfterMs;
    }
}
