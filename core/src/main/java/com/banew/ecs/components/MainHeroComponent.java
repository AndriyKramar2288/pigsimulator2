package com.banew.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.banew.other.dto.PlayerInfo;

public class MainHeroComponent implements Component {
    public PlayerInfo playerInfo;
    public Entity openedContainer = null; // Посилання на відкриту скриню/труп
    public boolean isRunning = false;
}
