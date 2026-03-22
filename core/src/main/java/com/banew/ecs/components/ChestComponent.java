package com.banew.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.List;

public class ChestComponent implements Component {
    public List<TextureRegion> chestsRegions;

    public ChestComponent init(List<TextureRegion> chestsRegions) {
        this.chestsRegions = chestsRegions;
        return this;
    }
}
