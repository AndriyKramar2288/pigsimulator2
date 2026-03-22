package com.banew.containers.game;

import com.badlogic.gdx.audio.Sound;
import com.banew.ecs.components.MainHeroComponent;
import com.banew.ecs.components.MovingComponent;
import com.banew.other.records.GameContext;

public class WalkingSoundResolver {
    private long currentWalkSoundId = -1;
    private String previousWalkAreaName = "";

    public void play(GameContext context) {
        String walkSound = context.currentLevel().getCurrentWalkSound();
        Sound sound = context.soundContainer().getSound(walkSound);
        if (!walkSound.equals(previousWalkAreaName)) {
            if (currentWalkSoundId != -1) {
                sound.stop(currentWalkSoundId);
            }
            previousWalkAreaName = walkSound;
            currentWalkSoundId = sound.loop(context.soundContainer().getVolume(walkSound));
        }
        boolean isRunning = context.mainHeroEntity().getComponent(MainHeroComponent.class).isRunning;
        sound.setPitch(currentWalkSoundId, isRunning ? 2.0f : 1.6f);
    }

    public void stop(GameContext context) {
        if (!previousWalkAreaName.isEmpty() && currentWalkSoundId != -1) {
            context.soundContainer().getSound(previousWalkAreaName).stop(currentWalkSoundId);
            previousWalkAreaName = "";
            currentWalkSoundId = -1;
        }
    }
}
