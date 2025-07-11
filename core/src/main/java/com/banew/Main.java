package com.banew;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.banew.containers.GameContainer;
import com.banew.containers.gui.GuiContainer;
import com.banew.external.GeneralSettings;
import com.banew.other.dto.PlayerInfo;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main implements ApplicationListener {
    SpriteBatch spriteBatch;
    FillViewport viewport;

    private GameContainer gameContainer;
    private GuiContainer guiContainer;
    private Cursor cursor;

    private void toggleFullscreen() {
        if (Gdx.graphics.isFullscreen()) {
            Gdx.graphics.setWindowedMode(640, 480); // Або інші бажані розміри
        } else {
            Graphics.DisplayMode displayMode = Gdx.graphics.getDisplayMode();
            Gdx.graphics.setFullscreenMode(displayMode);
        }
    }

    @Override
    public void create() {
        GeneralSettings generalSettings = GeneralSettings.importSettings();

        spriteBatch = new SpriteBatch();
        viewport = new FillViewport(8, 5);

        Pixmap pixmap = new Pixmap(Gdx.files.internal("textures/cursor.png"));
        cursor = Gdx.graphics.newCursor(pixmap, 9, 5);

        PlayerInfo playerInfo = new PlayerInfo();
        gameContainer = new GameContainer(viewport, generalSettings, playerInfo);
        guiContainer = new GuiContainer(generalSettings);
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
            toggleFullscreen();
        }

        ScreenUtils.clear(Color.BLACK);
        Gdx.graphics.setCursor(cursor);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        renderGame();
    }

    private void renderGame() {
        gameContainer.renderScene();
        spriteBatch.begin();
        gameContainer.renderSprites(spriteBatch);
        spriteBatch.end();
        gameContainer.renderLight();
        guiContainer.render(gameContainer.getContext());
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
        guiContainer.dispose();
    }
}
