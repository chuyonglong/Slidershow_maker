package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.more_filter.FilterTimable;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlDispersionFilter extends FilterTimable {

    private static final String FRAGMENT_SHADER = "\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float time;\nuniform float paramIntensity;\nuniform float paramSize;\nuniform float paramSpeed;\nvoid main() {\n    float intensity = 0.001 + (paramIntensity / 100.0)*0.12;\n    float size = 2.0 + (paramSize / 100.0)*36.0;\n    float speed = 1.0 + (paramSpeed / 100.0)*12.0;\n    vec4 m = vec4(0);\n    vec4 to = vec4(0);\n    for(float i=0.0 ; i<1.0 ; i+=.08) {\n        vec4 c = vec4(i, 4.3*pow((1.0-i)*i, 2.2), 1.0-i, 1.0);\n        m += c*c;\n        to += c*c*texture2D(sTexture, vTextureCoord+i*intensity*(pow(cos(vTextureCoord*size+speed*time), vec2(3.0))-0.15));\n    }\n    gl_FragColor = smoothstep(0.0, 1.0, to/m);\n}\n";
    private float paramIntensity = 40.0f;
    private float paramSize = 50.0f;
    private float paramSpeed = 25.0f;

    public GlDispersionFilter() {
        super(FRAGMENT_SHADER);
        filterName = "Dispersion";
    }

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
        FilterUtilsKt.setFloat(getHandle("paramSize"), this.paramSize);
        FilterUtilsKt.setFloat(getHandle("paramSpeed"), this.paramSpeed);
    }
}
