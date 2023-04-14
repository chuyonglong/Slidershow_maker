package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlFresnelFilter extends GlFilter {

    private static final String FRAGMENT_SHADER = "\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float texelWidth;\nuniform float texelHeight;\nuniform float time;\nuniform float paramIntensity;\nuniform float paramSpeed;\nuniform float paramZoom;\nconst vec2 ringCenter = vec2(0.5);\nvoid main() {\n    float rings = 1.0 + (paramIntensity / 100.0)*6.0;\n    float speed = 0.05 + (paramSpeed / 100.0)*0.3;\n    float zoom = 0.2 + (paramZoom / 100.0)*0.6;\n    float aspect = texelHeight / texelWidth;\n    vec2 pos = vec2(vTextureCoord.x * aspect, vTextureCoord.y);\n    float r = distance(pos, vec2(ringCenter.x * aspect, ringCenter.y)) - speed*time;\n    r = fract(r*rings) / zoom;\n    vec2 uv = 0.5*(r*(-1.0 + 2.0 * vTextureCoord) + 1.0);\n    gl_FragColor = texture2D(sTexture, uv);\n}\n";
    private float paramIntensity = 30.0f;
    private float paramSpeed = 20.0f;
    private float paramZoom = 50.0f;
    private float texelHeight;
    private float texelWidth;

    public GlFresnelFilter() {
        super("attribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = aPosition;\nvTextureCoord = aTextureCoord.xy;\n}\n", FRAGMENT_SHADER);
        filterName = "Fresnel";
    }

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("texelWidth"), this.texelWidth);
        FilterUtilsKt.setFloat(getHandle("texelHeight"), this.texelHeight);
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
        FilterUtilsKt.setFloat(getHandle("paramSpeed"), this.paramSpeed);
        FilterUtilsKt.setFloat(getHandle("paramZoom"), this.paramZoom);
    }

    public void setFrameSize(int i, int i2) {
        this.texelWidth = 1.0f / ((float) i);
        this.texelHeight = 1.0f / ((float) i2);
    }
}
