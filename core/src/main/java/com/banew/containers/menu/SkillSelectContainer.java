package com.banew.containers.menu;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.banew.containers.GlobalGameContext;

public class SkillSelectContainer extends TogglingMenuContainer {
    public SkillSelectContainer(MenuContainer menuContainer, GlobalGameContext context) {
        super(context, menuContainer);

        setBackground(
            new TextureRegionDrawable(context.getTextureAtlas().findRegion("gui/create_character_back"))
        );

        Label topLabel = new Label("Створення персонажа: вибір навиків", context.getMainSkin());
        center().top().add(topLabel);
        context.getDynamicLabelsContainer().put(topLabel, .4f);

        Table innerTable = new Table().left().top();
        row();
        add(innerTable).width(Value.percentWidth(.9f, this));
    }
}
