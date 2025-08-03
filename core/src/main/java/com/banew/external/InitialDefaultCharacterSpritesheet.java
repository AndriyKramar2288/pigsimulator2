package com.banew.external;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.banew.other.records.MovingEntityTexturesPerDirectionPack;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.banew.other.records.MovingEntityTexturesPerDirectionPack.fromOneSubtexture;

@Data
public class InitialDefaultCharacterSpritesheet {
    String region;
    int width, height;
    String sides; // Синтаксис: <Верх.рядок>:<Верх.х-скейл>:<Верх.y-скейл>|Ліво|Низ|Право

    public Map<String, MovingEntityTexturesPerDirectionPack> extractTextures(TextureAtlas atlas) {
        List<String> indexes = List.of("up","left","down","right");

        Map<String, String> sidesMap = new HashMap<>();
        String[] strings = sides.split("/");
        for (int i = 0; i < indexes.size(); i++) {
            sidesMap.put(indexes.get(i), strings[i]);
        }

        return sidesMap.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                String[] each = entry.getValue().split(":");

                int start = (Integer.parseInt(each[0]) - 1) * width + 1;
                int end = start + width;
                int[] range = IntStream.range(start + 1, end).toArray();

                return fromOneSubtexture(
                    region, width, height, atlas,
                    start,
                    new Vector2(
                        Float.parseFloat(each[1]),
                        Float.parseFloat(each[2])
                    ),
                    range
                );
            }));
    }
}
