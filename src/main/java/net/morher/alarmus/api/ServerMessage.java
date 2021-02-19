package net.morher.alarmus.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import net.morher.alarmus.state.GamePhase;
import net.morher.alarmus.state.Player;

@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class ServerMessage {
    private GamePhase phase;
    private List<Player> players;
    private Map<String, String> attributes;
    private Collection<String> sceneActions;
    private String completedTaskToken;
    private String hook;

    public GamePhase getPhase() {
        return phase;
    }

    public ServerMessage withPhase(GamePhase phase) {
        this.phase = phase;
        return this;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public ServerMessage withPlayers(List<Player> players) {
        this.players = players;
        return this;
    }

    public boolean hasAttributes() {
        return attributes != null
                && !attributes.isEmpty();
    }

    public Map<String, String> getAttributes() {
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        return attributes;
    }

    public ServerMessage setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
        return this;
    }

    public ServerMessage withAttribute(String attributeKey, String value) {
        getAttributes().put(attributeKey, value);
        return this;
    }

    public boolean hasSceneActions() {
        return sceneActions != null
                && !sceneActions.isEmpty();
    }

    public Collection<String> getSceneActions() {
        if (sceneActions == null) {
            sceneActions = new ArrayList<>();
        }
        return sceneActions;
    }

    public ServerMessage setSceneActions(Collection<String> sceneActions) {
        this.sceneActions = sceneActions;
        return this;
    }

    public ServerMessage withSceneActions(String sceneAction) {
        getSceneActions().add(sceneAction);
        return this;
    }

    public String getCompletedTaskToken() {
        return completedTaskToken;
    }

    public ServerMessage setCompletedTaskToken(String completedTaskToken) {
        this.completedTaskToken = completedTaskToken;
        return this;
    }

    public String getHook() {
        return hook;
    }

    public ServerMessage setHook(String webhook) {
        this.hook = webhook;
        return this;
    }

}
