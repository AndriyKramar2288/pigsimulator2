package com.banew.other.enums;

import com.banew.containers.GlobalGameContext;
import lombok.Getter;

@Getter
public enum Skill implements SetupEnum {
    RUNNING("Біг"),
    ENDURANCE("Витривалість"),
    SPIRT_RESISTANCE("Толерантність до спирту"),
    NAZAREUS("nazareus (зобов'язаність)", "Icon43"),
    STATIC_MAGIC("Магія компіляції"),
    DYNAMIC_MAGIC("Магія інтерпретації"),
    FORBITTEN_MAGIC("Заборонені чаклунства"),
    ELECTRIC_MAGIC("Магія електрика");

    private final String ukrName;
    private final String iconRegion;
    private String desc = "";

    Skill(String urkName) {
        this(urkName, "");
    }

    Skill(String ukrName, String iconRegion) {
        this.ukrName = ukrName;
        this.iconRegion = "skills/" + iconRegion;
    }

    @Override
    public void setup(GlobalGameContext context) {
        desc = context.getCurrentLocalization().getRaceDescriptions().get(name());
    }
}
