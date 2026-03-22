package com.banew.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;

public class ZombieComponent implements Component {
    public List<Vector2> wayToPlayer = new ArrayList<>();
    public float resetTimer = 0f;
    public float attackTimer = 0f;
}
