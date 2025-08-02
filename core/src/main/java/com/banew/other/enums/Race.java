package com.banew.other.enums;

import com.banew.containers.GlobalGameContext;
import com.banew.items.AbstractItem;
import lombok.Getter;
import lombok.Setter;

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

    Race(String ukrName) {
        this.ukrName = ukrName;
    }

    @Override
    public void setup(GlobalGameContext context) {
        desc = context.getCurrentLocalization().getRaceDescriptions().get(name());
    }
}
