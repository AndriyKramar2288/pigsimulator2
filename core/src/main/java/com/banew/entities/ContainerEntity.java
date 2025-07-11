package com.banew.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.banew.items.AbstractItem;
import com.banew.other.records.GameContext;

public abstract class ContainerEntity extends SpriteEntity {
    public ContainerEntity(Sprite sprite, Body body, Vector2 collisionScales) {
        super(sprite, body, collisionScales);
    }
//    private final Cursor fineCursor;
//    private final Cursor badCursor;
//
//    public static final float CRITICAL_DISTANCE = .5f;
//
//    public abstract int getSize();
//    public abstract String getName();
//
//    public AbstractItem getItem(int index) {
//        return itemMap.get(index - getSize());
//    }
//
//    public void putItem(int index, AbstractItem item) {
//        if (index - getSize() >= 0) {
//            itemMap.put(index - getSize(), item);
//        }
//        else throw new RuntimeException("сюди нізя!");
//    }
//
//    private boolean touchDown(int screenX, int screenY, Camera camera) {
//        // Конвертація координат з екрану у світ
//        Vector3 touchPos = new Vector3(screenX, screenY, 0);
//        camera.unproject(touchPos); // важливо: без цього буде криво
//
//        // Координати спрайта
//        float x = getSprite().getX();
//        float y = getSprite().getY();
//        float width = getSprite().getWidth();
//        float height = getSprite().getHeight();
//
//        if (touchPos.x >= x && touchPos.x <= x + width &&
//            touchPos.y >= y && touchPos.y <= y + height) {
//            return true;
//        }
//
//        return false;
//    }
//
//    @Override
//    public void render(GameContext context) {
//        if (touchDown(
//            Gdx.input.getX(), Gdx.input.getY(), context.camera()
//        )) {
//            if (context.mainHeroEntity().getCenterCoordinates().sub(getCenterCoordinates()).len2() < CRITICAL_DISTANCE) {
//                Gdx.graphics.setCursor(fineCursor);
//                if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
//                    System.out.println("Перемога!");
//                    context.mainHeroEntity().setOpenedContainer(this);
//                }
//            }
//            else {
//                Gdx.graphics.setCursor(badCursor);
//            }
//        }
//    }
//
//    public ContainerEntity(Sprite sprite, Body body, Vector2 collisionScales, Cursor fineCursor, Cursor badCursor) {
//        super(sprite, body, collisionScales);
//        this.fineCursor = fineCursor;
//        this.badCursor = badCursor;
//    }
}
