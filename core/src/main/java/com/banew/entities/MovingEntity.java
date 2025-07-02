package com.banew.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.banew.external.textures.AbstractInitialTexture;
import com.banew.other.records.InitialMovingEntityTexturesPerDirectionPack;
import com.banew.other.records.MovingEntityTexturesPerDirectionPack;

import java.util.List;
import java.util.Map;

public class MovingEntity extends SpriteEntity {

    protected float timer = 0f;

    private final List<TextureRegion> waitingRegions;
    protected List<Animation<TextureRegion>> animationList;
    protected List<Vector2> animationsScales;

    private int movingSide = 0;
    private boolean isMoving = false;

    public MovingEntity(
        Sprite sprite,
        Body body,
        Map<String, InitialMovingEntityTexturesPerDirectionPack> animations,
        TextureAtlas textureAtlas
    ) {
        super(sprite, body);
        List<String> directions = List.of("up", "left", "down", "right");

        List<MovingEntityTexturesPerDirectionPack> texturePacks = directions.stream()
            .map(direction -> new MovingEntityTexturesPerDirectionPack(
                animations.get(direction).getWaitingTexture().extractTextureExtractor(),
                animations.get(direction).getAnimation().stream()
                    .map(AbstractInitialTexture::extractTextureExtractor)
                    .toList(),
                new Vector2(
                    animations.get(direction).getWaitingTexture().getWidthScale(),
                    animations.get(direction).getWaitingTexture().getHeightScale()
                )
            )).toList();


        this.waitingRegions = texturePacks.stream()
            .map(MovingEntityTexturesPerDirectionPack::waitingTexture)
            .map(t -> t.extractRegions(textureAtlas))
            .toList();
        this.animationList = texturePacks.stream()
            .map(range -> new Animation<TextureRegion>(
                0.25f,
                range.animation().stream()
                    .map(a -> a.extractRegions(textureAtlas))
                    .toList().toArray(new TextureRegion[0])
            ))
            .toList();
        this.animationsScales = texturePacks.stream()
            .map(MovingEntityTexturesPerDirectionPack::scaleTexture)
            .toList();
    }

    @Override
    public void update(float delta) {
        timer += delta;
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        update(Gdx.graphics.getDeltaTime());
        if (!isMoving) {
            getSprite().setRegion(waitingRegions.get(movingSide));
        }
        super.draw(spriteBatch);
        isMoving = false;
    }

    public void doNotMove() {
        getBody().setLinearVelocity(0, 0);
    }

    public void move(float stepX, float stepY, boolean isRunning) {
        animationList.get(movingSide).setFrameDuration(isRunning ? .15f : .25f);
        move(stepX, stepY);
    }

    public void move(float stepX, float stepY) {
        getBody().setLinearVelocity(stepX * 100, stepY * 100);

        isMoving = true;
        if (computeMovingSide(movingSide, stepX, stepY) != movingSide) {
            movingSide = computeMovingSide(movingSide, stepX, stepY);
            resetBody();
        }

        getSprite().setRegion(animationList.get(movingSide).getKeyFrame(timer, true));
        resetBody();
    }

    private void resetBody() {
        FixtureDef def = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(
            (getSprite().getWidth() / 2f) * animationsScales.get(movingSide).x,
            (getSprite().getWidth() / 2f) * animationsScales.get(movingSide).y
        );
        def.shape = shape;
        def.density = 1f;
        def.friction = 0.5f;

        Fixture oldFixture = getBody().getFixtureList().first();
        getBody().createFixture(def);
        getBody().destroyFixture(oldFixture);
    }

    private int computeMovingSide(int movingSide, float stepX, float stepY) {
        if (stepX > 0) {
            return 3;
        } else if (stepX < 0) {
            return 1;
        } else if (stepY < 0) {
            return 2;
        } else if (stepY > 0) {
            return 0;
        } else {
            return 0;
        }
    }

}
