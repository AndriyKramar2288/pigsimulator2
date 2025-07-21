package com.banew.containers;

import com.badlogic.gdx.utils.Disposable;

public interface Container extends Disposable {
    void render();
    void resize(int width, int height);
}
