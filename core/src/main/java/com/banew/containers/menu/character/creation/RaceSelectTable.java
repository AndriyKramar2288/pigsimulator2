package com.banew.containers.menu.character.creation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.banew.containers.GlobalGameContext;
import com.banew.containers.menu.MenuContainer;
import com.banew.containers.menu.AbstractTogglingTable;
import com.banew.other.enums.Race;

public class RaceSelectTable extends AbstractTogglingTable {
    public RaceSelectTable(GlobalGameContext context, MenuContainer menuContainer) {
        super(context, menuContainer);

        setBackground(
            new TextureRegionDrawable(context.getTextureAtlas().findRegion("gui/create_character_back"))
        );

        Label topLabel = new Label("Створення персонажа: вибір раси", context.getMainSkin());
        center().top().add(topLabel);
        context.getDynamicLabelsContainer().put(topLabel, .4f);

        Table innerTable = new Table().left().top();
        row();
        add(innerTable)
            .width(Value.percentWidth(.9f, this))
            .row();
        RaceViewTable raceViewer = new RaceViewTable(this, context);

        //
        Image image = new Image(new TextureRegionDrawable(
            context.getTextureAtlas().findRegion("gui/g22")
        ));
        innerTable.add(image).width(Value.percentWidth(.5f, this));
        Pixmap maskPixmap = new Pixmap(Gdx.files.internal("textures/gui/g22_mask.png"));

        //
        innerTable.add(raceViewer).top();

        //
        pad(Value.percentWidth(.03f, this));
        Table bufferTable = new Table();
        addButton(bufferTable, .15f, .075f, .4f, "Повернутись", context, () -> {
            toggleOff(menuContainer.viewport());
            menuContainer.toggleMain();
        });
        TextButton nextButton = addButton(bufferTable,.15f, .075f, .4f, "Продовжити", context, () -> {
            toggleOff(menuContainer.viewport());
            menuContainer.toggleSkills();
        }).getActor();
        innerTable.row();
        innerTable.add(bufferTable);
        nextButton.setVisible(false);

        // -----------
        image.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Color color = new Color();
                float apr_x = (x / image.getWidth()) * maskPixmap.getWidth();
                float apr_y = (1 - (y / image.getHeight())) * maskPixmap.getHeight();
                Color.rgba8888ToColor(color, maskPixmap.getPixel((int) apr_x, (int) apr_y));
                Race race = switch (color.toString().toUpperCase()) {
                    case "0026FFFF" -> Race.VOLYNYAKA;
                    case "FF0000FF" -> Race.ZAPADENEC;
                    case "FF006EFF" -> Race.POROHOBOT;
                    case "4CFF00FF" -> Race.LEFT_BANK_VILLAGER;
                    case "FFD800FF" -> Race.NIGER;
                    case "B200FFFF" -> Race.HARKOVSKII;
                    case "0094FFFF" -> Race.OFFICE_MAN;
                    case "FF00DCFF" -> Race.COAL_MINER;
                    case "00FFFFFF" -> Race.KAVUN;
                    case "7F0037FF" -> Race.JEW;
                    default -> null;
                };
                if (race != null) {
                    context.getSoundContainer().play("click_button");
                    raceViewer.showRace(race, context);
                    menuContainer.getNewCharacterProperties().setRace(race);
                    nextButton.setVisible(true);
                }
            }
        });
    }
}
