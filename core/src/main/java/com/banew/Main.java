package com.banew;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.ScreenUtils;
import com.banew.containers.GameContainer;
import com.banew.external.GeneralSettings;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main implements ApplicationListener {
    private GameContainer gameContainer;
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

        Pixmap pixmap = new Pixmap(Gdx.files.internal("textures/cursor.png"));
        cursor = Gdx.graphics.newCursor(pixmap, 9, 5);

        gameContainer = new GameContainer(generalSettings);
    }

    @Override
    public void resize(int width, int height) {
        if(width <= 0 || height <= 0) return;
        gameContainer.resize(width, height);
    }

    @Override
    public void render() {
        // Draw your application here.
        if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
            toggleFullscreen();
        }

        ScreenUtils.clear(Color.BLACK);
        Gdx.graphics.setCursor(cursor);

        gameContainer.render();
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
