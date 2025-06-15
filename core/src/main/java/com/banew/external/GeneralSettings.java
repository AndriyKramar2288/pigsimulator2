package com.banew.external;

import com.badlogic.gdx.Gdx;
import com.banew.containers.GameLevel;
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
    //private String background_music;
    private String collision_level_name;
    private List<InitialGameLevel> gameLevels = new ArrayList<>();

    public Set<GameLevel> getLevels(EntityFactory factory) {
        return gameLevels.stream()
            .map(initSet -> new GameLevel(initSet, factory))
            .collect(Collectors.toSet());
    }

    public static GeneralSettings importSettings() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(Gdx.files.internal("settings.json").readString(), GeneralSettings.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
