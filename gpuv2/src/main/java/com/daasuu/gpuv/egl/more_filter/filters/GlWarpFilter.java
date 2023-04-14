package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlWarpFilter extends GlFilter {

    private static final String FRAGMENT_SHADER = "\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float paramIntensity;\nvoid main() {\n    float intensity = 8.0 - (paramIntensity / 100.0)*6.5;\n    vec2 uv = vTextureCoord;\n    vec2 v = vec2(0.5) - uv;\n    float d = length(v);\n    v /= d;\n    uv += v * max(0.0, pow(d, intensity));\n    vec3 to = texture2D(sTexture, vec2(0) + vec2(1)*uv).rgb;\n    vec2 valid = step(vec2(0.0), uv) * step(uv, vec2(1.0));\n    to *= valid.x*valid.y;\n    gl_FragColor = vec4(to, 1.0);\n}\n";
    private float paramIntensity = 70.0f;


    public GlWarpFilter() {
        super("attribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = aPosition;\nvTextureCoord = aTextureCoord.xy;\n}\n", FRAGMENT_SHADER);
        filterName = "Warp";}

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
    }
}
