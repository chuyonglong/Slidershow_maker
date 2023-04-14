package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.more_filter.FilterTimable;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlWavyFilter extends FilterTimable {

    private static final String FRAGMENT_SHADER = "\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float time;\nuniform float paramIntensity;\nuniform float paramSize;\nuniform float paramSpeed;\nvoid main() {\n    float intensity = 0.005 + (paramIntensity / 100.0)*0.24;\n    float size = 1.0 + (paramSize / 100.0)*30.0;\n    float speed = 3.0 + (paramSpeed / 100.0)*27.0;\n    vec2 uv = vTextureCoord + vec2(sin(speed*time + vTextureCoord.y * size) * intensity, 0.0);\n    gl_FragColor = texture2D(sTexture, uv);\n}\n";
    private float paramIntensity = 20.0f;
    private float paramSize = 20.0f;
    private float paramSpeed = 25.0f;

    public GlWavyFilter() {
        super(FRAGMENT_SHADER);filterName = "Wavy";
    }

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
        FilterUtilsKt.setFloat(getHandle("paramSize"), this.paramSize);
        FilterUtilsKt.setFloat(getHandle("paramSpeed"), this.paramSpeed);
    }
}
