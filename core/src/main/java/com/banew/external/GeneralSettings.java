package com.banew.external;

import com.badlogic.gdx.Gdx;
import com.banew.containers.game.GameLevel;
import com.banew.containers.game.MusicPattern;
import com.banew.ecs.EntityFactory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class GeneralSettings {
    private String main_atlas_src;
    private String collision_level_name;
    private String main_skin_src;
    private List<InitialGameLevel> gameLevels = new ArrayList<>();
    private Map<String, InitialMusicPattern> musicPatterns = new HashMap<>();
    private Map<String, InitialMusicSong> sounds = new HashMap<>();
    private List<InitialEffectAnimation> effectAnimations = new ArrayList<>();
    private List<String> menuPhotos = new ArrayList<>();
    private Map<String, InitialRace> races;
    private Map<String, String> skillDescriptions;

    private float generalVolume = 10.0f;

    @JsonIgnore
    private final Map<String, MusicPattern> musicPatternMap = new HashMap<>();

    /**
     * Завантажує ігрові рівні
     * @param factory фабрика для створення сутностей в рівнях
     * @return сет з рівнями
     */
    public Set<GameLevel> getLevels(EntityFactory factory) {

        musicPatterns.forEach((key, value) -> {
            musicPatternMap.put(key, new MusicPattern(value, key, this));
        });

        return gameLevels.stream()
            .map(initLevel -> new GameLevel(initLevel, factory, musicPatternMap, this))
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

    public void setGeneralVolume(float generalVolume) {
        this.generalVolume = generalVolume;
        musicPatternMap.forEach((k, v) -> v.stopPlay());
    }
}
