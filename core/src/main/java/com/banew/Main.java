package com.banew;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.ScreenUtils;
import com.banew.containers.GlobalGameContext;
import com.banew.containers.SoundContainer;
import com.banew.containers.menu.MenuContainer;
import com.banew.external.GeneralSettings;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main implements ApplicationListener {

    private Cursor cursor;

    private GlobalGameContext globalGameContext;

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
        Pixmap pixmap = new Pixmap(Gdx.files.internal("textures/cursors/cursor.png"));
        cursor = Gdx.graphics.newCursor(pixmap, 9, 5);

        GeneralSettings generalSettings = GeneralSettings.importSettings();

        globalGameContext = new GlobalGameContext(generalSettings);
    }

    @Override
    public void resize(int width, int height) {
        if(width <= 0 || height <= 0) return;
        globalGameContext.resizeCurrent(width, height);
    }

    @Override
    public void render() {
        // Draw your application here.
        if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
            toggleFullscreen();
        }

        ScreenUtils.clear(Color.BLACK);
        Gdx.graphics.setCursor(cursor);
        globalGameContext.renderCurrent();
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
        globalGameContext.dispose();
    }
}
