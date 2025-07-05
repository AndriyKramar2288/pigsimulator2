package com.banew.external;

import com.badlogic.gdx.Gdx;
import com.banew.containers.GameLevel;
import com.banew.external.entities.InitialMainHeroEntity;
import com.banew.factories.EntityFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class GeneralSettings {
    private String main_atlas_src;
    private String collision_level_name;
    private InitialMainHeroEntity mainHero;
    private List<InitialGameLevel> gameLevels = new ArrayList<>();

    /**
     * Завантажує ігрові рівні
     * @param factory фабрика для створення сутностей в рівнях
     * @return сет з рівнями
     */
    public Set<GameLevel> getLevels(EntityFactory factory) {
        return gameLevels.stream()
            .map(initSet -> new GameLevel(initSet, factory))
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
