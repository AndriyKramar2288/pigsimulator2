package com.banew.other.dto;

import lombok.Data;

@Data
public class AliveEntityInfo {
    private float health = 100f;
    private float stamina = 100f;
    private float maxStamina = 100f;
    private float maxHp = 100f;
    private float attackDistance = .7f;
}
