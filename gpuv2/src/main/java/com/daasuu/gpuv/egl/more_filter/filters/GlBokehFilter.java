package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlBokehFilter extends GlFilter {

    private static final String FRAGMENT_SHADER = "\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float texelWidth;\nuniform float texelHeight;\nconst float PI = 3.14159535;\nconst float ITER_ANGLE = 2.3999632;\nconst float ITERS = 16.0 * ITER_ANGLE;\nuniform float paramIntensity;\nuniform float paramSize;\nvoid main() {\n    float intensity = (paramIntensity / 100.0)*0.01;\n    float size = 0.001 + (paramSize / 100.0)*0.01;\n    vec2 uv = vTextureCoord;\n    vec2 R = 1.0 / vec2(texelWidth, texelHeight);\n    float radius = intensity*distance(uv, vec2(0.5))*length(R);\n    float bokehMult = 150.0;\n    vec3 to = vec3(0.0);\n    vec3 d = vec3(0.0);\n    vec2 pxl = size * radius * vec2(texelWidth/texelHeight, 1.0);\n    float t = 1.0;\n    for (float angle = 0.0; angle < ITERS; angle += ITER_ANGLE) {\n        t = t + (1.0 / t);\n        vec2 offset = (t - 1.0) * vec2(cos(angle), sin(angle));\n        vec3 tc = texture2D(sTexture, uv + pxl * offset).rgb;\n        vec3 bokeh = vec3(4.0) + pow(tc, vec3(8.0)) * bokehMult;\n        to += tc * bokeh;\n        d += bokeh;\n    }\n    gl_FragColor = vec4(to/d, 1.0);\n}\n";
    private float paramIntensity = 50.0f;
    private float paramSize = 50.0f;
    private float texelHeight;
    private float texelWidth;

    public GlBokehFilter() {
        super("attribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = aPosition;\nvTextureCoord = aTextureCoord.xy;\n}\n", FRAGMENT_SHADER);

        filterName = "Bokeh";
    }

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("texelWidth"), this.texelWidth);
        FilterUtilsKt.setFloat(getHandle("texelHeight"), this.texelHeight);
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
        FilterUtilsKt.setFloat(getHandle("paramSize"), this.paramSize);
    }

    public void setFrameSize(int i, int i2) {
        this.texelWidth = 1.0f / ((float) i);
        this.texelHeight = 1.0f / ((float) i2);
    }

}
