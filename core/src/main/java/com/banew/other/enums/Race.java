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
    ZAPADENEC("Западенець"),
    VOLYNYAKA("Волиняка"),
    POROHOBOT("Порохобот"),
    JEW("Жид"),
    KAVUN("Кавун"),
    OFFICE_MAN("Робітник офісу"),
    NIGER("Негр"),
    LEFT_BANK_VILLAGER("Представник лівого берега Києва"),
    COAL_MINER("Шахтар Доннбасса"),
    HARKOVSKII("Харківець");

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
        if (initialRace == null) return;
        desc = initialRace.getDesc();
        if (initialRace.getSpritesheet() != null)
            textures = initialRace.getSpritesheet().extractTextures(context.getTextureAtlas());
    }
}
