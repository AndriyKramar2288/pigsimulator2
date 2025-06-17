package com.banew.external.textures;

import com.banew.other.records.MatrixVector;
import com.banew.utilites.TextureExtractor;
import com.banew.utilites.TextureExtractorDeep;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class InitialDeepTexture extends AbstractInitialTexture {
    private String region;
    private int sizeX;
    private int sizeY;
    private int cordX;
    private int cordY;

    @Override
    public TextureExtractor extractTextureExtractor() {
        return new TextureExtractorDeep(
            region,
            new MatrixVector(sizeX, sizeY),
            new MatrixVector(cordX, cordY),
            getWidthScale(),
            getHeightScale()
        );
    }
}
