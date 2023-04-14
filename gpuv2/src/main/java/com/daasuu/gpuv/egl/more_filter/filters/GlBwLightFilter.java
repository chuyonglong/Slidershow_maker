package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.more_filter.FilterTimable;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlBwLightFilter extends FilterTimable {

    private static final String FRAGMENT_SHADER = "\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\n#define PI 3.14159265359\nuniform float time;\nconst float period = 12.0;\nuniform float paramIntensity;\nuniform float paramSpeed;\nvoid main() {\n    float intensity = 0.0 + (paramIntensity / 100.0)*1.0;\n    float speed = 0.5 + (paramSpeed / 100.0)*1.0;\n    vec3 ts = texture2D(sTexture, vTextureCoord).rgb;\n    float lum = 2.0*PI*dot(ts, vec3(1.0/3.0));\n    float t = time*speed;\n    vec3 to = vec3(0.5-0.5*cos(t+2.0*PI*mod(t, period)*lum));\n    gl_FragColor = vec4(mix(ts, to, intensity),1.0);\n}\n";
    private float paramIntensity = 75.0f;
    private float paramSpeed = 50.0f;

    public GlBwLightFilter() {
        super(FRAGMENT_SHADER);
        filterName = "Light";
    }

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
        FilterUtilsKt.setFloat(getHandle("paramSpeed"), this.paramSpeed);
    }

}
