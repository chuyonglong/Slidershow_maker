package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlSolarizationFilter extends GlFilter {

    private static final String FRAGMENT_SHADER = "\nprecision highp float;\n#define THRESHOLD vec3(1.0, 0.92, 0.1)\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float paramIntensity;\nvec3 texfilter(vec3 val) {\n    if (val.x < THRESHOLD.x) val.x = 1. - val.x;\n    if (val.y < THRESHOLD.y) val.y = 1. - val.y;\n    if (val.z < THRESHOLD.z) val.z = 1. - val.z;\n\treturn val; }\nvoid main() {\n    float intensity = (paramIntensity / 100.0)*2.0;\n    vec2 uv = vTextureCoord;\n    vec4 tc = texture2D(sTexture, uv);\n    float l = 1.0;\n    vec3 cf = texfilter(tc.rgb);\n\t gl_FragColor = vec4(mix(tc.rgb, cf*l, intensity), 1.0);\n}";
    private float paramIntensity = 50.0f;

    public GlSolarizationFilter() {
        super("attribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = aPosition;\nvTextureCoord = aTextureCoord.xy;\n}\n", FRAGMENT_SHADER);
        filterName = "Solarization";}

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
    }
}
