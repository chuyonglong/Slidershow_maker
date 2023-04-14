package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlBlueFilter extends GlFilter {

    public static final String FRAGMENT_SHADER = "\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\n#define PI 3.1415926535\nuniform float paramIntensity;\nuniform float paramSmoothness;\nuniform float paramColor;\nuniform float paramQuality;\nvec4 sampleNeighbor(float diffX, float diffY) { return texture2D(sTexture, vTextureCoord + vec2(diffX, diffY)); }\nvoid main() {\n    float intensity = (paramIntensity / 100.0)*2.0;\n    float d = 0.009 - (paramSmoothness / 100.0)*0.008;\n    float edges = 4.0 - (paramQuality / 100.0)*3.5;\n    float color = 0.5 + (paramColor / 100.0)*7.5;\n    vec3 ts = sampleNeighbor(0.0, 0.0).rgb;\n    vec3 tsAvg = (sampleNeighbor(-d, -d) + sampleNeighbor(d,-d) + sampleNeighbor(-d, d) + sampleNeighbor(d, d)).rgb / 4.0;\n    vec3 to = pow(10.0, edges)*(ts-tsAvg)*(ts-tsAvg)*(ts-tsAvg);\n    float w = (tsAvg.x-0.3)*color;\n    vec3 col0 = vec3(0.0);\n    vec3 col1 = vec3(0.25, 0.5, 1.0);\n    vec3 col2 = vec3(1.0, 0.85, 0.75);\n    vec3 col3 = vec3(0.0);\n    vec3 col = mix(col0, col1, w);\n    col = w > 1.0 ? mix(col1, col2, w-1.0) : col;\n    col = w > 2.0 ? mix(col2, col3, w-2.0) : col;\n    col = clamp(col ,0.0, 1.0);\n    to = clamp(col*(1.0-clamp(to, 0.0, 1.0)), 0.0, 1.0);\n    to = (to == col0) ? col3 : to;\n    gl_FragColor = vec4(mix(ts, to, intensity), 1.0);\n}\n";
    private float paramColor = 40.0f;
    private float paramIntensity = 50.0f;
    private float paramQuality = 70.0f;
    private float paramSmoothness = 70.0f;

    public GlBlueFilter() {
        super("attribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = aPosition;\nvTextureCoord = aTextureCoord.xy;\n}\n", FRAGMENT_SHADER);
        filterName = "Blue";
    }

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
        FilterUtilsKt.setFloat(getHandle("paramSmoothness"), this.paramSmoothness);
        FilterUtilsKt.setFloat(getHandle("paramColor"), this.paramColor);
        FilterUtilsKt.setFloat(getHandle("paramQuality"), this.paramQuality);
    }

}
