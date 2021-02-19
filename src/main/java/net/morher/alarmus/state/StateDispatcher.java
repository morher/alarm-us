package net.morher.alarmus.state;

import java.util.Collection;
import java.util.Map;

public interface StateDispatcher {
    void updateState(Map<String, String> attributes, Collection<String> actions);
}
