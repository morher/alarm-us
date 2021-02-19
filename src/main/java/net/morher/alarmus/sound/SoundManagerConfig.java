package net.morher.alarmus.sound;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SoundManagerConfig {
    public String basePath;
    public Map<String, SoundDef> actions = new HashMap<>();
    public String backgroundMusic;

    public static class SoundDef {
        public String file;
    }
}
