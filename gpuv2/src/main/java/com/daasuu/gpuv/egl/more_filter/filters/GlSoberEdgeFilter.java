package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.filter.GlFilterGroup;
import com.daasuu.gpuv.egl.filter.GlGrayScaleFilter;
import com.daasuu.gpuv.egl.filter.GlThreex3TextureSamplingFilter;

public class GlSoberEdgeFilter extends GlFilterGroup {

    private static final String SOBER_EDGE_DETECTION = "\nprecision mediump float;\n\nvarying vec2 textureCoordinate;\nvarying vec2 leftTextureCoordinate;\nvarying vec2 rightTextureCoordinate;\n\nvarying vec2 topTextureCoordinate;\nvarying vec2 topLeftTextureCoordinate;\nvarying vec2 topRightTextureCoordinate;\n\nvarying vec2 bottomTextureCoordinate;\nvarying vec2 bottomLeftTextureCoordinate;\nvarying vec2 bottomRightTextureCoordinate;\n\nuniform sampler2D sTexture;\n\nvoid main()\n{\n    float bottomLeftIntensity = texture2D(sTexture, bottomLeftTextureCoordinate).r;\n    float topRightIntensity = texture2D(sTexture, topRightTextureCoordinate).r;\n    float topLeftIntensity = texture2D(sTexture, topLeftTextureCoordinate).r;\n    float bottomRightIntensity = texture2D(sTexture, bottomRightTextureCoordinate).r;\n    float leftIntensity = texture2D(sTexture, leftTextureCoordinate).r;\n    float rightIntensity = texture2D(sTexture, rightTextureCoordinate).r;\n    float bottomIntensity = texture2D(sTexture, bottomTextureCoordinate).r;\n    float topIntensity = texture2D(sTexture, topTextureCoordinate).r;\n    float h = -topLeftIntensity - 2.0 * topIntensity - topRightIntensity + bottomLeftIntensity + 2.0 * bottomIntensity + bottomRightIntensity;\n    float v = -bottomLeftIntensity - 2.0 * leftIntensity - topLeftIntensity + bottomRightIntensity + 2.0 * rightIntensity + topRightIntensity;\n\n    float mag = length(vec2(h, v));\n\n    gl_FragColor = vec4(vec3(mag), 1.0);\n}\n";


    public GlSoberEdgeFilter() {
        super(new GlGrayScaleFilter(), new GlThreex3TextureSamplingFilter(SOBER_EDGE_DETECTION));
        filterName = "Sober";
    }
}
