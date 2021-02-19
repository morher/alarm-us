package net.morher.alarmus.state;

public class Task {
    private static int lastId = 0;
    private final String id;
    private final String icon;
    private final String name;
    private final String type;
    private String completeToken;
    private boolean completed;

    public Task(String icon, String name, String type) {
        this.id = "t-" + Integer.toString(++lastId);
        this.icon = icon;
        this.name = name;
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public String getType() {
        return type;
    }

    public String getCompleteToken() {
        return completeToken;
    }

    public Task withCompleteToken(String completeToken) {
        this.completeToken = completeToken;
        return this;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
