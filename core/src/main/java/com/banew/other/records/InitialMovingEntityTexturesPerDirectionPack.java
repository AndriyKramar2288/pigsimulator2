package com.banew.other.records;

import com.banew.external.textures.AbstractInitialTexture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InitialMovingEntityTexturesPerDirectionPack {
    private AbstractInitialTexture waitingTexture;
    private List<AbstractInitialTexture> animation;
}
