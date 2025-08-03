package com.banew.containers.menu;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.banew.containers.GlobalGameContext;
import com.banew.other.enums.Skill;

public class SkillViewContainer extends Table {
    private final GlobalGameContext context;

    public SkillViewContainer(GlobalGameContext context) {
        this.context = context;
        setBackground(new TextureRegionDrawable(
            context.getTextureAtlas().findRegion("gui/skills_back_info")
        ));
        doNotView();
    }

    public void view(Skill skill) {
        clearChildren();
        top();
        addLabel(skill.getUkrName(), .45f);
        addLabel(skill.getDesc(), .3f);
    }

    public void doNotView() {
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
