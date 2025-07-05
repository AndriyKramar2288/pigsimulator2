package com.banew.external.entities;

import com.banew.entities.Torch;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class InitialTorch extends InitialAnimatedEntity {
    public InitialTorch() {
        setTargetClass(Torch.class);
    }
}
