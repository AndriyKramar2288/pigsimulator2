package com.banew.other.records;

import com.badlogic.gdx.math.Vector2;
import com.banew.utilites.TextureExtractor;

import java.util.List;
import java.util.Set;

public record MovingEntityTexturesPerDirectionPack(
    TextureExtractor waitingTexture,
    List<? extends TextureExtractor> animation,
    Vector2 scaleTexture
) { }
