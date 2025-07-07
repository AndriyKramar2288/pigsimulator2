package com.banew.external;

import com.badlogic.gdx.Gdx;
import com.banew.containers.GameLevel;
import com.banew.containers.MusicPattern;
import com.banew.factories.EntityFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class GeneralSettings {
    private String main_atlas_src;
    private String collision_level_name;
    private List<InitialGameLevel> gameLevels = new ArrayList<>();
    private Map<String, InitialMusicPattern> musicPatterns = new HashMap<>();
    private Map<String, InitialMusicSong> sounds = new HashMap<>();

    /**
     * Завантажує ігрові рівні
     * @param factory фабрика для створення сутностей в рівнях
     * @return сет з рівнями
     */
    public Set<GameLevel> getLevels(EntityFactory factory) {

        Map<String, MusicPattern> musicPatternMap = new HashMap<>();

        musicPatterns.forEach((key, value) -> {
            musicPatternMap.put(key, new MusicPattern(value, key));
        });

        return gameLevels.stream()
            .map(initLevel -> new GameLevel(initLevel, factory, musicPatternMap))
            .collect(Collectors.toSet());
    }

    /**
     * Створити екземпляр налаштувань на основі файлу "settings.json" в ./assets
     * @return екземпляр
     */
    public static GeneralSettings importSettings() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(Gdx.files.internal("settings.json").readString(), GeneralSettings.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
