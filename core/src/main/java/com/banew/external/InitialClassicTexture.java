package com.banew.external;

import com.banew.utilites.TextureExtractor;
import com.banew.utilites.TextureExtractorClassic;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class InitialClassicTexture extends AbstractInitialTexture {
    private String region;

    @Override
    public TextureExtractor extractTextureExtractor() {
        return new TextureExtractorClassic(region);
    }
}
