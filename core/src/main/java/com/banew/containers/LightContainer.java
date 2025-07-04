package com.banew.containers;

import box2dLight.DirectionalLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

public class LightContainer implements Disposable {
    private final OrthographicCamera camera;
    private final RayHandler rayHandler;

    public LightContainer(OrthographicCamera camera, World world) {
        this.camera = camera;
        rayHandler = new RayHandler(world);
        rayHandler.setAmbientLight(1f);

        rayHandler.setAmbientLight(0.3f);

        DirectionalLight light = new DirectionalLight(rayHandler, 4096,
            new Color(1f, 0.5f, 0f, .3f), -90
        );
        light.setDistance(5);
    }

    public void setWorld(World world) {
        rayHandler.setWorld(world);
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
