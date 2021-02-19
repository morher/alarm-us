package net.morher.alarmus.plugins.wiz;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class LampGroup {
    private String name;
    private Collection<InetAddress> bulbs = new ArrayList<>();
    private Map<String, String> styleParams = new HashMap<>();

    public LampGroup(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public LampGroup addBulb(String addr) {
        try {
            InetAddress address = InetAddress.getByName(addr);
            bulbs.add(address);
        } catch (Exception e) {
            throw new IllegalArgumentException("Bulb not found " + addr, e);
        }
        return this;
    }

    public Collection<InetAddress> getBulbs() {
        return bulbs;
    }

    public String getStyleParams(String style) {
        return styleParams.get(style);
    }

    public LampGroup putStyle(String style, WizBulbParam bulbParam) {
        styleParams.put(style, bulbParam.toString());
        return this;
    }
}
