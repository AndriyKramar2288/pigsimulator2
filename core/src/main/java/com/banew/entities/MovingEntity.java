package com.banew.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
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

    protected int movingSide = 0;
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
