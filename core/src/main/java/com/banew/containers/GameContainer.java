package com.banew.containers;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.banew.entities.MainHeroEntity;
import com.banew.external.GeneralSettings;
import com.banew.factories.EntityFactory;
import com.banew.other.dto.PlayerInfo;
import lombok.Getter;

import java.util.Map;
import java.util.function.BiConsumer;

public class GameContainer implements Disposable {
    private final World world;
    private final MainHeroEntity mainHeroEntity;
    private final OrthographicCamera camera;
    private GameLevel currentLevel;
    private boolean isMoving = false;
    @Getter
    private final PlayerInfo playerInfo;

    private float staminaReloadTimer = 0f;

    public GameContainer(Camera camera, GeneralSettings generalSettings, PlayerInfo playerInfo) {
        this.camera = (OrthographicCamera) camera;
        this.playerInfo = playerInfo;
        String COLLISION_LAYER_NAME = generalSettings.getCollision_level_name();

        world = new World(
            new Vector2(0, 0),
            false
        );



        EntityFactory entityFactory = new EntityFactory(generalSettings, world);
        this.currentLevel = generalSettings.getLevels(entityFactory).stream().toList().get(0);

        mainHeroEntity = (MainHeroEntity) generalSettings.getMainHero().extractEntity(entityFactory);
        currentLevel.getEntitySet().add(mainHeroEntity);
        currentLevel.loadCollisions(world, COLLISION_LAYER_NAME);
        // light initialization
        lightPlayground();
    }

    RayHandler rayHandler;
    private void lightPlayground() {
         // якщо Box2D є, або new RayHandler(null)
        rayHandler = new RayHandler(world);
        rayHandler.setAmbientLight(1f); // повний день (1.0 = світло, 0.0 = ніч)

        // 2) Змінюєш яскравість для дня і ночі
        rayHandler.setAmbientLight(0.3f); // темна ніч

        PointLight torch = new PointLight(rayHandler, 256);
        torch.setPosition(3, 3);
        torch.setDistance(4);
        torch.setSoftnessLength(2f);
        torch.setColor(new Color(1f, 0.5f, 0f, 0.4f));
        torch.attachToBody(mainHeroEntity.getBody());

    }

    public void renderLight() {
        rayHandler.setCombinedMatrix(camera); // синхронізує з камерою
        rayHandler.updateAndRender();
    }

    public void renderSprites(SpriteBatch spriteBatch) {
        isMoving = false;

        world.step(Gdx.graphics.getDeltaTime(), 1, 1);

        movingRender();
        drawVisibleEntities(spriteBatch);

    }

    public void renderScene() {
        if (currentLevel != null) {
            currentLevel.renderMap(camera);
        }
    }


    private void movingRender() {
        moveMainHeroRender();
        reloadStamina();

        camera.position.lerp(new Vector3(mainHeroEntity.getCenterCoordinates(), 0f), .125f);
        camera.zoom = isMoving ? smoothZoom(1.05f) : smoothZoom(1f);

        camera.update();
    }

    private void reloadStamina() {
        if (isMoving) {
            staminaReloadTimer = 0f;
        }
        staminaReloadTimer += Gdx.graphics.getDeltaTime();
        if (staminaReloadTimer > 3 && !isMoving && getPlayerInfo().getPlayerStamina() < getPlayerInfo().getMaxPlayerStamina()) {
            getPlayerInfo().setPlayerStamina(getPlayerInfo().getPlayerStamina() + .7f);
        }
    }


    private void moveMainHero(float x, float y, boolean isRunning) {
        mainHeroEntity.move(-x, -y, isRunning);
    }

    private float computeStep() {
        float speed = 1f;
        return speed * Gdx.graphics.getDeltaTime();
    }

    private void moveMainHeroRender() {
        Map<Integer, BiConsumer<Float, Boolean>> keysMovementAction = Map.of(
            Input.Keys.W, (speed, isRunning) -> moveMainHero(0, -speed, isRunning),
            Input.Keys.S, (speed, isRunning) -> moveMainHero(0, speed, isRunning),
            Input.Keys.A, (speed, isRunning) -> moveMainHero(speed, 0, isRunning),
            Input.Keys.D, (speed, isRunning) -> moveMainHero(-speed, 0, isRunning)
        );

        mainHeroEntity.doNotMove();
        keysMovementAction.forEach((key, value) -> {
            if (Gdx.input.isKeyPressed(key)) {
                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && getPlayerInfo().getPlayerStamina() > 0) {
                    value.accept(computeStep() * 1.5f, true);
                    getPlayerInfo().setPlayerStamina(getPlayerInfo().getPlayerStamina() - .3f);
                }
                else {
                    value.accept(computeStep(), false);
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

    private void drawVisibleEntities(SpriteBatch spriteBatch) {
        currentLevel.getEntitySet().forEach(e -> e.draw(spriteBatch));
    }

    @Override
    public void dispose() {
        currentLevel.dispose();
        world.dispose();
    }
}
