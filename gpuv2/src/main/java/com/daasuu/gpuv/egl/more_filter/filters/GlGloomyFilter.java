package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.more_filter.FilterTimable;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlGloomyFilter extends FilterTimable {

    public static final String FRAGMENT_SHADER = "\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float time;\nuniform float paramIntensity;\nuniform float paramColor;\nfloat luminance(vec4 vector) { return dot(vector.rgb, vec3(1.0/3.0)); }\nfloat sampleLum(vec2 diff, vec2 uv) { return luminance(texture2D(sTexture, uv + diff)); }\nvoid main() {\n    float intensity = (paramIntensity / 100.0)*1.25;\n    float contrast = 1.75 - (paramColor / 100.0)*1.5;\n    float diff = sin(time) * 0.001;\n    float upLum = sampleLum(vec2(0.0, diff), vTextureCoord);\n    float downLum = sampleLum(vec2(0.0, -diff), vTextureCoord);\n    float leftLum = sampleLum(vec2(-diff, 0.0), vTextureCoord);\n    float rightLum = sampleLum(vec2(diff, 0.0), vTextureCoord);\n    vec4 tc = vec4(leftLum*rightLum, downLum*upLum, 0.0, 1.0);\n    tc.b = tc.r*tc.g;\n    vec4 ts = texture2D(sTexture, vTextureCoord);\n    gl_FragColor = mix(ts, pow(tc, vec4(contrast)), intensity);\n}\n";
    private float paramColor = 50.0f;
    private float paramIntensity = 80.0f;
    public GlGloomyFilter() {
        super(FRAGMENT_SHADER);
        filterName = "Gloomy";
    }
    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
        FilterUtilsKt.setFloat(getHandle("paramColor"), this.paramColor);
    }
}
