package net.morher.alarmus.sound;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {
    private final File soundFile;

    public Sound(File soundFile) {
        this.soundFile = soundFile;
    }

    public void play() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();

        } catch (Exception e) {
            throw new IllegalArgumentException("Could not load sound from file '" + soundFile + "'", e);
        }
    }

    public void stop() {

    }
}
