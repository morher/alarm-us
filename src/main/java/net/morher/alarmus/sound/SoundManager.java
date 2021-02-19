package net.morher.alarmus.sound;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import net.morher.alarmus.api.ServerMessage;
import net.morher.alarmus.messages.AbstractMessager;

public class SoundManager extends AbstractMessager<ServerMessage> {
    private Map<String, Clip> clipMap = new HashMap<>();

    public SoundManager() {
        registerClip(null, null);

    }

    public SoundManager registerClip(String action, File soundFile) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile.getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clipMap.put(action, clip);

        } catch (Exception e) {
            throw new IllegalArgumentException("Could not load sound from file '" + soundFile + "'", e);
        }
        return this;
    }

    @Override
    public void onMessage(ServerMessage message) {
        if (message.hasSceneActions()) {
            for (String action : message.getSceneActions()) {
                Clip clip = clipMap.get(action);
                if (clip != null) {
                    clip.start();
                }
            }
        }
    }

    public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException, InterruptedException {
        String filePath = "./src/main/resources/public/sounds/amongus-ost.mp3";
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());

        Clip clip = AudioSystem.getClip();

        clip.open(audioInputStream);

        // clip.loop(Clip.LOOP_CONTINUOUSLY);

        clip.start();
        Thread.sleep(10000);
    }
}
