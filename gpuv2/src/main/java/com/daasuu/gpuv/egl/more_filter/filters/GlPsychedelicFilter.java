package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.more_filter.FilterTimable;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlPsychedelicFilter extends FilterTimable {

    private static final String FRAGMENT_SHADER = "\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float time;\nuniform float paramIntensity;\nuniform float paramSpeed;\nvoid main() {\n    float levelsNum = 2.0 + (paramIntensity / 100.0)*14.0;\n    float speed = 0.2 + (paramSpeed / 100.0)*1.3;\n    float phase = time*speed;\n    vec4 tc = texture2D(sTexture, vTextureCoord);\n    gl_FragColor = vec4(vec3(mod(floor(levelsNum*mod(tc + phase, 1.0)), 2.0)), 1.0);\n}\n";
    private float paramIntensity = 50.0f;
    private float paramSpeed = 30.0f;

    public GlPsychedelicFilter() {
        super(FRAGMENT_SHADER);filterName = "Psychedelic";
    }

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
        FilterUtilsKt.setFloat(getHandle("paramSpeed"), this.paramSpeed);
    }
}
