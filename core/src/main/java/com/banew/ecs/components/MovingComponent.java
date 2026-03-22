package com.banew.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class MovingComponent implements Component {
    public float timer = 0f;

    public List<TextureRegion> waitingRegions = new ArrayList<>();
    public List<Animation<TextureRegion>> animationList = new ArrayList<>();
    public List<Vector2> animationsScales = new ArrayList<>();

    public int movingSide = 3;
    public Vector2 selfMoving = new Vector2();
    public Vector2 movingStep = new Vector2();

    public boolean isMoving() {
        return selfMoving.len2() != 0;
    }
}
