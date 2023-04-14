package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.more_filter.FilterTimable;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlNightVisionFilter extends FilterTimable {

    private static final String FRAGMENT_SHADER = "\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float time;\nuniform float texelWidth;\nuniform float texelHeight;\nuniform float paramIntensity;\nuniform float paramSmoothness;\nuniform float paramSize;\nfloat hash(in float n) { return fract(sin(n)*43758.5453123); }\nvoid main() {\n    float intensity = 1.0 + (paramIntensity / 100.0)*3.0;\n    float quality = (1.0 - paramSmoothness / 100.0)*1.5;\n    float size = -0.4 + (paramSize / 100.0)*1.1;\n    vec2 n = (2.0*vTextureCoord - 1.0) * vec2(texelHeight / texelWidth, 1.0);\n    vec3 c = texture2D(sTexture, vTextureCoord).rgb;\n    c += quality * sin(hash(time)) * 0.01;\n    c += quality * hash((hash(n.x) + n.y) * (time + 2.0)) * 0.5;\n    c *= mix(1.5*smoothstep(length(n * n * n * vec2(0.075, 0.4)), 1.0, 0.4), 1.0, size);\n    c = dot(c, vec3(0.2126, 0.7152, 0.0722)) * vec3(0.2, intensity - quality * hash(time) * 0.1, 0.4);\n\t gl_FragColor = vec4(c, 1.0);\n}\n";
    private float paramIntensity = 30.0f;
    private float paramSize = 40.0f;
    private float paramSmoothness = 65.0f;
    private float texelHeight;
    private float texelWidth;

    public GlNightVisionFilter() {
        super(FRAGMENT_SHADER);filterName = "Night Vision";
    }

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("texelWidth"), this.texelWidth);
        FilterUtilsKt.setFloat(getHandle("texelHeight"), this.texelHeight);
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
        FilterUtilsKt.setFloat(getHandle("paramSize"), this.paramSize);
        FilterUtilsKt.setFloat(getHandle("paramSmoothness"), this.paramSmoothness);
    }

    public void setFrameSize(int i, int i2) {
        this.texelWidth = 1.0f / ((float) i);
        this.texelHeight = 1.0f / ((float) i2);
    }
}
