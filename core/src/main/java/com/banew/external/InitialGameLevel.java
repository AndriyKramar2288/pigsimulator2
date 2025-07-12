package com.banew.external;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class InitialGameLevel {
    private String levelName;
    private String mapName;
    private String lightMode = "";
    private List<String> musicPatterns = new ArrayList<>();
    private String defaultWalkSound = "walk_default";
}
