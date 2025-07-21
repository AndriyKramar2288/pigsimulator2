package com.banew.containers.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.banew.other.records.CursorPair;

import java.util.HashMap;
import java.util.Map;

public class CursorsContainer {
    private final Map<String, Cursor> cursorsMap = new HashMap<>();

    public CursorsContainer() {
        for (FileHandle cursorFile : Gdx.files.internal("textures/cursors").list()) {
            Pixmap pixmap = new Pixmap(cursorFile);
            cursorsMap.put(cursorFile.nameWithoutExtension(), Gdx.graphics.newCursor(pixmap, 3, 3));
            pixmap.dispose();
        }
    }

    /**
     * Одержати пару курсорів. Назва курсора - назва файлу ~.png в теці textures/cursors, без розширення
     * @param available щось, коли доступне
     * @param not_available коли недоступне
     * @return пара курсорів
     */
    public CursorPair getCursorPair(String available, String not_available) {
        return new CursorPair(cursorsMap.get(available), cursorsMap.get(not_available));
    }

    /**
     * @param name назва курсора в теці textures/cursors, без розширення
     * @return курсор
     */
    public Cursor get(String name) {
        return cursorsMap.get(name);
    }
}
