package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.filter.GlThreex3TextureSamplingFilter;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlConvolutionFilter extends GlThreex3TextureSamplingFilter {
    private static final String THREE_X_THREE_TEXTURE_SAMPLING_FRAGMENT_SHADER = "\nprecision highp float;\n\nuniform sampler2D sTexture;\n\nuniform mediump mat3 convolutionMatrix;\n\nvarying vec2 textureCoordinate;\nvarying vec2 leftTextureCoordinate;\nvarying vec2 rightTextureCoordinate;\n\nvarying vec2 topTextureCoordinate;\nvarying vec2 topLeftTextureCoordinate;\nvarying vec2 topRightTextureCoordinate;\n\nvarying vec2 bottomTextureCoordinate;\nvarying vec2 bottomLeftTextureCoordinate;\nvarying vec2 bottomRightTextureCoordinate;\n\nvoid main()\n{\n    mediump vec4 bottomColor = texture2D(sTexture, bottomTextureCoordinate);\n    mediump vec4 bottomLeftColor = texture2D(sTexture, bottomLeftTextureCoordinate);\n    mediump vec4 bottomRightColor = texture2D(sTexture, bottomRightTextureCoordinate);\n    mediump vec4 centerColor = texture2D(sTexture, textureCoordinate);\n    mediump vec4 leftColor = texture2D(sTexture, leftTextureCoordinate);\n    mediump vec4 rightColor = texture2D(sTexture, rightTextureCoordinate);\n    mediump vec4 topColor = texture2D(sTexture, topTextureCoordinate);\n    mediump vec4 topRightColor = texture2D(sTexture, topRightTextureCoordinate);\n    mediump vec4 topLeftColor = texture2D(sTexture, topLeftTextureCoordinate);\n\n    mediump vec4 resultColor = topLeftColor * convolutionMatrix[0][0] + topColor * convolutionMatrix[0][1] + topRightColor * convolutionMatrix[0][2];\n    resultColor += leftColor * convolutionMatrix[1][0] + centerColor * convolutionMatrix[1][1] + rightColor * convolutionMatrix[1][2];\n    resultColor += bottomLeftColor * convolutionMatrix[2][0] + bottomColor * convolutionMatrix[2][1] + bottomRightColor * convolutionMatrix[2][2];\n\n    gl_FragColor = resultColor;\n}";
    private float[] convolutionKernel = {-1.0f, 0.0f, 1.0f, -2.0f, 0.0f, 2.0f, -1.0f, 0.0f, 1.0f};

    public GlConvolutionFilter() {
        super(THREE_X_THREE_TEXTURE_SAMPLING_FRAGMENT_SHADER);
        filterName = "Convolution";
    }

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setUniformMatrix3f(getHandle("convolutionMatrix"), this.convolutionKernel);
    }
}
