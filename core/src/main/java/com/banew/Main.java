package com.banew;

import box2dLight.RayHandler;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.banew.containers.EntityContainer;
import com.banew.external.GeneralSettings;
import com.banew.factories.EntityFactory;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main implements ApplicationListener {
    SpriteBatch spriteBatch;
    FillViewport viewport;

    private EntityContainer entityContainer;
    private GeneralSettings generalSettings;

    @Override
    public void create() {
        generalSettings = GeneralSettings.importSettings();

        spriteBatch = new SpriteBatch();
        viewport = new FillViewport(8, 5);

        entityContainer = new EntityContainer(viewport.getCamera(), generalSettings);


    }

    @Override
    public void resize(int width, int height) {
        if(width <= 0 || height <= 0) return;
        viewport.update(width, height, false);
    }

    @Override
    public void render() {
        // Draw your application here.
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();

        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();
        entityContainer.render(spriteBatch);
        spriteBatch.end();
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void dispose() {
        entityContainer.dispose();
    }
}
