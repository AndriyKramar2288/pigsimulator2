package com.banew.external.textures;

import com.banew.utilites.TextureExtractor;
import com.banew.utilites.TextureExtractorClassic;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InitialClassicTexture extends AbstractInitialTexture {
    private String region;

    @Override
    public TextureExtractor extractTextureExtractor() {
        return new TextureExtractorClassic(region, getWidthScale(), getHeightScale());
    }
}
