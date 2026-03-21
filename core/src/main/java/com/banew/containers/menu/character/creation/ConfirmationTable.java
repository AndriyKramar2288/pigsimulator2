package com.banew.containers.menu.character.creation;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.banew.containers.GlobalGameContext;
import com.banew.containers.game.GameContainer;
import com.banew.containers.menu.AbstractTogglingTable;
import com.banew.containers.menu.MenuContainer;

public class ConfirmationTable extends AbstractTogglingTable {
    private final TextButton nextButton;
    private final NewCharacterProperties properties;

    public ConfirmationTable(GlobalGameContext context, MenuContainer menuContainer) {
        super(context, menuContainer);

        properties = menuContainer.getNewCharacterProperties();

        setBackground(
            new TextureRegionDrawable(context.getTextureAtlas().findRegion("gui/create_character_back"))
        );
        top();

        Label topLabel = new Label("Створення персонажа: підтвердження", context.getMainSkin());
        context.getDynamicLabelsContainer().put(topLabel, .4f);
        add(topLabel).padBottom(Value.percentHeight(.1f, this));
        row();

        Table innerTable = new Table();
        innerTable.setBackground(new TextureRegionDrawable(
            context.getTextureAtlas().findRegion("gui/confirmation_back")
        ));
        innerTable.pad(Value.percentWidth(.05f, this));
        add(innerTable);

        Label nameLabel = new Label("Введіть внутрішньоігрове ім'я персонажа:", context.getMainSkin());
        context.getDynamicLabelsContainer().put(nameLabel, .4f);
        innerTable.add(nameLabel).top().right();

        TextField textField = new TextField("ім'я", context.getMainSkin());
        innerTable.add(textField)
            .pad(Value.percentHeight(.01f, this))
            .width(Value.percentWidth(.2f, this));
        innerTable.row();
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                properties.setName(textField.getText());
                check();
            }
        });

        Label bioLabel = new Label("Введіть внутрішньоігрову біографію персонажа:", context.getMainSkin());
        context.getDynamicLabelsContainer().put(bioLabel, .4f);
        innerTable.add(bioLabel).top().right()
            .padTop(Value.percentHeight(.01f, this));;

        TextArea bioField = new TextArea("біо", context.getMainSkin());
        innerTable.add(bioField)
            .size(
                Value.percentWidth(.2f, this),
                Value.percentWidth(.1f, this)
            ).pad(Value.percentHeight(.01f, this));
        bioField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                properties.setBio(bioField.getText());
                check();
            }
        });

        row();
        Table buttonsWrap = new Table();
        addButton(buttonsWrap, .15f, .075f, .4f, "Повернутись", context, () -> {
            toggleOff(menuContainer.viewport());
            menuContainer.toggleSkills();
        });
        nextButton = addButton(buttonsWrap, .15f, .075f, .4f, "Почати гру!", context, () -> {
            context.setContainer(new GameContainer(context, properties));
        }).getActor();
        nextButton.setVisible(false);
        add(buttonsWrap);
    }

    private void check() {
        nextButton.setVisible(properties.getName().length() >= 5 && properties.getBio().length() >= 10);
    }
}
