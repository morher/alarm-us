package net.morher.alarmus.state;

import java.util.ArrayList;
import java.util.Collection;

public class Player {
    private String id;
    private String displayName;
    private String color;
    private boolean enabled = true;
    private Collection<Task> criticalTasks = new ArrayList<>();
    private Collection<Task> tasks = new ArrayList<>();

    public Player() {
    }

    public Player(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Collection<Task> getCriticalTasks() {
        return criticalTasks;
    }

    public void setCriticalTasks(Collection<Task> criticalTasks) {
        this.criticalTasks = criticalTasks;
    }

    public Collection<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Collection<Task> tasks) {
        this.tasks = tasks;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }
}
