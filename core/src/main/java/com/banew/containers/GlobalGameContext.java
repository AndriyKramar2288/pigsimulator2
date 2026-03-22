package com.banew.containers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Disposable;
import com.banew.containers.game.CursorsContainer;
import com.banew.containers.game.gui.DynamicLabelsContainer;
import com.banew.containers.menu.MenuButtonsListener;
import com.banew.containers.menu.MenuContainer;
import com.banew.external.GeneralSettings;
import com.banew.items.StupidItem;
import com.banew.other.enums.Race;
import com.banew.other.enums.SetupEnum;
import com.banew.other.enums.Skill;
import lombok.Getter;

public final class GlobalGameContext implements Disposable {
    private Container currentContainer;
    @Getter
    private final SoundContainer soundContainer;
    @Getter
    private final TextureAtlas textureAtlas;
    @Getter
    private final Skin mainSkin;
    @Getter
    private final GeneralSettings generalSettings;
    @Getter
    private final DynamicLabelsContainer dynamicLabelsContainer;
    @Getter
    private final CursorsContainer cursorsContainer;

    private Vector2 lastSize;

    public GlobalGameContext(
        GeneralSettings generalSettings
    ) {
        this.generalSettings = generalSettings;

        textureAtlas = new TextureAtlas(Gdx.files.internal(generalSettings.getMain_atlas_src()));
        mainSkin = new Skin(Gdx.files.internal(generalSettings.getMain_skin_src()));
        soundContainer = new SoundContainer(generalSettings);
        dynamicLabelsContainer = new DynamicLabelsContainer();
        cursorsContainer = new CursorsContainer();

        setupEnum(Race.values());
        setupEnum(Skill.values());

        Race.OD.setInitialItem(
            new StupidItem(getTextureAtlas().findRegion("hryak1/tile000"), "Хряк")
        );

        setContainer(new MenuContainer(this));
    }

    private void setupEnum(Enum<?>[] values) {
        for (Enum<?> value : values) {
            if (value instanceof SetupEnum) {
                ((SetupEnum) value).setup(this);
            }
        }
    }

    public void setContainer(Container container) {
        if (currentContainer != null)
            currentContainer.dispose();

        currentContainer = container;

        if (lastSize != null) {
            container.resize((int) lastSize.x, (int) lastSize.y);
        }
    }

    public Container currentContainer() {
        return currentContainer;
    }

    @Override
    public void dispose() {
        currentContainer.dispose();
    }

    public void renderCurrent() {
        currentContainer().render();
    }

    public void resizeCurrent(int width, int height) {
        currentContainer().resize(width, height);
        lastSize = new Vector2(width, height);
    }

    public void initButton(TextButton textButton, float textScale) {
        dynamicLabelsContainer.put(textButton.getLabel(), textScale);
        textButton.addListener(new MenuButtonsListener(soundContainer));
    }
}
