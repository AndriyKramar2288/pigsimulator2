package com.banew.containers.game;

import com.banew.items.AbstractItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class ItemContainer {
    private int size;
    private final Map<Integer, AbstractItem> itemMap = new HashMap<>();

    public ItemContainer(int size) {
        this.size = size;
    }

    public AbstractItem get(int index) {
        return itemMap.get(index);
    }

    public AbstractItem put(int index, AbstractItem item) {
        if (size <= index) {
            size = index + 1;
        }
        return itemMap.put(index, item);
    }

    public AbstractItem remove(int index) {
        if (index >= 0 && index < size) {
            return itemMap.remove(index);
        }
        throw new RuntimeException("Індекс " + index + " за межами розміру контейнера (" + size + ")!");
    }

    public List<AbstractItem> getList() {
        return IntStream.range(0, size)
            .mapToObj(itemMap::get)
            .toList();
    }

    public int size() {
        return size;
    }
}
