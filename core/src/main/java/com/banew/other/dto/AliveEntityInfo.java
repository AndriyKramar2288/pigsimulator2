package com.banew.other.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AliveEntityInfo {
    protected float health;
    protected float stamina;
    protected float maxStamina;
    protected float maxHp;
    protected float attackDistance;

    public void changeHealth(float step) {
        health += step;
        if (health < 0) health = 0;
        if (health > maxHp) health = maxHp;
    }

    public void changeStamina(float step) {
        stamina += step;
        if (stamina < 0) stamina = 0;
        if (stamina > maxHp) stamina = maxHp;
    }
}
