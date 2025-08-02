package com.banew.other.enums;

import com.banew.items.AbstractItem;
import com.banew.items.StupidItem;
import lombok.Getter;
import lombok.Setter;

@Getter
public enum Race {
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
    @Setter
    private String desc = "";

    Race(String ukrName) {
        this.ukrName = ukrName;
    }
}
