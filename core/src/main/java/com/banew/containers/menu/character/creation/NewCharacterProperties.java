package com.banew.containers.menu.character.creation;

import com.banew.other.enums.Race;
import com.banew.other.enums.Skill;
import lombok.Data;

import java.util.List;

@Data
public class NewCharacterProperties {
    private Race race;
    private List<Skill> mainSkills;
    private List<Skill> majorSkills;
    private List<Skill> minorSkills;
    private String name = "";
    private String bio = "";
}
