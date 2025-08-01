package com.banew.containers.menu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.banew.containers.GlobalGameContext;

public class NewGameContainer extends TogglingMenuContainer {
    public NewGameContainer(MenuContainer menuContainer, GlobalGameContext context) {
        setBackground(
            new TextureRegionDrawable(context.getTextureAtlas().findRegion("gui/create_character_back"))
        );

        top();
        Label topLabel = new Label("Створення персонажа", context.getMainSkin());
        add(topLabel);
        context.getDynamicLabelsContainer().put(topLabel, .4f);

        TextButton textButton = new TextButton("Повернутись", context.getMainSkin());
        context.initButton(textButton, .4f);
        row();

        add(textButton)
            .width(Value.percentWidth(.15f, this))
            .height(Value.percentHeight(.075f, this))
            .pad(10);;

        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                toggleOff(menuContainer.viewport());
                menuContainer.getFrontMainMenuContainer().toggleOn(menuContainer.viewport());
            }
        });

        setVisible(false);

        pad(Value.percentWidth(.03f, this));

        centerInViewport(menuContainer.viewport());
        menuContainer.getStage().addActor(this);
    }
}
