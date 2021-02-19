package net.morher.alarmus.sound;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.morher.alarmus.api.ServerMessage;
import net.morher.alarmus.messages.AbstractMessager;
import net.morher.alarmus.sound.SoundManagerConfig.SoundDef;

public class SoundManager extends AbstractMessager<ServerMessage> {
    private Map<String, Sound> clipMap = new HashMap<>();

    public SoundManager() {
    }

    public SoundManager configure(SoundManagerConfig config) {
        String basePath = config.basePath != null
                ? config.basePath
                : "";

        if (config.actions != null) {
            for (Entry<String, SoundDef> action : config.actions.entrySet()) {
                registerClip(action.getKey(), new File(basePath + action.getValue().file));
            }
        }
        return this;
    }

    public SoundManager registerClip(String action, File soundFile) {
        try {
            clipMap.put(action, new Sound(soundFile.getAbsoluteFile()));
            return this;

        } catch (Exception e) {
            throw new IllegalArgumentException("Could not load sound from file '" + soundFile + "'", e);
        }
    }

    @Override
    public void onMessage(ServerMessage message) {
        if (message.hasSceneActions()) {
            for (String action : message.getSceneActions()) {
                Sound clip = clipMap.get(action);
                if (clip != null) {
                    clip.stop();
                    clip.play();
                }
            }
        }
    }
}
