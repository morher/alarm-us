package net.morher.alarmus.plugins.wiz;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class WizLightConfig {
    public Map<String, StyleDef> commonStyles = new HashMap<>();
    public Map<String, BulbGroupDef> groups = new HashMap<>();

    public static class BulbGroupDef {
        public Collection<String> bulbs = new ArrayList<>();
        public Map<String, StyleDef> styles = new HashMap<>();
    }

    public static class StyleDef {
        public int[] rgb;
        public Integer scene;
        public int dimmed = 100;
    }
}
