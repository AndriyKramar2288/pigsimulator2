package com.banew.external.entities;

import com.banew.other.records.InitialMovingEntityTexturesPerDirectionPack;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class InitialMovingEntity extends AbstractInitialEntity {
    protected Map<String, InitialMovingEntityTexturesPerDirectionPack> animations;
}
