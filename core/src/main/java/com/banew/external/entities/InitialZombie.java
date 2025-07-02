package com.banew.external.entities;

import com.banew.entities.SpriteEntity;
import com.banew.external.textures.InitialDeepTexture;
import com.banew.factories.EntityFactory;
import com.banew.other.records.InitialMovingEntityTexturesPerDirectionPack;

import java.util.List;
import java.util.Map;

public class InitialZombie extends InitialMovingEntity {
    public InitialZombie() {
        initializeDefaultTexture();
    }

    @Override
    public SpriteEntity extractEntity(EntityFactory factory) {
        return factory.createZombie(this);
    }

    private void initializeDefaultTexture() {
        setSize_x(.5f);
        setSize_y(.9f);
        setAnimations(Map.of(
            "down", InitialMovingEntityTexturesPerDirectionPack.builder()
                .waitingTexture(
                    InitialDeepTexture.builder()
                        .region("Characters/zombie_n_skeleton2")
                        .sizeX(9).sizeY(4)
                        .cordX(2).cordY(1)
                        .build()
                )
                .animation(List.of(
                    InitialDeepTexture.builder()
                        .region("Characters/zombie_n_skeleton2")
                        .sizeX(9).sizeY(4)
                        .cordX(1).cordY(1)
                        .build(),
                    InitialDeepTexture.builder()
                        .region("Characters/zombie_n_skeleton2")
                        .sizeX(9).sizeY(4)
                        .cordX(2).cordY(1)
                        .build(),
                    InitialDeepTexture.builder()
                        .region("Characters/zombie_n_skeleton2")
                        .sizeX(9).sizeY(4)
                        .cordX(3).cordY(1)
                        .build()
                )).build(),
            "left", InitialMovingEntityTexturesPerDirectionPack.builder()
                .waitingTexture(
                    InitialDeepTexture.builder()
                        .region("Characters/zombie_n_skeleton2")
                        .sizeX(9).sizeY(4)
                        .cordX(2).cordY(2)
                        .build()
                )
                .animation(List.of(
                    InitialDeepTexture.builder()
                        .region("Characters/zombie_n_skeleton2")
                        .sizeX(9).sizeY(4)
                        .cordX(1).cordY(2)
                        .build(),
                    InitialDeepTexture.builder()
                        .region("Characters/zombie_n_skeleton2")
                        .sizeX(9).sizeY(4)
                        .cordX(2).cordY(2)
                        .build(),
                    InitialDeepTexture.builder()
                        .region("Characters/zombie_n_skeleton2")
                        .sizeX(9).sizeY(4)
                        .cordX(3).cordY(2)
                        .build()
                )).build(),
            "right", InitialMovingEntityTexturesPerDirectionPack.builder()
                .waitingTexture(
                    InitialDeepTexture.builder()
                        .region("Characters/zombie_n_skeleton2")
                        .sizeX(9).sizeY(4)
                        .cordX(2).cordY(3)
                        .build()
                )
                .animation(List.of(
                    InitialDeepTexture.builder()
                        .region("Characters/zombie_n_skeleton2")
                        .sizeX(9).sizeY(4)
                        .cordX(1).cordY(3)
                        .build(),
                    InitialDeepTexture.builder()
                        .region("Characters/zombie_n_skeleton2")
                        .sizeX(9).sizeY(4)
                        .cordX(2).cordY(3)
                        .build(),
                    InitialDeepTexture.builder()
                        .region("Characters/zombie_n_skeleton2")
                        .sizeX(9).sizeY(4)
                        .cordX(3).cordY(3)
                        .build()
                )).build(),
            "up", InitialMovingEntityTexturesPerDirectionPack.builder()
                .waitingTexture(
                    InitialDeepTexture.builder()
                        .region("Characters/zombie_n_skeleton2")
                        .sizeX(9).sizeY(4)
                        .cordX(2).cordY(4)
                        .build()
                )
                .animation(List.of(
                    InitialDeepTexture.builder()
                        .region("Characters/zombie_n_skeleton2")
                        .sizeX(9).sizeY(4)
                        .cordX(1).cordY(4)
                        .build(),
                    InitialDeepTexture.builder()
                        .region("Characters/zombie_n_skeleton2")
                        .sizeX(9).sizeY(4)
                        .cordX(2).cordY(4)
                        .build(),
                    InitialDeepTexture.builder()
                        .region("Characters/zombie_n_skeleton2")
                        .sizeX(9).sizeY(4)
                        .cordX(3).cordY(4)
                        .build()
                )).build()
        ));
        getAnimations().values().forEach(a -> {
            a.getWaitingTexture().setHeightScale(.5f);
            a.getWaitingTexture().setWidthScale(.5f);
        });
    }
}
