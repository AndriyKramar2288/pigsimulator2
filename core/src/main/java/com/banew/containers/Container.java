package com.banew.containers;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;

public interface Container extends Disposable {
    void render();
    void resize(int width, int height);
    Viewport viewport();
}
