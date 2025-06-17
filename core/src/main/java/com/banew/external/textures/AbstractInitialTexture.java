package com.banew.external.textures;

import com.banew.utilites.TextureExtractor;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = InitialClassicTexture.class, name = "classic"),
    @JsonSubTypes.Type(value = InitialDeepTexture.class, name = "deep")
})
@Data
public abstract class AbstractInitialTexture {
    public abstract TextureExtractor extractTextureExtractor();
    private float widthScale = 1;
    private float heightScale = 1;
}
