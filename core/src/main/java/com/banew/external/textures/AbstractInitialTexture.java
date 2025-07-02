package com.banew.external.textures;

import com.banew.utilites.TextureExtractor;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = InitialClassicTexture.class, name = "classic"),
    @JsonSubTypes.Type(value = InitialDeepTexture.class, name = "deep")
})
@Data
public abstract class AbstractInitialTexture {
    public abstract TextureExtractor extractTextureExtractor();
    protected float widthScale = 1;
    protected float heightScale = 1;
}
