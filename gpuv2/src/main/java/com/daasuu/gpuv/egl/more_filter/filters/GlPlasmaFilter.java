package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.more_filter.FilterTimable;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlPlasmaFilter extends FilterTimable {

    private static final String FRAGMENT_SHADER = "\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float time;\nuniform float paramIntensity;\nuniform float paramSpeed;\nuniform float paramSize;\nfloat speed;\nfloat scale;\n    float clampNorm(float f) { return clamp(f, 0.0, 1.0); }\n    vec3 rainbow(float h) {\n        h = mod(mod(h, 1.0) + 1.0, 1.0);\n        float h6 = h * 6.0;\n        float r = clampNorm(h6 - 4.0) + clampNorm(2.0 - h6);\n        float g = h6 < 2.0 ? clampNorm(h6) : clampNorm(4.0 - h6);\n        float b = h6 < 4.0 ? clampNorm(h6 - 2.0) : clampNorm(6.0 - h6);\n        return vec3(r, g, b);\n    }\n    vec3 plasma() {\n        vec4 start = vec4(563.0, 233.0, 4325.0, 312556.0) / 512.0;\n        vec4 advance = vec4(6.34, 4.98, 4.46, 5.72) / 512.0 * 18.2 * speed;\n        vec4 pos = start + time*advance;\n        vec2 uv = vTextureCoord * scale;\n        float n = sin(pos.x + 3.0 * uv.x) + sin(pos.y - 4.0 * uv.x) + sin(pos.z + 2.0 * uv.y) + sin(pos.w + 5.0 * uv.y);\n        n = mod(((4.0 + n) / 4.0), 1.0);\n        n += dot(texture2D(sTexture, vTextureCoord).rgb, vec3(0.2, 0.4, 0.2));\n        return rainbow(n);\n    }\nvoid main() {\n    float intensity = paramIntensity / 100.0;\n    speed = 10.0 + (paramSpeed / 100.0)*25.0;\n    scale = 1.5 + (paramSize / 100.0)*10.0;\n    vec4 tc = texture2D(sTexture, vTextureCoord);\n    float color = 1.0 - (length(tc - vec4(0.153, 0.461, 0.181, 1.0)) / 1.75);\n    float alpha = 1.0 - clampNorm(5.0*(color - 0.7));\n    gl_FragColor = vec4(mix(tc.rgb, alpha * plasma(), intensity), 1.0);\n}\n";
    private float paramIntensity = 75.0f;
    private float paramSize = 20.0f;
    private float paramSpeed = 20.0f;

    public GlPlasmaFilter() {
        super(FRAGMENT_SHADER);filterName = "Plasma";
    }

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
        FilterUtilsKt.setFloat(getHandle("paramSize"), this.paramSize);
        FilterUtilsKt.setFloat(getHandle("paramSpeed"), this.paramSpeed);
    }
}
