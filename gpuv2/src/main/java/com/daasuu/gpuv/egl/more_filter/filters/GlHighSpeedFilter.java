package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlHighSpeedFilter extends GlFilter {

    private static final String FRAGMENT_SHADER = "\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float texelWidth;\nuniform float texelHeight;\nuniform float paramIntensity;\nvoid main() {\n    float intensity = -0.01 - (paramIntensity / 100.0)*0.80;\n    vec2 uv = vTextureCoord;\n    vec2 center = vec2(0.5, 0.5);\n    vec2 dist = uv - center;\n    float power = ( 3.14159265 /  length(center) ) * intensity;\n    float bind = power > 0.0 ? length(center) : (texelHeight < texelWidth ? center.x : center.y);\n    uv = center + normalize(dist) * atan(length(dist) * -power * 8.0) * bind / atan(-power * bind * 8.0);\n    gl_FragColor = vec4(texture2D(sTexture, uv).rgb, 1.0);\n}\n";
    private float paramIntensity = 40.0f;
    private float texelHeight;
    private float texelWidth;


    public GlHighSpeedFilter() {
        super("attribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = aPosition;\nvTextureCoord = aTextureCoord.xy;\n}\n", FRAGMENT_SHADER);
        filterName = "High Speed";}

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
