package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlCrossStitchingFilter extends GlFilter {

    public static final String FRAGMENT_SHADER = "\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float texelWidth;\nuniform float texelHeight;\nconst int invert = 1;\nuniform float paramIntensity;\nvoid main() {\n    float intensity = 3.5 - (paramIntensity / 100.0)*2.75;\n    vec2 uv = vTextureCoord;\n    float stitching_size = 9.0 * sqrt(pow(1.0/texelWidth/100.0, 2.0) + pow(1.0/texelHeight/100.0, 2.0)) / 16.0;\n    stitching_size = max(3.0, floor(intensity * stitching_size));\n    vec2 cPos = vec2(uv.x / texelWidth, uv.y / texelHeight);\n    vec2 tlPos = floor(cPos / vec2(stitching_size, stitching_size)) * stitching_size;\n    int remX = int(mod(cPos.x, stitching_size));\n    int remY = int(mod(cPos.y, stitching_size));\n    if (remX == 0 && remY == 0) tlPos = cPos;\n    vec2 blPos = tlPos;\n    blPos.y += (stitching_size - 1.0);\n    vec4 c;\n    if ((remX == remY) || (((int(cPos.x) - int(blPos.x)) == (int(blPos.y) - int(cPos.y))))) {\n\t     c = invert == 1 ? vec4(0.2, 0.15, 0.05, 1.0) : 1.4*texture2D(sTexture, tlPos * vec2(texelWidth, texelHeight));\n    } else {\n\t     c = invert == 1 ? 1.4*texture2D(sTexture, tlPos * vec2(texelWidth, texelHeight)) : vec4(0.0, 0.0, 0.0, 1.0);\n    }\n    gl_FragColor = c;\n}";
    private float paramIntensity = 75.0f;
    private float texelHeight;
    private float texelWidth;

    public GlCrossStitchingFilter() {
        super("attribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = aPosition;\nvTextureCoord = aTextureCoord.xy;\n}\n", FRAGMENT_SHADER);
        filterName = "Cross Stitching";
    }

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("texelWidth"), this.texelWidth);
        FilterUtilsKt.setFloat(getHandle("texelHeight"), this.texelHeight);
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
    }

    public void setFrameSize(int i, int i2) {
        this.texelWidth = 1.0f / ((float) i);
        this.texelHeight = 1.0f / ((float) i2);
    }
}
