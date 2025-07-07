package com.banew.containers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.banew.other.dto.PlayerInfo;
import com.banew.other.records.GameContext;

public class GuiContainer implements Disposable {
    private final Stage stage;
    private final Viewport guiViewport;

    private final PlayerInfo playerInfo;
    private final ProgressBar staminaBar;
    private final ProgressBar hpBar;

    public GuiContainer (PlayerInfo playerInfo) {
        this.playerInfo = playerInfo;
        guiViewport = new ScreenViewport();
        stage = new Stage(guiViewport);

        Skin skin = new Skin(Gdx.files.internal("skin/freezing-ui.json"));

        Table table = new Table(skin);
        table.setFillParent(true);
        stage.addActor(table);

        staminaBar = new ProgressBar(0, playerInfo.getMaxPlayerStamina(), 1, false, skin);
        hpBar = new ProgressBar(0, playerInfo.getMaxPlayerHp(), 1, false, skin);
        hpBar.setColor(Color.RED);

        table.bottom().left();

        table.add(staminaBar)
            .width(Value.percentWidth(0.1f, table))
            .height(Value.percentHeight(0.0625f, table))
            .padBottom(10)
            .padLeft(10)
            .padRight(10);

        table.add(hpBar)
            .width(Value.percentWidth(0.1f, table))
            .height(Value.percentHeight(0.0625f, table))
            .padBottom(10);

        Gdx.input.setInputProcessor(stage);
    }

    public void resize(int width, int height) {
        guiViewport.update(width, height, true);
    }

    public void render(GameContext context) {
        staminaBar.setValue(context.playerInfo().getPlayerStamina());
        hpBar.setValue(context.playerInfo().getPlayerHealth());

        guiViewport.apply();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
