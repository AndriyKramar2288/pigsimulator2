package com.banew;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.banew.containers.EntityContainer;
import com.banew.external.GeneralSettings;
import com.banew.factories.EntityFactory;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main implements ApplicationListener {
    private String BACKGROUND_MUSIC_SRC = "sounds/Coolio - Gansta's Paradise.mp3";
    private final String ATLAS_SRC = "textures-generated/game.atlas";

    SpriteBatch spriteBatch;
    FillViewport viewport;

    private EntityFactory entityFactory;
    private EntityContainer entityContainer;
    private Music background_music;
    private GeneralSettings generalSettings;


    @Override
    public void create() {
        generalSettings = GeneralSettings.importSettings();
        //BACKGROUND_MUSIC_SRC = generalSettings.getBackground_music();

        spriteBatch = new SpriteBatch();

        viewport = new FillViewport(16, 9);
        // Prepare your application here.
        background_music = Gdx.audio.newMusic(Gdx.files.internal(BACKGROUND_MUSIC_SRC));

        entityFactory = new EntityFactory(ATLAS_SRC, generalSettings);
        entityContainer = new EntityContainer(entityFactory, spriteBatch, viewport.getCamera(), generalSettings);


        background_music.setLooping(true);
        background_music.setVolume(0.1f);
        //background_music.play();
    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;
        // Resize your application here. The parameters represent the new window size.
        viewport.update(width, height, false);
    }

    @Override
    public void render() {
        // Draw your application here.
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();

        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();
        entityContainer.render();
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
        // Destroy application's resources here.
        spriteBatch.dispose();
    }
}
