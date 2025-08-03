package com.banew.other.enums;

import com.banew.containers.GlobalGameContext;
import com.banew.external.InitialRace;
import com.banew.items.AbstractItem;
import com.banew.other.records.MovingEntityTexturesPerDirectionPack;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
public enum Race implements SetupEnum {
    LV("Западенець"),
    VL("Волиняка"),
    PD("Порохобот"),
    OD("Жид"),
    KR("Кавун"),
    DN("Робітник офісу"),
    CH("Негр"),
    KY("Представник лівого берега Києва"),
    DC("Шахтар Доннбасса"),
    HK("Харківець");

    private final String ukrName;
    @Setter
    private AbstractItem initialItem;
    private String desc = "";
    private Map<String, MovingEntityTexturesPerDirectionPack> textures;

    Race(String ukrName) {
        this.ukrName = ukrName;
    }

    @Override
    public void setup(GlobalGameContext context) {
        InitialRace initialRace = context.getGeneralSettings().getRaces().get(name());
        if (initialRace == null) throw new RuntimeException("Раса " + name() + " чогось не має налаштувань!");
        if (initialRace.getSpritesheet() == null)
            throw new RuntimeException("Раса " + name() + " чогось не має текстури!");

        desc = initialRace.getDesc();
        textures = initialRace.getSpritesheet().extractTextures(context.getTextureAtlas());
    }
}
