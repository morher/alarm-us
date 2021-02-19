package net.morher.alarmus.scene;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.morher.alarmus.state.GamePhase;

public class SceneManagerConfig {
    public Map<String, SceneDef> defs = new HashMap<>();

    public static class SceneDef {
        public Collection<GamePhase> phases;
        public FrameDef initialFrame;
        public Map<String, FrameDef> frames;
    }

    public static class FrameDef {
        public Map<String, String> attributes;
        public Collection<String> actions;
        public String next;
        public long duration;
    }
}
