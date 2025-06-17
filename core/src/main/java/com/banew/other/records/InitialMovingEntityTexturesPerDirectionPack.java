package com.banew.other.records;

import com.banew.external.textures.AbstractInitialTexture;
import lombok.Data;

import java.util.List;

@Data
public class InitialMovingEntityTexturesPerDirectionPack {
    private AbstractInitialTexture waitingTexture;
    private List<AbstractInitialTexture> animation;
}
