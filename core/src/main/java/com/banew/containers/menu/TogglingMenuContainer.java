package com.banew.containers.menu;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class TogglingMenuContainer extends Table {
    public void centerInViewport(Viewport viewport) {
        float width = viewport.getScreenWidth() * 0.9f;
        float height = viewport.getScreenHeight() * 0.9f;
        setSize(width, height);
        setPosition(
            (viewport.getScreenWidth() - width) / 2f,
            (viewport.getScreenHeight() - height) / 2f
        );
    }

    public void toggleOn(Viewport viewport) {
        setVisible(true);
        Vector2 currentPosition = new Vector2(
            getX(), getY()
        );
        setPosition(viewport.getScreenWidth(), getY()); // справа за екраном
        addAction(moveTo(
            currentPosition.x, currentPosition.y,
            0.7f,
            Interpolation.elasticOut
        ));
    }

    public void toggleOff(Viewport viewport) {
        Vector2 currentPosition = new Vector2(
            getX(), getY()
        );
        addAction(sequence(
            moveTo(
                viewport.getScreenWidth(), currentPosition.y,
                0.7f,
                Interpolation.elasticOut
            ),
            run(() -> {
                setVisible(false);
                setPosition(currentPosition.x, currentPosition.y);
            })
        ));
    }
}
