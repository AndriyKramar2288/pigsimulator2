package com.banew.external;

import lombok.Data;

import java.util.List;

@Data
public class InitialMusicPattern {
    private float delay = 0;
    private List<InitialMusicSong> songs;
}
