package com.banew.containers.menu.character.creation;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.banew.containers.GlobalGameContext;
import com.banew.containers.menu.AbstractTogglingTable;
import com.banew.containers.menu.MenuContainer;

public class ConfirmationTable extends AbstractTogglingTable {
    private final TextButton nextButton;

    public ConfirmationTable(GlobalGameContext context, MenuContainer menuContainer) {
        super(context, menuContainer);

        setBackground(
            new TextureRegionDrawable(context.getTextureAtlas().findRegion("gui/create_character_back"))
        );
        center();

        row();
        Table buttonsWrap = new Table();
        addButton(buttonsWrap, .15f, .075f, .4f, "Повернутись", context, () -> {
            toggleOff(menuContainer.viewport());
            menuContainer.toggleSkills();
        });
        nextButton = addButton(buttonsWrap, .15f, .075f, .4f, "Перейти далі", context, () -> {
//            toggleOff(menuContainer.viewport());
//            menuContainer.toggleConfirmation();
        }).getActor();
        nextButton.setVisible(false);
        add(buttonsWrap);
    }


}
