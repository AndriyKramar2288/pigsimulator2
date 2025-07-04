package com.banew.containers;

import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.banew.entities.MainHeroEntity;

public class LightContainer implements Disposable {
    private final OrthographicCamera camera;
    private final RayHandler rayHandler;
    private final Light light;

    public LightContainer(OrthographicCamera camera, World world, MainHeroEntity mainHeroEntity) {
        this.camera = camera;
        rayHandler = new RayHandler(world);
        rayHandler.setAmbientLight(1f);

        rayHandler.setAmbientLight(0.3f);


        light = new PointLight(
            rayHandler, 4096,
            new Color(1f, 0.5f, 0f, .3f), 5f, 0f, 0f
        );
        light.attachToBody(mainHeroEntity.getBody());
    }

    public void setWorld(World world, MainHeroEntity mainHeroEntity) {
        rayHandler.setWorld(world);
        light.attachToBody(mainHeroEntity.getBody());
    }

    public void render() {
        rayHandler.setCombinedMatrix(camera); // синхронізує з камерою
        rayHandler.updateAndRender();
    }

    @Override
    public void dispose() {
        rayHandler.dispose();
    }
}
