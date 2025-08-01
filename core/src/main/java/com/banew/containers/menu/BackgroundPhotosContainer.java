package com.banew.containers.menu;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import java.util.List;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class BackgroundPhotosContainer {
    private final Array<Image> backgroundImages = new Array<>();
    private int currentIndex = 0;

    public BackgroundPhotosContainer(List<String> photos, TextureAtlas atlas, Stage stage) {
        // Додавання зображень як акторів
        photos.stream()
            .map(atlas::findRegion)
            .forEach(region -> {
                Image image = new Image(region);
                image.setFillParent(true); // займає весь екран
                image.getColor().a = 0f; // стартово прозорий
                backgroundImages.add(image);
                stage.addActor(image);
            });
        // Старт першого зображення
        showImage(0);
    }

    private void showImage(int index) {
        Image image = backgroundImages.get(index);
        image.getColor().a = 0f;
        image.setScale(1f);
        image.setOrigin(Align.center);
        image.setPosition(0, 0);
        image.addAction(sequence(
            parallel(
                fadeIn(2f),
                scaleTo(1.1f, 1.1f, 6f), // зум повільний
                moveBy(30, 20, 6f) // плавний рух
            ),
            parallel(
                fadeOut(2f),
                moveBy(60, 40, 6f),
                run(() -> {
                    currentIndex = (index + 1) % backgroundImages.size;
                    showImage(currentIndex);
                })
            )
        ));
    }
}
