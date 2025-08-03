package com.banew.containers.menu.character.creation;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.banew.containers.GlobalGameContext;
import com.banew.other.enums.Skill;

public class SkillViewTable extends Table {
    private final GlobalGameContext context;
    private Skill lastSkill;

    public SkillViewTable(GlobalGameContext context) {
        this.context = context;
        setBackground(new TextureRegionDrawable(
            context.getTextureAtlas().findRegion("gui/skills_back_info")
        ));
        doNotView();
    }

    public void view(Skill skill) {
        if (isLastSkill(skill)) return;

        clearChildren();
        top();
        addLabel(skill.getUkrName(), .45f);
        addLabel(skill.getDesc(), .3f);
    }

    public void doNotView() {
        if (isLastSkill(null)) return;

        clearChildren();
        center();
        addLabel("Для перегляду інформації про навик... наведіться на навик", .4f);
        Image image = new Image(new TextureRegionDrawable(
            context.getTextureAtlas().findRegion("gui/ochko")
        ));
        add(image).size(
            Value.percentWidth(.3f, this),
            Value.percentHeight(.1f, this)
        ).padTop(Value.percentHeight(.1f, this));
    }

    /**
     * Перевіряє, чи деякий skill рівний попередньому (тому, що, як передбачається, відображається),
     * і присвоює одержаний skill, як попередній
     * @param skill для порівняння
     * @return чи рівний тому, що, як очікується, відображається
     */
    private boolean isLastSkill(Skill skill) {
        Skill beforeLastSkill = lastSkill;
        lastSkill = skill;
        return skill == beforeLastSkill;
    }

    private void addLabel(String text, float size) {
        Label label = new Label(
            text, context.getMainSkin()
        );
        label.setWrap(true);
        context.getDynamicLabelsContainer().put(label, size);
        add(label)
            .padBottom(Value.percentHeight(.025f, this))
            .width(Value.percentWidth(.7f, this));
        row();
    }
}
