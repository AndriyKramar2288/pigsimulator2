package com.banew.containers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.banew.entities.MainHeroEntity;
import com.banew.external.GeneralSettings;
import com.banew.factories.EntityFactory;
import com.banew.other.dto.PlayerInfo;
import com.banew.other.records.GameContext;
import com.banew.utilites.GameLevelRef;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;

public class GameContainer implements Disposable {
    private boolean isMoving = false;
    private float staminaReloadTimer = 0f;
    private float hpReloadTimer = 0f;
    public static boolean isDebug = false;

    @Getter
    private final GameContext context;

    public static final float PLAYER_SPEED = .7f;
    public static final Texture WHITE_PIXEL;
    static {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA4444);
        pixmap.setColor(new Color(1, 1, 1, .5f));
        pixmap.fill();
        WHITE_PIXEL = new Texture(pixmap);
        pixmap.dispose();
    }

    public GameContainer(Viewport viewport, GeneralSettings generalSettings, PlayerInfo playerInfo) {

        String COLLISION_LAYER_NAME = generalSettings.getCollision_level_name();

        EntityFactory entityFactory = new EntityFactory(generalSettings);

        var levels = generalSettings.getLevels(entityFactory);

        var currentLevel = levels.stream()
            .filter(l -> l.getLevelName().equals("main"))
            .findFirst()
            .orElseThrow();

        var mainHeroEntity = (MainHeroEntity) currentLevel.getEntitySet().stream()
            .filter(e -> e instanceof MainHeroEntity)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Головного бандіта не найшли на поточному рівні!"));

        context = new GameContext(
            mainHeroEntity,
            viewport,
            new GameLevelRef(currentLevel),
            levels,
            playerInfo,
            new SoundContainer(generalSettings)
        );

        currentLevel.switchTo(mainHeroEntity, mainHeroEntity.getBody().getPosition(), context);
    }


    public void renderSprites(SpriteBatch spriteBatch) {
        isMoving = false;

        context.currentLevel().getWorld().step(Gdx.graphics.getDeltaTime(), 1, 1);

        movingRender();

        new ArrayList<>(context.currentLevel().getEntitySet()).forEach(e -> {
            e.draw(spriteBatch);
            e.render(context);

            if (isDebug) {
                e.getCollisionSprite(WHITE_PIXEL).draw(spriteBatch);
            }
        });

        if (isDebug) {
            context.currentLevel().getCollisions().forEach(r -> {
                spriteBatch.draw(WHITE_PIXEL, r.x, r.y, r.width, r.height);
            });
        }
    }

    public void renderScene() {
        if (context.currentLevel() != null) {
            context.currentLevel().renderMap(context.camera());
        }
    }

    public void renderLight() {
        context.levels().forEach(l -> l.getLightMode().step());
        context.currentLevel().getLightMode().render(context);
    }

    private void movingRender() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isDebug = !isDebug;
        }

        moveMainHeroRender();
        reloadStats();

        context.camera().position.lerp(new Vector3(context.mainHeroEntity().getCenterCoordinates(), 0f), .125f);
        context.camera().zoom = isMoving ? smoothZoom(1.05f) : smoothZoom(1f);

        context.camera().update();
    }

    private void reloadStats() {
        if (context.mainHeroEntity().isRunning()) {
            staminaReloadTimer = 0f;
        }

        hpReloadTimer += Gdx.graphics.getDeltaTime();
        staminaReloadTimer += Gdx.graphics.getDeltaTime();

        if (staminaReloadTimer > 3 && context.playerInfo().getPlayerStamina() < context.playerInfo().getMaxPlayerStamina()) {
            context.playerInfo().setPlayerStamina(context.playerInfo().getPlayerStamina() + .7f);
        }

        if (context.playerInfo().getPlayerHealth() < context.playerInfo().getMaxPlayerHp()) {
            if ((hpReloadTimer > 5)) {
                context.playerInfo().setPlayerHealth(context.playerInfo().getPlayerHealth() + .1f);
            }
        }
        else {
            hpReloadTimer = 0;
        }
    }


    private void moveMainHero(float x, float y) {
        context.mainHeroEntity().move(-x, -y);
    }

    private void moveMainHeroRender() {
        Map<Integer, Consumer<Float>> keysMovementAction = Map.of(
            Input.Keys.W, (speed) -> moveMainHero(0, -speed),
            Input.Keys.S, (speed) -> moveMainHero(0, speed),
            Input.Keys.A, (speed) -> moveMainHero(speed, 0),
            Input.Keys.D, (speed) -> moveMainHero(-speed, 0)
        );

        context.mainHeroEntity().doNotMove();
        keysMovementAction.forEach((key, value) -> {
            if (Gdx.input.isKeyPressed(key)) {
                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && context.playerInfo().getPlayerStamina() > 0) {
                    context.mainHeroEntity().setRunning(true);
                    value.accept(PLAYER_SPEED * 1.5f);
                    context.playerInfo().setPlayerStamina(context.playerInfo().getPlayerStamina() - .05f);
                }
                else {
                    context.mainHeroEntity().setRunning(false);
                    value.accept(PLAYER_SPEED);
                }

                isMoving = true;
            }
        });
    }

    private float smoothZoom(float targetZoom) {
        // Швидкість наближення (чим менше, тим плавніше)
        float zoomSpeed = 4.5f; // одиниці за секунду

        // Поточний зум → поступово тягнемо до цілі
        return context.camera().zoom + (targetZoom - context.camera().zoom) * zoomSpeed * Gdx.graphics.getDeltaTime();
    }

    @Override
    public void dispose() {
        context.levels().forEach(GameLevel::dispose);
        WHITE_PIXEL.dispose();
    }
}
