package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlCrosshatchingFilter extends GlFilter {

    public static final String FRAGMENT_SHADER = "precision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float texelWidth;\nuniform float texelHeight;\nuniform float paramIntensity;\nconst float lum_threshold_1 = 1.0;\nconst float lum_threshold_2 = 0.7;\nconst float lum_threshold_3 = 0.5;\nconst float lum_threshold_4 = 0.3;\nvoid main() {\n    float absHatchOffset = 9.0 - (paramIntensity / 100.0)*16.0;\n    vec3 tc = vec3(1.0, 1.0, 1.0);\n    vec2 absCoords = vec2(vTextureCoord.x/texelWidth, vTextureCoord.y/texelHeight);\n    float absHatchMod = 2.0*absHatchOffset;\n    float lum = length(texture2D(sTexture, vTextureCoord).rgb);\n    if (lum < lum_threshold_1) {\n        if (int(mod(floor(absCoords.x + absCoords.y), absHatchMod)) == 0) tc = vec3(0.0, 0.0, 0.0);\n    }\n    if (lum < lum_threshold_2) {\n        if (int(mod(floor(absCoords.x - absCoords.y), absHatchMod)) == 0) tc = vec3(0.0, 0.0, 0.0);\n    }\n    if (lum < lum_threshold_3) {\n        if (int(mod(floor(absCoords.x + absCoords.y - absHatchOffset), absHatchMod)) == 0) tc = vec3(0.0, 0.0, 0.0);\n    }\n    if (lum < lum_threshold_4) {\n        if (int(mod(floor(absCoords.x - absCoords.y - absHatchOffset), absHatchMod)) == 0) tc = vec3(0.0, 0.0, 0.0);\n    }\n    gl_FragColor = vec4(tc.rgb, 1.0);\n}\n";
    private float paramIntensity = 70.0f;
    private float texelHeight;
    private float texelWidth;


    public GlCrosshatchingFilter() {
        super("attribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = aPosition;\nvTextureCoord = aTextureCoord.xy;\n}\n", FRAGMENT_SHADER);
        filterName = "Cross Hatching";
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
