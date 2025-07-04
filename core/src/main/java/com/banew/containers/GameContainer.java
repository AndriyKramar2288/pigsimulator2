package com.banew.containers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.banew.entities.LevelsDoor;
import com.banew.entities.MainHeroEntity;
import com.banew.external.GeneralSettings;
import com.banew.factories.EntityFactory;
import com.banew.other.dto.PlayerInfo;
import lombok.Getter;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class GameContainer implements Disposable {
    private MainHeroEntity mainHeroEntity;
    private final OrthographicCamera camera;
    private final LightContainer lightContainer;
    private GameLevel currentLevel;
    private final Set<GameLevel> levels;
    private boolean isMoving = false;
    @Getter
    private final PlayerInfo playerInfo;
    private float staminaReloadTimer = 0f;

    public static boolean isDebug = false;

    public static final Texture WHITE_PIXEL;
    static {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA4444);
        pixmap.setColor(new Color(1, 1, 1, .5f));
        pixmap.fill();
        WHITE_PIXEL = new Texture(pixmap);
        pixmap.dispose();
    }

    public GameContainer(Camera camera, GeneralSettings generalSettings, PlayerInfo playerInfo) {
        this.camera = (OrthographicCamera) camera;


        this.playerInfo = playerInfo;
        String COLLISION_LAYER_NAME = generalSettings.getCollision_level_name();

        EntityFactory entityFactory = new EntityFactory(generalSettings);

        levels = generalSettings.getLevels();
        currentLevel = levels.stream().toList().get(0);
        entityFactory.setCurrentGameLevel(currentLevel);
        levels.forEach(level -> level.loadEntites(generalSettings, entityFactory));

        entityFactory.setCurrentGameLevel(currentLevel);
        mainHeroEntity = (MainHeroEntity) generalSettings.getMainHero().extractEntity(entityFactory);
        currentLevel.setMainHeroEntity(mainHeroEntity);

        this.lightContainer = new LightContainer((OrthographicCamera) camera, currentLevel.getWorld());
    }


    public void renderSprites(SpriteBatch spriteBatch) {
        isMoving = false;

        currentLevel.getWorld().step(Gdx.graphics.getDeltaTime(), 1, 1);

        movingRender();

        currentLevel.getEntitySet().forEach(e -> {
            e.draw(spriteBatch);
            e.render();

            if (e instanceof LevelsDoor) {
                if (mainHeroEntity.getCenterCoordinates().sub(e.getCenterCoordinates()).len2() < 2f) {
                    System.out.println("Ми поруч з порталом в " + ((LevelsDoor) e).getLavelTo());
                }


                if (mainHeroEntity.getCenterCoordinates().sub(e.getCenterCoordinates()).len2() < .5f && ((LevelsDoor) e).isOpen()) {
                    GameLevel targetLevel = levels.stream()
                        .filter(l -> l.getLevelName().equals(((LevelsDoor) e).getLavelTo()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Рівня такого нема, довбойоб"));

                    System.out.println(
                        "Рівень змінюється з " + currentLevel.getLevelName() + " на " + targetLevel.getLevelName()
                    );

                    targetLevel.setMainHeroEntity(mainHeroEntity);
                    currentLevel = targetLevel;
                    lightContainer.setWorld(targetLevel.getWorld());
                    ((LevelsDoor) e).setOpen(false);
                }
                if (mainHeroEntity.getCenterCoordinates().sub(e.getCenterCoordinates()).len2() > 3f) {
                    ((LevelsDoor) e).setOpen(true);
                }
            }

            if (isDebug) {
                e.getCollisionSprite(WHITE_PIXEL).draw(spriteBatch);
            }
        });

        if (isDebug) {
            currentLevel.getCollisions().forEach(r -> {
                spriteBatch.draw(WHITE_PIXEL, r.x, r.y, r.width, r.height);
            });
        }
    }

    public void renderScene() {
        if (currentLevel != null) {
            currentLevel.renderMap(camera);
        }
    }


    private void movingRender() {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            isDebug = !isDebug;
        }

        moveMainHeroRender();
        reloadStamina();

        camera.position.lerp(new Vector3(mainHeroEntity.getCenterCoordinates(), 0f), .125f);
        camera.zoom = isMoving ? smoothZoom(1.05f) : smoothZoom(1f);

        camera.update();
    }

    private void reloadStamina() {
        if (mainHeroEntity.isRunning()) {
            staminaReloadTimer = 0f;
        }
        staminaReloadTimer += Gdx.graphics.getDeltaTime();
        if (staminaReloadTimer > 3 && getPlayerInfo().getPlayerStamina() < getPlayerInfo().getMaxPlayerStamina()) {
            getPlayerInfo().setPlayerStamina(getPlayerInfo().getPlayerStamina() + .7f);
        }
    }


    private void moveMainHero(float x, float y) {
        mainHeroEntity.move(-x, -y);
    }

    private float computeStep() {
        float speed = 1f;
        return speed * Gdx.graphics.getDeltaTime();
    }

    private void moveMainHeroRender() {
        Map<Integer, Consumer<Float>> keysMovementAction = Map.of(
            Input.Keys.W, (speed) -> moveMainHero(0, -speed),
            Input.Keys.S, (speed) -> moveMainHero(0, speed),
            Input.Keys.A, (speed) -> moveMainHero(speed, 0),
            Input.Keys.D, (speed) -> moveMainHero(-speed, 0)
        );

        mainHeroEntity.doNotMove();
        keysMovementAction.forEach((key, value) -> {
            if (Gdx.input.isKeyPressed(key)) {
                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && getPlayerInfo().getPlayerStamina() > 0) {
                    mainHeroEntity.setRunning(true);
                    value.accept(computeStep() * 1.5f);
                    getPlayerInfo().setPlayerStamina(getPlayerInfo().getPlayerStamina() - .05f);
                }
                else {
                    mainHeroEntity.setRunning(false);
                    value.accept(computeStep());
                }

                isMoving = true;
            }
        });
    }

    private float smoothZoom(float targetZoom) {
        // Швидкість наближення (чим менше, тим плавніше)
        float zoomSpeed = 4.5f; // одиниці за секунду

        // Поточний зум → поступово тягнемо до цілі
        return camera.zoom + (targetZoom - camera.zoom) * zoomSpeed * Gdx.graphics.getDeltaTime();
    }

    @Override
    public void dispose() {
        currentLevel.dispose();
        lightContainer.dispose();
    }

    public void renderLight() {
        lightContainer.render();
    }
}
