package com.banew.containers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;
import com.banew.external.InitialMusicPattern;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

public class MusicPattern implements Disposable {
    private final Set<Music> musicSet = new HashSet<>();
    private final float delay;
    private float timerDelay = 0;

    public MusicPattern(InitialMusicPattern src, String name) {
        delay = src.getDelay();

        AssetManager assetManager = new AssetManager();
        src.getSongs().forEach(m -> {
            assetManager.load("music/" + name + "/" + m.getSrc(), Music.class);
        });
        assetManager.finishLoading();

        src.getSongs().forEach(m -> {
            Music music = assetManager.get("music/" + name + "/" + m.getSrc(), Music.class);
            music.setVolume(m.getVolume());

            musicSet.add(music);
        });
    }

    public void render() {
        for (Music music : musicSet) {
            if (music.isPlaying()) {
                return;
            }
        }
        timerDelay += Gdx.graphics.getDeltaTime();

        if (timerDelay > delay) {
            musicSet.stream().findAny().orElseThrow().play();
            timerDelay = 0;
        }
    }

    public void stopPlay() {
        musicSet.forEach(Music::stop);
    }

    @Override
    public void dispose() {
        musicSet.forEach(Music::dispose);
    }
}
