package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlPosterizationFilter extends GlFilter {

    public static final String FRAGMENT_SHADER = "\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float paramColor;\nuniform float paramIntensity;\nvoid main() {\n    float numColors = 4.0 + (paramIntensity / 100.0)*20.0;\n    float gamma = 0.3 + (paramColor / 100.0)*0.6;\n    vec3 tc = texture2D(sTexture, vTextureCoord).rgb;\n    tc = pow(tc, vec3(gamma, gamma, gamma));\n    tc = floor(tc * numColors) / numColors;\n    tc = pow(tc, vec3(1.0/gamma));\n    gl_FragColor = vec4(tc.rgb, 1.0);\n}\n";
    private float paramColor = 50.0f;
    private float paramIntensity = 30.0f;

    public GlPosterizationFilter() {
        super("attribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = aPosition;\nvTextureCoord = aTextureCoord.xy;\n}\n", FRAGMENT_SHADER);
        filterName = "Posterization";}

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
        FilterUtilsKt.setFloat(getHandle("paramColor"), this.paramColor);
    }
}
