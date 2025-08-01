package com.banew.containers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Disposable;
import com.banew.containers.game.gui.DynamicLabelsContainer;
import com.banew.containers.menu.MenuButtonsListener;
import com.banew.containers.menu.MenuContainer;
import com.banew.external.GeneralSettings;
import com.banew.utilites.Reference;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

public final class GlobalGameContext implements Disposable {
    private final Reference<Container> currentContainerRef = new Reference<>(null);
    private final Set<Container> containerSet = new HashSet<>();
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

    private Vector2 lastSize;

    public GlobalGameContext(
        GeneralSettings generalSettings
    ) {
        this.generalSettings = generalSettings;

        textureAtlas = new TextureAtlas(Gdx.files.internal(generalSettings.getMain_atlas_src()));
        mainSkin = new Skin(Gdx.files.internal(generalSettings.getMain_skin_src()));
        soundContainer = new SoundContainer(generalSettings);
        dynamicLabelsContainer = new DynamicLabelsContainer();

        setContainer(new MenuContainer(this));
    }

    public void setContainer(Container container) {
        containerSet.add(container);
        currentContainerRef.setElement(container);
        if (lastSize != null) {
            container.resize((int) lastSize.x, (int) lastSize.y);
        }
    }

    public Container currentContainer() {
        return currentContainerRef.getElement();
    }

    @Override
    public void dispose() {
        containerSet.forEach(Disposable::dispose);
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
