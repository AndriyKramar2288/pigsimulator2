package com.banew.containers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.banew.external.GeneralSettings;

import java.util.HashMap;
import java.util.Map;

public class SoundContainer {
    private final Map<String, Sound> soundMap = new HashMap<>();
    private final Map<String, Float> volumeMap = new HashMap<>();

    public SoundContainer(GeneralSettings generalSettings) {
        AssetManager manager = new AssetManager();
        generalSettings.getSounds().forEach((key, value) -> {
            manager.load("sounds/" + value.getSrc(), Sound.class);
        });
        manager.finishLoading();

        generalSettings.getSounds().forEach((key, value) -> {
            Sound sound = manager.get("sounds/" + value.getSrc(), Sound.class);
            volumeMap.put(key, value.getVolume());
            soundMap.put(key, sound);
        });
    }

    /**
     * @param name назва, що була вказана в конфігурації
     */
    public void play(String name) {
        Sound sound = soundMap.get(name);
        if (sound != null) {
            sound.play(volumeMap.get(name));
        }
        else {
            System.out.println("Звук '" + name + "' слід змінити на латиницю, так воно не хоче!");
        }
    }

    public Sound getSound(String name) {
        Sound sound = soundMap.get(name);
        if (sound != null) {
            return sound;
        }
        else throw new RuntimeException("Звука '" + name + "' не знайдено!");
    }
}
