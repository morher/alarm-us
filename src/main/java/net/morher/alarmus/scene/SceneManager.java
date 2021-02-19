package net.morher.alarmus.scene;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.morher.alarmus.api.ServerMessage;
import net.morher.alarmus.messages.AbstractMessager;
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
    private Map<GamePhase, String> phaseToStringMap = new HashMap<>();
    private final StateObserver<String> newScene = new StateObserver<>();
    private boolean running;

    public SceneManager() {
        phaseToStringMap.put(GamePhase.STARTING, "start");

        phaseToStringMap.put(GamePhase.CRITICAL, "alarm");
        phaseToStringMap.put(GamePhase.CRUNCH, "alarm");

        phaseToStringMap.put(GamePhase.TASKS, "daytime");
        phaseToStringMap.put(GamePhase.DONE, "daytime");

        phaseToStringMap.put(GamePhase.WON, "winner");
        phaseToStringMap.put(GamePhase.LOST, "looser");

        registerScene(new Scene("start")
                .addFrame(new SceneFrame()
                        .withAction("soundStart")));

        registerScene(new Scene("alarm")
                .addFrame(new SceneFrame("start")
                        .nextFrame("high", 1000))

                .addFrame(new SceneFrame("high")
                        .changeAttribute("displayAlarm", "bright")
                        .changeAttribute("familyRoomWall", "redBright")
                        .changeAttribute("diningTable", "redBright")
                        .changeAttribute("hallways", "redBright")
                        .changeAttribute("otherLights", "redBright")
                        .withAction("soundAlarm")
                        .nextFrame("low", 1000))

                .addFrame(new SceneFrame("low")
                        .changeAttribute("displayAlarm", "dark")
                        .changeAttribute("familyRoomWall", "redDark")
                        .changeAttribute("diningTable", "redDark")
                        .changeAttribute("hallways", "redDark")
                        .changeAttribute("otherLights", "redDark")
                        .nextFrame("high", 1500)));

        registerScene(new Scene("winner")
                .addFrame(new SceneFrame()
                        .changeAttribute("displayAlarm", "off")
                        .changeAttribute("familyRoomWall", "greenBright")
                        .changeAttribute("diningTable", "greenBright")
                        .changeAttribute("hallways", "greenBright")
                        .changeAttribute("otherLights", "greenBright")
                        .withAction("soundWin")
                        .nextFrame("normal", 10000))
                .addFrame(new SceneFrame("normal")
                        .changeAttribute("displayAlarm", "off")
                        .changeAttribute("familyRoomWall", "warmwhite")
                        .changeAttribute("diningTable", "warmwhite")
                        .changeAttribute("hallways", "warmwhite")
                        .changeAttribute("otherLights", "off")));

        registerScene(new Scene("looser")
                .addFrame(new SceneFrame()
                        .changeAttribute("displayAlarm", "off")
                        .changeAttribute("familyRoomWall", "redBright")
                        .changeAttribute("diningTable", "redBright")
                        .changeAttribute("hallways", "redBright")
                        .changeAttribute("otherLights", "redBright")
                        .withAction("soundLoose")
                        .nextFrame("normal", 5000))
                .addFrame(new SceneFrame("normal")
                        .changeAttribute("displayAlarm", "off")
                        .changeAttribute("familyRoomWall", "warmwhite")
                        .changeAttribute("diningTable", "warmwhite")
                        .changeAttribute("hallways", "warmwhite")
                        .changeAttribute("otherLights", "off")));

        registerScene(new Scene("daytime")
                .addFrame(new SceneFrame()
                        .changeAttribute("displayAlarm", "off")
                        .changeAttribute("familyRoomWall", "warmwhite")
                        .changeAttribute("diningTable", "warmwhite")
                        .changeAttribute("hallways", "warmwhite")
                        .changeAttribute("otherLights", "off")));
    }

    public void start() {
        new Thread(this, "SceneManager")
                .start();
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
        String newSceneName = phaseToStringMap.get(phase);
        if (newSceneName != null) {
            startScene(newSceneName);
        }
    }

    private void startScene(String newSceneName) {
        LOGGER.info("Request scene: " + newSceneName);
        newScene.newState(newSceneName);
    }

}
