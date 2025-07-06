package com.banew.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.banew.other.records.MovingEntityTexturesPerDirectionPack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MovingEntity extends SpriteEntity {
    protected float timer = 0f;

    private final List<TextureRegion> waitingRegions = new ArrayList<>();
    protected List<Animation<TextureRegion>> animationList = new ArrayList<>();
    protected List<Vector2> animationsScales = new ArrayList<>();

    protected int movingSide = 3;
    private boolean isMoving = false;

    public MovingEntity(
        Sprite sprite,
        Body body,
        Map<String, MovingEntityTexturesPerDirectionPack> animations,
        TextureAtlas textureAtlas
    ) {
        super(sprite, body, animations.get("down").scaleTexture());
        List<String> directions = List.of("up", "left", "down", "right");

        directions.forEach(direction -> {
            MovingEntityTexturesPerDirectionPack pack = animations.get(direction);

            waitingRegions.add(pack.waitingTexture());
            animationList.add(new Animation<>(
                1f / pack.animation().size(), pack.animation().toArray(new TextureRegion[0])
            ));
            animationsScales.add(pack.scaleTexture());
        });

        resetBody();
    }

    @Override
    public void update(float delta) {
        timer += delta;
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        update(Gdx.graphics.getDeltaTime());
        if (!isMoving && getBody().getLinearVelocity().len2() < .01f) {
            getSprite().setRegion(waitingRegions.get(movingSide));
        }
        else {
            getSprite().setRegion(animationList.get(movingSide).getKeyFrame(timer, true));
        }
        super.draw(spriteBatch);
        isMoving = false;
    }

    public void doNotMove() {
        isMoving = false;
        getBody().setLinearVelocity(0, 0);
    }

    public void move(float stepX, float stepY) {
        getBody().setLinearVelocity(
            stepX * 100 + getBody().getLinearVelocity().x,
            stepY * 100 + getBody().getLinearVelocity().y
        );

        isMoving = true;
        if (computeMovingSide(stepX, stepY) != movingSide) {
            movingSide = computeMovingSide(stepX, stepY);
            resetBody();
        }
    }

    public FixtureDef generateFixtureDef() {
        FixtureDef def = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(
            (getSprite().getWidth() / 2f) * animationsScales.get(movingSide).x,
            (getSprite().getHeight() / 2f) * animationsScales.get(movingSide).y
        );
        def.shape = shape;
        def.density = 1f;
        def.friction = 0.5f;

        return def;
    }

    private void resetBody() {
        if (getBody().getFixtureList().size > 0) {
            Fixture oldFixture = getBody().getFixtureList().first();
            getBody().destroyFixture(oldFixture);
        }
        getBody().createFixture(generateFixtureDef());

        setCurrentScales(new Vector2( // для дебагу (відображення колізій)
            animationsScales.get(movingSide).x, animationsScales.get(movingSide).y
        ));
    }

    private int computeMovingSide(float stepX, float stepY) {
        // Якщо взагалі немає руху — залиш поточну сторону
        if (stepX == 0 && stepY == 0) return movingSide;

        // Порівнюємо абсолютні значення руху по x та y
        if (Math.abs(stepX) > Math.abs(stepY)) {
            return stepX > 0 ? 3 : 1; // 3 → вправо, 1 → вліво
        } else {
            return stepY > 0 ? 0 : 2; // 0 → вгору, 2 → вниз
        }
    }
}
