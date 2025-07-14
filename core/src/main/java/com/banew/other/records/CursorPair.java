package com.banew.other.records;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;

public record CursorPair(
    Cursor availableCursor,
    Cursor notAvailableCursor
) {
    public void use(boolean isAvailable) {
        Gdx.graphics.setCursor(isAvailable ? availableCursor : notAvailableCursor);
    }
}
