package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.more_filter.FilterTimable;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlSplitColorFilter extends FilterTimable {

    private static final String FRAGMENT_SHADER = "\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float time;\nuniform float paramIntensity;\nuniform float paramSpeed;\nvoid main() {\n    float intensity = (paramIntensity / 100.0)*8.0;\n    float speed = 0.25 + (paramSpeed / 100.0)*4.0;\n    vec2 deltaR = intensity*0.01*vec2(sin(speed*time)+sin(speed*0.3*time)+sin(speed*0.06*time)*0.25+sin(speed*0.9*time), 0);\n    vec2 deltaB = intensity*0.01*vec2(sin(speed*1.2*time)+sin(speed*0.15*time)+sin(speed*0.02*time)*0.3+sin(speed*0.8*time), 0);\n    vec4 tc = texture2D(sTexture, vTextureCoord);\n    tc.r = texture2D(sTexture, vTextureCoord + deltaR).r;\n    tc.b = texture2D(sTexture, vTextureCoord - deltaB).b;\n    gl_FragColor = vec4(tc.rgb, 1.0);\n}\n";
    private float paramIntensity = 50.0f;
    private float paramSpeed = 20.0f;


    public GlSplitColorFilter() {
        super(FRAGMENT_SHADER);filterName = "Split";
    }

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
        FilterUtilsKt.setFloat(getHandle("paramSpeed"), this.paramSpeed);
    }
}
