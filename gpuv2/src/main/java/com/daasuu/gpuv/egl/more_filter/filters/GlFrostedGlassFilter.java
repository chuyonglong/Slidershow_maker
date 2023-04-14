package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlFrostedGlassFilter extends GlFilter {

    private static final String FRAGMENT_SHADER = "\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float paramIntensity;\nuniform float paramSmoothness;\nconst float rnd_factor = 0.05;\nconst vec2 v1 = vec2(92.,80.);\nconst vec2 v2 = vec2(41.,62.);\nfloat rnd_scale;\nfloat rand(vec2 co) {\n  return fract(sin(dot(co ,v1)) + cos(dot(co ,v2)) * rnd_scale); }\nvoid main() {\n    rnd_scale = 0.5 + (paramSmoothness / 100.0)*9.2;\n    float intensity = paramIntensity / 100.0;\n    vec4 tc = texture2D(sTexture, vTextureCoord);\n    vec2 rnd = vec2(rand(vTextureCoord.xy), rand(vTextureCoord.yx));\n    gl_FragColor = mix(tc, texture2D(sTexture, vTextureCoord+rnd*rnd_factor), intensity);\n}";
    private float paramIntensity = 80.0f;
    private float paramSmoothness = 50.0f;


    public GlFrostedGlassFilter() {
        super("attribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = aPosition;\nvTextureCoord = aTextureCoord.xy;\n}\n", FRAGMENT_SHADER);
        filterName = "Frosted Glass";}

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
        FilterUtilsKt.setFloat(getHandle("paramSmoothness"), this.paramSmoothness);
    }
}
