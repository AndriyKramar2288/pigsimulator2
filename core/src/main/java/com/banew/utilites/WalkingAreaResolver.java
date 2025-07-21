package com.banew.utilites;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.banew.containers.game.GameLevel;

import java.util.HashMap;
import java.util.Map;

public class WalkingAreaResolver {
    private final String defaultWalkingSound;
    private final Map<Rectangle, String> walkingAreas = new HashMap<>();

    public WalkingAreaResolver(MapLayer layer, String defaultWalkingSound) {
        this.defaultWalkingSound = defaultWalkingSound;

        if (layer != null) {
            layer.getObjects().forEach(object -> {
                Rectangle rectangle = GameLevel.fromMapObject(object);
                String area = object.getProperties().get("Class", String.class);
                walkingAreas.put(rectangle, area);
            });
        }
    }

    public String getCurrentAreaSound(Vector2 position) {
        for (Map.Entry<Rectangle, String> entry : walkingAreas.entrySet()) {
            if (entry.getKey().contains(position.x, position.y)) {
                return entry.getValue();
            }
        }
        return defaultWalkingSound;
    }
}
