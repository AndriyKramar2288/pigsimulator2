package com.banew.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import java.util.HashSet;
import java.util.Set;

public class LevelsDoorComponent implements Component {
    public String levelFrom;
    public String levelTo;
    public String singleName;

    public Set<Entity> isClosedSet = new HashSet<>();
}
