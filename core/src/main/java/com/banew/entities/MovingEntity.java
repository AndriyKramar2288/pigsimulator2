package com.banew.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import java.util.List;

public class MovingEntity extends SpriteEntity {

    protected float timer = 0f;

    private final List<TextureRegion> waitingRegions;
    protected List<Animation<TextureRegion>> animationList;
    protected List<Vector2> animationsScales;

    private int movingSide = 0;
    private boolean isMoving = false;

    public MovingEntity(
        Sprite sprite,
        Body body, List<TextureRegion> waitingRegions,
        List<Animation<TextureRegion>> animationList,
        List<Vector2> animationsScales
    ) {
        super(sprite, body);
        this.waitingRegions = waitingRegions;
        this.animationList = animationList;
        this.animationsScales = animationsScales;
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

    public void donotMove() {
        getBody().setLinearVelocity(0, 0);
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
        getBody().getFixtureList().forEach(e -> getBody().destroyFixture(e));

        FixtureDef def = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(
            (getSprite().getWidth() / 2f) * animationsScales.get(movingSide).x,
            (getSprite().getWidth() / 2f) * animationsScales.get(movingSide).y
        );
        def.shape = shape;
        def.density = 1f;
        def.friction = 0.5f;

        getBody().createFixture(def);
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
