package com.banew.containers;

import com.badlogic.gdx.audio.Sound;
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
            currentWalkSoundId = sound.loop();
        }
        boolean isRunning = context.mainHeroEntity().isRunning();
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
