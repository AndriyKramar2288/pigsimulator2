package com.banew.external;

import lombok.Data;

@Data
public class InitialGameLevel {
    private String levelName;
    private String mapName;
    private String lightMode = "";
}
