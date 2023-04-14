package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlEdgesFilter extends GlFilter {

    private static final String FRAGMENT_SHADER = "\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float texelWidth;\nuniform float texelHeight;\nuniform float paramIntensity;\nvoid main() {\n    float intensity = 2.0 + (paramIntensity / 100.0)*12.0;\n    float sw = 2. * texelWidth;\n    float sh = 2. * texelHeight;\n    vec4 grad1 = texture2D(sTexture, vTextureCoord + vec2(-sw,0.)) - texture2D(sTexture, vTextureCoord + vec2(sw,0.));\n    vec4 grad2 = texture2D(sTexture, vTextureCoord + vec2(0.,-sh)) - texture2D(sTexture, vTextureCoord + vec2(0.,sh));\n    gl_FragColor = vec4((grad1 + grad2).rgb * intensity, 1.);\n}\n";
    private float paramIntensity = 25.0f;
    private float texelHeight;
    private float texelWidth;


    public GlEdgesFilter() {
        super("attribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = aPosition;\nvTextureCoord = aTextureCoord.xy;\n}\n", FRAGMENT_SHADER);
        filterName = "Edges";}

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
    }

    public void setFrameSize(int i, int i2) {
        this.texelWidth = 1.0f / ((float) i);
        this.texelHeight = 1.0f / ((float) i2);
    }
}
