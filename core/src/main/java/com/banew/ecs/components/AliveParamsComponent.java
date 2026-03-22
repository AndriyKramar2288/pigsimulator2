package com.banew.ecs.components;

import com.badlogic.ashley.core.Component;
import com.banew.other.dto.AliveEntityInfo;
import com.banew.other.records.CursorPair;

public class AliveParamsComponent implements Component {
    public CursorPair attackCursor;
    public AliveEntityInfo info;

    public float reloadHpTimer = 0;
    public float reloadStaminaTimer = 0;

    public float reloadStaminaTime;
    public float getReloadStaminaSpeed;
    public float getReloadHpTime;
    public float getReloadHpSpeed;
}
