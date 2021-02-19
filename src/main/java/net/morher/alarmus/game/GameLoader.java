package net.morher.alarmus.game;

import net.morher.alarmus.state.Task;

public class GameLoader {

    public Game load(GameListener gameListener, String gameName) {
        SimpleGameConfiguration config = new SimpleGameConfiguration();

        TaskRegistry taskRegistry = new TaskRegistry();

        taskRegistry.addPlayer("sebastian", "Sebastian", "red");
        // taskRegistry.addTask("sebastian", new Task("", "Electrical: Fix wiring", "electrical"));
        taskRegistry.addTask("sebastian", new Task("🔑", "Sign in: Scan card",
                "signin").withCompleteToken("user-signin-sebastian"));
        taskRegistry.addTask("sebastian", new Task("📁", "Transfer files: From Sebastians Room to Bathroom downsairs", "transfer")
                .withCompleteToken("file-transfer-sebastian-to-bathroom1-by-sebastian"));
        // taskRegistry.addTask("sebastian", new Task("📁", "Transfer files: From Family Room to Bathroom", "transfer")
        // .withCompleteToken("file-transfer-familyroom-to-bathroom1-by-sebastian"));
        // taskRegistry.addTask("sebastian", new Task("📁", "Transfer files: From Bathroom to Entrance to sebastians room",
        // "transfer")
        // .withCompleteToken("file-transfer-bathroom1-to-entrance-by-sebastian"));
        taskRegistry.addTask("sebastian", new Task("📁", "Transfer files: From Kitchen to Entrance", "transfer")
                .withCompleteToken("file-transfer-kitchen-to-entrance-by-sebastian"));
        taskRegistry.addTask("sebastian", new Task("🍞", "Eat breakfast", "manual"));
        taskRegistry.addTask("sebastian", new Task("🍱", "Pack lunch box", "manual"));
        taskRegistry.addTask("sebastian", new Task("🎒", "Pack backpack", "manual"));
        taskRegistry.addTask("sebastian", new Task("🦷", "Brush teeth", "manual"));
        //
        taskRegistry.addPlayer("sofia", "Sofia", "blue");
        taskRegistry.addCriticalTask("sofia", new Task("🔑", "Sign in: Scan card", "signing")
                .withCompleteToken("user-signin-sofia"));
        taskRegistry.addCriticalTask("sofia", new Task("📁", "Transfer files: From Sofias room to Entrance", "transfer")
                .withCompleteToken("file-transfer-sofia-to-entrance-by-sofia"));
        taskRegistry.addTask("sofia", new Task("🍞", "Eat breakfast", "manual"));
        taskRegistry.addTask("sofia", new Task("🍱", "Pack lunch box", "manual"));
        taskRegistry.addTask("sofia", new Task("🎒", "Pack backpack", "manual"));
        taskRegistry.addTask("sofia", new Task("🦷", "Brush teeth", "manual"));

        taskRegistry.addPlayer("sunniva", "Sunniva", "pink");
        taskRegistry.addCriticalTask("sunniva", new Task("🔑", "Sign in: Scan card", "signing")
                .withCompleteToken("user-signin-sunniva"));
        taskRegistry.addCriticalTask("sunniva", new Task("📁", "Transfer files: From Sunnivas room to Kitchen", "transfer")
                .withCompleteToken("file-transfer-sunniva-to-kitchen-by-sunniva"));
        taskRegistry.addTask("sunniva", new Task("🍞", "Eat breakfast", "manual"));
        taskRegistry.addTask("sunniva", new Task("🍱", "Pack lunch box", "manual"));
        taskRegistry.addTask("sunniva", new Task("🎒", "Pack backpack", "manual"));
        taskRegistry.addTask("sunniva", new Task("🦷", "Brush teeth", "manual"));

        // taskRegistry.addTask("sunniva", new Task("📁", "Transfer files: From Kitchen to Entrance", "transfer")
        // .withCompleteToken("file-transfer-kitchen-to-entrance-by-sunniva"));

        return new Game(config, taskRegistry, gameListener);
    }
}
