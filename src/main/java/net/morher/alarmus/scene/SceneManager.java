package net.morher.alarmus.scene;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.morher.alarmus.api.ServerMessage;
import net.morher.alarmus.messages.AbstractMessager;
import net.morher.alarmus.messages.MessageBroker;
import net.morher.alarmus.scene.SceneManagerConfig.FrameDef;
import net.morher.alarmus.scene.SceneManagerConfig.SceneDef;
import net.morher.alarmus.state.GamePhase;
import net.morher.alarmus.utils.StateObserver;

/**
 * The scene manager
 * 
 * @author Morten Hermansen
 */
public class SceneManager extends AbstractMessager<ServerMessage> implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(SceneManager.class);
    private GamePhase phase;
    private Map<String, Scene> scenes = new HashMap<String, Scene>();
    private Map<GamePhase, String> phaseToSceneMap = new HashMap<>();
    private final StateObserver<String> newScene = new StateObserver<>();
    private boolean running;

    public SceneManager() {
    }

    public SceneManager configure(SceneManagerConfig config) {
        for (Entry<String, SceneDef> def : config.defs.entrySet()) {
            SceneDef sceneDef = def.getValue();
            String sceneName = def.getKey();
            if (sceneDef.phases != null) {
                for (GamePhase phase : sceneDef.phases) {
                    phaseToSceneMap.put(phase, sceneName);
                }
            }
            registerScene(createScene(sceneName, sceneDef));
        }
        return this;
    }

    private static Scene createScene(String name, SceneDef def) {
        Scene scene = new Scene(name);
        if (def.initialFrame != null) {
            scene.addFrame(createFrame("default", def.initialFrame));
        }
        if (def.frames != null) {
            for (Entry<String, FrameDef> frame : def.frames.entrySet()) {
                scene.addFrame(createFrame(frame.getKey(), frame.getValue()));
            }
        }
        return scene;
    }

    private static SceneFrame createFrame(String id, FrameDef def) {
        SceneFrame frame = new SceneFrame(id);
        if (def.attributes != null) {
            for (Entry<String, String> attribute : def.attributes.entrySet()) {
                frame.changeAttribute(attribute.getKey(), attribute.getValue());
            }
        }
        if (def.actions != null) {
            for (String action : def.actions) {
                frame.withAction(action);
            }
        }
        frame.setNextFrame(def.next);
        frame.setNextFrameAfterMs(def.duration);
        return frame;
    }

    public SceneManager connectAndStart(MessageBroker<ServerMessage> broker) {
        connect(broker);
        new Thread(this, "SceneManager")
                .start();

        return this;
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            try {
                runNextScene();

            } catch (InterruptedException e) {
                running = false;
            }
        }
    }

    private void runNextScene() throws InterruptedException {
        newScene.awaitNewState();
        String sceneName = newScene.getNewState();
        LOGGER.debug("Starting scene {}", sceneName);

        Scene scene = scenes.get(sceneName);
        if (scene != null) {
            String nextFrame = scene.getDefaultFrame();
            while (nextFrame != null && !newScene.isUpdated()) {
                SceneFrame frame = scene.getFrame(nextFrame);
                if (frame != null) {
                    runFrame(frame);
                    nextFrame = frame.getNextFrame();

                } else {
                    nextFrame = null;
                }
            }
        }
    }

    private void runFrame(SceneFrame frame) throws InterruptedException {
        LOGGER.debug("Start frame: " + frame.getId());
        long frameStart = System.currentTimeMillis();

        // TODO: Time-shift...
        ServerMessage message = new ServerMessage();
        for (SceneAction action : frame.getActions()) {
            action.performAction(message);
        }
        sendMessage(message);

        // TODO: Wait for net frame...
        newScene.awaitNewState(frame.getNextFrameAfterMs(), TimeUnit.MILLISECONDS);
    }

    public void registerScene(Scene scene) {
        scenes.put(scene.getName(), scene);
    }

    @Override
    public void onMessage(ServerMessage message) {
        if (message.getPhase() != null && !Objects.equals(message.getPhase(), phase)) {
            onNewPhase(message.getPhase());
        }
    }

    private void onNewPhase(GamePhase newPhase) {
        this.phase = newPhase;
        String newSceneName = phaseToSceneMap.get(phase);
        if (newSceneName != null) {
            startScene(newSceneName);
        }
    }

    private void startScene(String newSceneName) {
        LOGGER.info("Request scene: " + newSceneName);
        newScene.newState(newSceneName);
    }

}
