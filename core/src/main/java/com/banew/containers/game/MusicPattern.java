package com.banew.containers.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;
import com.banew.external.GeneralSettings;
import com.banew.external.InitialMusicPattern;

import java.util.HashSet;
import java.util.Set;

public class MusicPattern implements Disposable {
    private final Set<MusicWithVolume> musicSet = new HashSet<>();
    private final float delay;
    private final GeneralSettings generalSettings;
    private float timerDelay = 0;

    public MusicPattern(InitialMusicPattern src, String name, GeneralSettings generalSettings) {
        delay = src.getDelay();
        this.generalSettings = generalSettings;

        AssetManager assetManager = new AssetManager();
        src.getSongs().forEach(m -> {
            assetManager.load("music/" + name + "/" + m.getSrc(), Music.class);
        });
        assetManager.finishLoading();

        src.getSongs().forEach(m -> {
            Music music = assetManager.get("music/" + name + "/" + m.getSrc(), Music.class);
            music.setVolume(m.getVolume());

            musicSet.add(new MusicWithVolume(music, m.getVolume()));
        });
    }

    public void render() {
        for (MusicWithVolume music : musicSet) {
            if (music.music().isPlaying()) {
                return;
            }
        }
        timerDelay += Gdx.graphics.getDeltaTime();

        if (timerDelay > delay) {
            MusicWithVolume music = musicSet.stream().findAny().orElseThrow();

            music.music().play();
            music.music().setVolume(generalSettings.getGeneralVolume() * music.ownVolume());

            timerDelay = 0;
        }
    }

    public void stopPlay() {
        musicSet.forEach(m -> m.music().stop());
    }

    @Override
    public void dispose() {
        musicSet.forEach(m -> m.music().dispose());
    }

    private record MusicWithVolume(
        Music music, float ownVolume
    ) {}
}
