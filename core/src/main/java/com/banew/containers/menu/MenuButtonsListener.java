package com.banew.containers.menu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.banew.containers.SoundContainer;

public class MenuButtonsListener extends InputListener {
    private final SoundContainer soundContainer;
    private long lastTimePlayed = System.currentTimeMillis();

    public MenuButtonsListener(SoundContainer soundContainer) {
        this.soundContainer = soundContainer;
    }

    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        super.enter(event, x, y, pointer, fromActor);
        if (System.currentTimeMillis() - lastTimePlayed > 1000) {
            lastTimePlayed = System.currentTimeMillis();
            soundContainer.play("inv_drop");
        }
    }
}
