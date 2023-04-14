package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlSeventyFilter extends GlFilter {

    private static final String FRAGMENT_SHADER = "\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float paramIntensity;\nvoid main() {\n   float intensity = (paramIntensity / 100.0)*2.0;\n    vec4 tc = texture2D(sTexture, vTextureCoord);\n    float grayscale = 0.2125 * tc.r + 0.7154 * tc.g + 0.0721 * tc.b;\n    vec4 to = vec4(tc.r*abs(cos(grayscale)), tc.g*abs(sin(grayscale)), tc.b*abs(atan(grayscale) * sin(grayscale)), tc.a);\n    to.rgb *= 1.5;\n    gl_FragColor = mix(tc, to, intensity);\n}";
    private float paramIntensity = 50.0f;

    public GlSeventyFilter() {
        super("attribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = aPosition;\nvTextureCoord = aTextureCoord.xy;\n}\n", FRAGMENT_SHADER);
        filterName = "Seventy";}

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
    }
}
