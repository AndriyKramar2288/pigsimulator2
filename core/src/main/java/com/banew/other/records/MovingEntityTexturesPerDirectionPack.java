package com.banew.other.records;

import com.banew.utilites.TextureExtractor;

import java.util.Set;

public record MovingEntityTexturesPerDirectionPack(
    TextureExtractor waitingTexture,
    Set<TextureExtractor> animation
) { }
