package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlOrangeTealFilter extends GlFilter {

    private static final String FRAGMENT_SHADER = "\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float paramIntensity;\nvoid main() {\n    float intensity = 0.25 + (paramIntensity / 100.0)*1.25;\n    vec3 orig = texture2D(sTexture, vTextureCoord).rgb;\n    vec3 col = orig * orig * 1.4;\n    float bri = dot(col.rgb, vec3(0.2125, 0.7154, 0.0721));\n    float v = smoothstep(.0, .7, bri);\n    col = mix(vec3(0., 1., 1.2) * bri, col, v);\n    v = smoothstep(.2, 1.1, bri);\n    col = mix(col, min(vec3(1.0, .55, 0.) * col * 1.2, 1.0), v);\n    gl_FragColor = vec4(clamp(mix(orig, col, intensity), 0.0, 1.0), 1.0);\n}\n";
    private float paramIntensity = 60.0f;

    public GlOrangeTealFilter() {
        super("attribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = aPosition;\nvTextureCoord = aTextureCoord.xy;\n}\n", FRAGMENT_SHADER);
        filterName = "Orange Teal";}

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
    }
}
