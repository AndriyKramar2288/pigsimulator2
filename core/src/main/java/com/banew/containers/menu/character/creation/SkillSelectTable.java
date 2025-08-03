package com.banew.containers.menu.character.creation;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.banew.containers.GlobalGameContext;
import com.banew.containers.menu.MenuContainer;
import com.banew.containers.menu.AbstractTogglingTable;
import com.banew.other.enums.Skill;
import lombok.Data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkillSelectTable extends AbstractTogglingTable {
    private final DragAndDrop dragAndDrop;
    private final Map<Integer, Skill> map = new HashMap<>();
    private final Map<Integer, Image> buttonMap = new HashMap<>();
    private final SkillViewTable skillViewTable;

    private final SkillCategory mainSkills;
    private final SkillCategory majorSkills;
    private final SkillCategory minorSkills;
    private final TextButton nextButton;

    private final MenuContainer menuContainer;

    private int dragged_slot = -1;

    public SkillSelectTable(MenuContainer menuContainer, GlobalGameContext context) {
        super(context, menuContainer);
        dragAndDrop = new DragAndDrop();

        this.menuContainer = menuContainer;

        setBackground(
            new TextureRegionDrawable(context.getTextureAtlas().findRegion("gui/create_character_back"))
        );
        center();

        Label topLabel = new Label("Створення персонажа: вибір навиків", context.getMainSkin());
        center().top().add(topLabel)
            .padBottom(Value.percentHeight(.08f, this))
            .colspan(2);
        context.getDynamicLabelsContainer().put(topLabel, .4f);

        Table innerTable = new Table().left().top();
        row();
        add(innerTable).width(Value.percentWidth(.6f, this));

        mainSkills = new SkillCategory("Головні", 101, 102);
        majorSkills = new SkillCategory("Важливі", 103, 104);
        minorSkills = new SkillCategory("Побічні", 105, 106);
        innerTable.add(mainSkills.categoryWrap(context)).colspan(2);
        innerTable.add(majorSkills.categoryWrap(context)).colspan(2);
        innerTable.add(minorSkills.categoryWrap(context)).colspan(2);
        innerTable.row();

        for (int i = 0; i < Skill.values().length; i++) {
            if (i % 6 == 0) innerTable.row();

            Image button = new Image();
            button.addListener(new InfoButtonListener(i));
            innerTable.add(wrap(button, context.getTextureAtlas()))
                .padTop(Value.percentHeight(.025f, this))
                .size(Value.percentWidth(.1f, this));
            buttonMap.put(i, button);
            map.put(i, Skill.values()[i]);
            setUpDragAndDrop(button, i, context);
        }
        reloadButtons(context.getTextureAtlas());
        // ------------------------------

        skillViewTable = new SkillViewTable(context);
        add(skillViewTable)
            .padLeft(Value.percentWidth(.075f, this))
            .size(
                Value.percentWidth(.25f, this),
                Value.percentHeight(.7f, this)
            );

        row();
        Table buttonsWrap = new Table();
        addButton(buttonsWrap, .15f, .075f, .4f, "Повернутись", context, () -> {
            toggleOff(menuContainer.viewport());
            menuContainer.toggleRace();
            skillViewTable.doNotView();
        });
        nextButton = addButton(buttonsWrap, .15f, .075f, .4f, "Перейти далі", context, () -> {
            toggleOff(menuContainer.viewport());
            menuContainer.toggleConfirmation();
        }).getActor();
        nextButton.setVisible(false);
        add(buttonsWrap).colspan(2);
    }

    private void reloadButtons(TextureAtlas atlas) {
        buttonMap.forEach((k, v) -> {
            Skill skill = map.get(k);
            if (skill != null && k != dragged_slot) {
                v.setDrawable(new TextureRegionDrawable(
                    atlas.findRegion("skills/" + skill.getIconRegion())
                ));
            }
            else {
                v.setDrawable(new BaseDrawable());
            }
        });

        if (mainSkills.isAllSelected() && majorSkills.isAllSelected() && minorSkills.isAllSelected()) {
            nextButton.setVisible(true);
            var p = menuContainer.getNewCharacterProperties();

            p.setMajorSkills(majorSkills.getList());
            p.setMinorSkills(minorSkills.getList());
            p.setMainSkills(mainSkills.getList());
        }
    }

    private Table wrap(Actor actor, TextureAtlas atlas) {
        Table table = new Table();
        table.setBackground(new TextureRegionDrawable(
            atlas.findRegion("gui/skills_default")
        ));
        table.center().pad(Value.percentWidth(.035f, this));
        table.add(actor)
            .size(Value.percentWidth(.05f, this));
        return table;
    }

    private void setUpDragAndDrop(Actor button, int index, GlobalGameContext context) {
        // drag source
        dragAndDrop.addSource(new DragAndDrop.Source(button) {
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                Skill skill = map.get(index);
                if (skill == null) return null;
                TextureRegion region = context.getTextureAtlas().findRegion("skills/" + skill.getIconRegion());
                if (region == null) return null;

                DragAndDrop.Payload payload = new DragAndDrop.Payload();

                Image dragImage = new Image(new TextureRegionDrawable(region));
                dragImage.setSize(32, 32); // розмір іконки під час перетягування

                payload.setDragActor(dragImage);
                payload.setObject(index); // зберігаємо індекс джерела
                dragged_slot = index;

                context.getSoundContainer().play("inv_drop");
                reloadButtons(context.getTextureAtlas());
                return payload;
            }

            public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                if (target == null) {
                    dragged_slot = -1;
                    reloadButtons(context.getTextureAtlas());
                }
            }
        });

        // drop target
        dragAndDrop.addTarget(new DragAndDrop.Target(button) {
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                return true; // дозволити дроп
            }

            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                int sourceData = (int) payload.getObject();

                // обмін слотами
                Skill fromRegion = map.get(sourceData);
                Skill toRegion = map.get(index);

                map.put(index, fromRegion);
                map.put(sourceData, toRegion);
                dragged_slot = -1;

                context.getSoundContainer().play("inv_drop");
                reloadButtons(context.getTextureAtlas());
                skillViewTable.view(fromRegion);
            }
        });
    }

    @Data
    private class SkillCategory {
        private int[] indexes;
        private String name;

        public SkillCategory(String name, int ... indexes) {
            this.name = name;
            this.indexes = indexes;
        }

        public boolean isAllSelected() {
            return Arrays.stream(indexes)
                .allMatch(i -> map.get(i) != null);
        }

        public List<Skill> getList() {
            return Arrays.stream(indexes)
                .boxed()
                .map(map::get)
                .toList();
        }

        public Table categoryWrap(GlobalGameContext context) {
            Table table = new Table();
            table.setBackground(new TextureRegionDrawable(
                context.getTextureAtlas().findRegion("gui/skills_back")
            ));
            table.top().pad(Value.percentWidth(.01f, SkillSelectTable.this)).padTop(0);

            Label label = new Label(name, context.getMainSkin());
            context.getDynamicLabelsContainer().put(label, .4f);
            table.add(label)
                .padBottom(Value.percentHeight(.0025f, SkillSelectTable.this))
                .colspan(indexes.length);
            table.row();

            for (int index : indexes) {
                Image button = new Image();
                button.addListener(new InfoButtonListener(index));
                table.add(wrap(button, context.getTextureAtlas()))
                    .size(Value.percentWidth(.1f, SkillSelectTable.this));
                buttonMap.put(index, button);
                setUpDragAndDrop(button, index, context);
            }

            return table;
        }
    }

    private class InfoButtonListener extends InputListener {
        private final int index;

        public InfoButtonListener(int index) {
            this.index = index;
        }

        @Override
        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
            super.enter(event, x, y, pointer, fromActor);
            Skill skill = map.get(index);
            if (skill != null && dragged_slot == -1) {
                skillViewTable.view(skill);
            }
        }
    }
}
