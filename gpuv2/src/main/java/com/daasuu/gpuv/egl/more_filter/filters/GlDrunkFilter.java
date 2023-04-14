package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.more_filter.FilterTimable;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlDrunkFilter extends FilterTimable {

    public static final String DRUNK_FRAGMENT_SHADER = "\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float time;\nconst float samples = 16.0;\nconst float minBlur = 0.15;\nconst float maxBlur = 0.4;\nuniform float paramIntensity;\nuniform float paramSpeed;\n\nvoid main() {\n    vec2 uv = vTextureCoord;\n    vec4 result = vec4(0);\n    float intensityMult = (paramIntensity + 50.0) / 100.0;\n    float speed = 1.0 + (paramSpeed / 100.0)*4.0;\n\tfloat timeQ = mix(minBlur, maxBlur, 0.5*(sin(time*speed)+1.0)) * intensityMult;\n\tfor (float i=0.0; i<samples; i+=1.0) {\n\t    float q = i/samples;\n\t    result += texture2D(sTexture, uv + (vec2(0.5)-uv)*q*timeQ) / samples;\n\t}\n\tgl_FragColor = result;\n}\n";
    private float paramIntensity = 50.0f;
    private float paramSpeed = 50.0f;

    public GlDrunkFilter() {
        super(DRUNK_FRAGMENT_SHADER);
        filterName = "Drunk";
    }

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
        FilterUtilsKt.setFloat(getHandle("paramSpeed"), this.paramSpeed);
    }
}
