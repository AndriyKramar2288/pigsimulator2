package com.banew.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.banew.containers.game.GameLevel;
import com.banew.entities.alive.MainHeroEntity;
import com.banew.other.records.GameContext;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class LevelsDoor extends SpriteEntity {



    private final String levelFrom;
    private final String levelTo;
    private final String singleName;
    @Getter
    private final Set<MovingEntity> isClosedSet = new HashSet<>();


    public LevelsDoor(Sprite sprite, String levelFrom, String levelTo, String singleName) {
        super(sprite, null, new Vector2());

        this.levelFrom = levelFrom;
        this.levelTo = levelTo;
        this.singleName = singleName;
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        getSprite().draw(spriteBatch);
    }

    @Override
    public void render(GameContext context) {

    }

    @Override
    public void step(GameContext context, GameLevel entityLevel) {
        super.step(context, entityLevel);


    }
}
