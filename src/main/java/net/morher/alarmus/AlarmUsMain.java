package net.morher.alarmus;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import net.morher.alarmus.api.AlarmUsServer;
import net.morher.alarmus.api.ServerConfig;
import net.morher.alarmus.api.ServerMessage;
import net.morher.alarmus.client.WebSocketClientMessageBroker;
import net.morher.alarmus.game.GameConfig;
import net.morher.alarmus.game.GameCoordinator;
import net.morher.alarmus.handler.InitiateGameHook;
import net.morher.alarmus.handler.StartGameHook;
import net.morher.alarmus.messages.LoopbackMessageBroker;
import net.morher.alarmus.messages.MessageBroker;
import net.morher.alarmus.plugins.wiz.WizLightConfig;
import net.morher.alarmus.plugins.wiz.WizLightPlugin;
import net.morher.alarmus.scene.SceneManager;
import net.morher.alarmus.scene.SceneManagerConfig;
import net.morher.alarmus.sound.SoundManager;
import net.morher.alarmus.sound.SoundManagerConfig;

public class AlarmUsMain implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmUsMain.class);
    private final ConfigurationFile config = new ConfigurationFile();
    private ObjectMapper om = new ObjectMapper(new YAMLFactory());

    public static void main(String[] args) {
        AlarmUsMain amongUs = new AlarmUsMain();
        CmdLineParser parser = new CmdLineParser(amongUs);
        try {
            parser.parseArgument(args);

        } catch (CmdLineException e) {
            LOGGER.error("Failed to parse arguments: " + e.getMessage());
            System.exit(1);
        }

        amongUs.run();
    }

    @Option(name = "--config", aliases = "-c", metaVar = "FILE", usage = "Read one or more YAML configuration files given by a comma separated list.")
    public void loadConfigFile(String configFilesList) throws IOException {
        String[] configFiles = configFilesList.split(",");
        for (String configFile : configFiles) {
            LOGGER.info("Loading configuration yaml: {}", configFile);
            om.readerForUpdating(config).readValue(new File(configFile));
        }
    }

    @Option(name = "--connect-remote", aliases = "-r", metaVar = "URL", usage = "Give a websocket URL to the broker-server to be used instead of the internal broker.")
    public void setRemoteUrl(String wsUrl) {
        config.serverUrl = wsUrl;
    }

    @Override
    public void run() {
        MessageBroker<ServerMessage> broker = getBroker();

        connectGameCoordinator(broker);
        connectServer(broker);
        connectSceneManager(broker);
        connectSoundManager(broker);
        connectWizLightPlugin(broker);

    }

    private void connectGameCoordinator(MessageBroker<ServerMessage> broker) {
        if (config.game != null && config.game.enable) {
            new GameCoordinator()
                    .addClientEventHandler(new InitiateGameHook())
                    .addClientEventHandler(new StartGameHook())
                    .configure(config.game)
                    .connectAndStart(broker);

        }
    }

    private void connectServer(MessageBroker<ServerMessage> broker) {
        if (config.server != null) {
            new AlarmUsServer()
                    .configure(config.server)
                    .connectAndStart(broker);
        }
    }

    private void connectSceneManager(MessageBroker<ServerMessage> broker) {
        if (config.scenes != null) {
            new SceneManager()
                    .configure(config.scenes)
                    .connectAndStart(broker);
        }
    }

    private void connectSoundManager(MessageBroker<ServerMessage> broker) {
        if (config.sounds != null) {
            new SoundManager()
                    .configure(config.sounds)
                    .connect(broker);
        }
    }

    private void connectWizLightPlugin(MessageBroker<ServerMessage> broker) {
        if (config.wizlight != null) {
            new WizLightPlugin()
                    .configure(config.wizlight)
                    .connectAndStart(broker);
        }
    }

    private MessageBroker<ServerMessage> getBroker() {
        if (config.serverUrl != null) {
            return new WebSocketClientMessageBroker<>(uri(config.serverUrl), ServerMessage.class)
                    .start();
        }
        return new LoopbackMessageBroker<>();
    }

    private URI uri(String connectAddr) {
        try {
            return new URI(connectAddr);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid url '" + connectAddr + "'");
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ConfigurationFile {
        public String serverUrl;
        public GameConfig game;
        public ServerConfig server;
        public SoundManagerConfig sounds;
        public SceneManagerConfig scenes;
        public WizLightConfig wizlight;
    }
}
