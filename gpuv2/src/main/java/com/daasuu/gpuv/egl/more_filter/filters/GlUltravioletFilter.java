package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlUltravioletFilter extends GlFilter {

    private static final String FRAGMENT_SHADER = "\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\n#define PI 3.14159265359\nuniform float paramIntensity;\nvoid main() {\n    float intensity = 0.0 + (paramIntensity / 100.0)*2.0;\n    vec3 ts = texture2D(sTexture, vTextureCoord).rgb;\n    float lum = 2.0*PI*dot(ts, vec3(1.0/3.0));\n    float rg = 2.0*atan(ts.r, ts.g)/PI;\n    vec3 to = vec3(rg*(0.5+0.5*cos(lum)), 0.1*rg, rg*(0.5+0.5*sin(lum)));\n    gl_FragColor = vec4(mix(ts, to, intensity), 1.0);\n}\n";
    private float paramIntensity = 50.0f;


    public GlUltravioletFilter() {
        super("attribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = aPosition;\nvTextureCoord = aTextureCoord.xy;\n}\n", FRAGMENT_SHADER);
        filterName = "Ultraviolet";}

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
    }
}
