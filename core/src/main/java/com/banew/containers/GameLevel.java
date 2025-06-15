package com.banew.containers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.banew.entities.SpriteEntity;
import com.banew.external.InitialGameLevel;
import com.banew.factories.EntityFactory;
import lombok.Getter;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class GameLevel implements Disposable {
    @Getter
    private final Set<SpriteEntity> entitySet;
    @Getter
    private final TiledMap map;
    private final OrthoCachedTiledMapRenderer renderer;

    public static final float unitScaleMap = 32f;


    public void renderMap(OrthographicCamera camera) {
        renderer.setView(camera);
        renderer.render();
    }

    public GameLevel(InitialGameLevel initLevel, EntityFactory factory) {
        entitySet = new TreeSet<>(Comparator.comparingInt(SpriteEntity::getPriority)
            .thenComparingInt(Object::hashCode));
        entitySet.addAll(initLevel.getEntities(factory));

        TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
        params.textureMinFilter = Texture.TextureFilter.Nearest;
        params.textureMagFilter = Texture.TextureFilter.Nearest;
        params.generateMipMaps = false;
        params.convertObjectToTileSpace = false;

        map = new TmxMapLoader().load(initLevel.getMapName(), params);
        renderer = new OrthoCachedTiledMapRenderer(map, 1f / unitScaleMap);
        renderer.setBlending(true);
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }
}
