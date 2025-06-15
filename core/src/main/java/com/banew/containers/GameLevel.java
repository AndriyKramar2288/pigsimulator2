package com.banew.containers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.BatchTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.banew.entities.SpriteEntity;
import com.banew.external.InitialGameLevel;
import com.banew.factories.EntityFactory;
import lombok.Getter;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class GameLevel {
    @Getter
    private final Set<SpriteEntity> entitySet;
    @Getter
    private final TiledMapRenderer renderer;

    public static final float unitScaleMap = 32f;

    public GameLevel(InitialGameLevel initLevel, EntityFactory factory) {
        entitySet = new TreeSet<>(Comparator.comparingInt(SpriteEntity::getPriority)
            .thenComparingInt(Object::hashCode));
        entitySet.addAll(initLevel.getEntities(factory));

        TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
        params.textureMinFilter = Texture.TextureFilter.Nearest;
        params.textureMagFilter = Texture.TextureFilter.Nearest;
        params.generateMipMaps = false;

        TiledMap map = new TmxMapLoader().load(initLevel.getMapName(), params);
        optimizeMapTextures(map);
        renderer = new OrthogonalTiledMapRenderer(map, 1f / unitScaleMap);
        //renderer = new OrthoCachedTiledMapRenderer(map, 1f / unitScaleMap, 5000);
    }

    private void optimizeMapTextures(TiledMap map) {
        // Проходимо по всіх tileset'ах та встановлюємо правильну фільтрацію
        for (TiledMapTileSet tileset : map.getTileSets()) {
            for (TiledMapTile tile : tileset) {
                TextureRegion region = tile.getTextureRegion();
                if (region != null && region.getTexture() != null) {
                    Texture texture = region.getTexture();
                    texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                    // Вимкнути wrap для уникнення артефактів на краях
                    texture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
                }
            }
        }
    }
}
