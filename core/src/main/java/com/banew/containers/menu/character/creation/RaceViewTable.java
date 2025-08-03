package com.banew.containers.menu.character.creation;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.banew.containers.GlobalGameContext;
import com.banew.other.enums.Race;

public class RaceViewTable extends Table {
    private Table currentRaceViewTable;

    public RaceViewTable(Table outerTable, GlobalGameContext context) {
        //
        String desc =
            "Обравши походження свого персонажа, ви визначите його майбутній талант,"
                + " можливий бонусний стартовий предмет, а також те, "
                + "наскільки він ефективно буде здобувати ті, чи інші"
                + " загальноігрові навички.";
        Label raceLabel = new Label(desc, context.getMainSkin());
        context.getDynamicLabelsContainer().put(raceLabel, .3f);
        raceLabel.setWrap(true);
        add(raceLabel)
            .top()
            .padTop(Value.percentHeight(.05f, outerTable))
            .width(Value.percentWidth(.4f, outerTable));
        row();
    }

    private Label createLabel(String text, float size, GlobalGameContext context) {
        Label label = new Label(text, context.getMainSkin());
        label.setWrap(true);
        context.getDynamicLabelsContainer().put(label, size);
        return label;
    }

    public void showRace(Race race, GlobalGameContext context) {
        Table table = new Table();

        table.add(createLabel(race.getUkrName(), .8f, context))
            .pad(Value.percentWidth(.02f, this))
            .width(Value.percentWidth(.9f, this));
        table.row();
        table.add(createLabel(race.getDesc(), .4f, context))
            .width(Value.percentWidth(.9f, this))
            .padBottom(Value.percentHeight(.05f, this));
        if (race.getInitialItem() != null) {
            table.row();
            table.add(generateItemIconDisplayer(race, context));
        }

        if (currentRaceViewTable != null) removeActor(currentRaceViewTable);
        currentRaceViewTable = table;
        row();
        add(table);
    }

    private Table generateItemIconDisplayer(Race race, GlobalGameContext context) {
        Table table = new Table();
        table.setBackground(new TextureRegionDrawable(
            context.getTextureAtlas().findRegion("gui/hot_keys")
        ));

        table.pad(Value.percentWidth(.025f, this));
        center();

        table.add(createLabel("Бонусний стартовий предмет: " + race.getInitialItem().getName(), .4f, context))
            .width(Value.percentWidth(.5f, this))
            .padBottom(Value.percentHeight(.05f, this));

        table.add(new Image(race.getInitialItem().getTextureRegion()))
            .size(Value.percentWidth(.15f, this));

        return table;
    }
}
