package com.banew;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.banew.containers.GameContainer;
import com.banew.containers.GuiContainer;
import com.banew.external.GeneralSettings;
import com.banew.other.dto.PlayerInfo;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main implements ApplicationListener {
    SpriteBatch spriteBatch;
    FillViewport viewport;

    private GameContainer gameContainer;
    private GuiContainer guiContainer;
    private GeneralSettings generalSettings;

    @Override
    public void create() {
        generalSettings = GeneralSettings.importSettings();

        spriteBatch = new SpriteBatch();
        viewport = new FillViewport(8, 5);

        PlayerInfo playerInfo = new PlayerInfo();
        gameContainer = new GameContainer(viewport.getCamera(), generalSettings, playerInfo);
        guiContainer = new GuiContainer(playerInfo);

    }

    @Override
    public void resize(int width, int height) {
        if(width <= 0 || height <= 0) return;
        viewport.update(width, height, false);
        guiContainer.resize(width, height);
    }

    @Override
    public void render() {
        // Draw your application here.
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();

        gameContainer.renderScene();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();
        gameContainer.renderSprites(spriteBatch);
        spriteBatch.end();
        gameContainer.renderLight();
        guiContainer.render(gameContainer);
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
        gameContainer.dispose();
    }
}
