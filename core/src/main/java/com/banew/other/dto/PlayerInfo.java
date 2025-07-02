package com.banew.other.dto;

import lombok.Data;

@Data
public class PlayerInfo {
    private float playerHealth = 100f;
    private float playerStamina = 100f;
    private float maxPlayerStamina = 100f;
    private float maxPlayerHp = 100f;
}
