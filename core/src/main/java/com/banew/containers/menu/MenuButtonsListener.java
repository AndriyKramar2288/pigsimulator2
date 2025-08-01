package com.banew.containers.menu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.banew.containers.SoundContainer;

public class MenuButtonsListener extends ClickListener {
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
            soundContainer.play("focus_button");
        }
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        super.clicked(event, x, y);
        soundContainer.play("click_button");
    }
}
