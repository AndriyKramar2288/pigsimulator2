package com.banew.other.enums;

import com.banew.containers.GlobalGameContext;
import lombok.Getter;

@Getter
public enum Skill implements SetupEnum {
    RUNNING("Біг", "Icon7"),
    ENDURANCE("Витривалість", "Icon2"),
    SPIRT_RESISTANCE("Толерантність до спирту", "Icon3"),
    NAZAREUS("nazareus (зобов'язаність)", "Icon43"),
    STATIC_MAGIC("Магія компіляції", "Icon4"),
    DYNAMIC_MAGIC("Магія інтерпретації", "Icon5"),
    FORBITTEN_MAGIC("Заборонені чаклунства", "Icon6"),
    ELECTRIC_MAGIC("Магія електрика", "Icon1");

    private final String ukrName;
    private final String iconRegion;
    private String desc = "";

    Skill(String urkName) {
        this(urkName, "");
    }

    Skill(String ukrName, String iconRegion) {
        this.ukrName = ukrName;
        this.iconRegion = iconRegion;
    }

    @Override
    public void setup(GlobalGameContext context) {
        desc = context.getGeneralSettings().getSkillDescriptions().get(name());
    }
}
