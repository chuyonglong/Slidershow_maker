package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.more_filter.FilterTimable;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlBwStrobeFilter extends FilterTimable {

    private static final String FRAGMENT_SHADER = "\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\n#define PI 3.14159265359\nuniform float time;\nuniform float paramIntensity;\nuniform float paramSpeed;\nvoid main() {\n    float intensity = paramIntensity / 100.0;\n    float speed = 0.1 + (paramSpeed / 100.0)*1.25;\n    vec3 ts = texture2D(sTexture, vTextureCoord).rgb;\n    float extraLum = 1.25 - 1.5 * intensity * sin(speed*time*6.4) * cos(speed*time*10.0);\n    float lum = dot(ts, vec3(0.3, 0.59, 0.11)) * extraLum;\n    gl_FragColor = vec4(vec3(lum), 1.0);\n}\n";
    private float paramIntensity = 75.0f;
    private float paramSpeed = 50.0f;

    public GlBwStrobeFilter() {
        super(FRAGMENT_SHADER);
        filterName = "Strobe";
    }

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
        FilterUtilsKt.setFloat(getHandle("paramSpeed"), this.paramSpeed);
    }

}
