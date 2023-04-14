package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.more_filter.FilterTimable;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;
import com.daasuu.gpuv.egl.more_filter.Orientation;

public class GlFireFilter extends FilterTimable {

    private static final String FRAGMENT_SHADER = "\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float texelWidth;\nuniform float texelHeight;\nuniform float time;\nuniform int orientation;\nconst float shift = 1.6;\nuniform float paramIntensity;\nuniform float paramQuality;\nuniform float paramSpeed;\nint quality;\nfloat rand(vec2 n) {\n    return fract(cos(dot(n, vec2(12.9898, 4.1414))) * 43758.5453);\n}\nfloat noise(vec2 n) {\n    const vec2 d = vec2(0.0, 1.0);\n    vec2 b = floor(n), f = smoothstep(vec2(0.0), vec2(1.0), fract(n));\n    return mix(mix(rand(b), rand(b + d.yx), f.x), mix(rand(b + d.xy), rand(b + d.yy), f.x), f.y);\n}\nfloat fbm(vec2 n) {\n    float total = 0.0, amplitude = 1.0;\n    for (int i = 0; i < quality; i++) {\n        total += noise(n) * amplitude;\n        n += n;\n        amplitude *= 0.5;\n    }\n    return total;\n}\nvoid main() {\n    float intensity = (paramIntensity / 100.0)*1.0;\n    quality = 2 + int(paramQuality / 100.0 * 6.0);\n    vec2 speed = vec2(1.05, 0.6) * (0.25 + (paramSpeed / 100.0)*3.0);\n    const vec3 c1 = vec3(0.5, 0.0, 0.1);\n    const vec3 c2 = vec3(0.9, 0.0, 0.0);\n    const vec3 c3 = vec3(0.2, 0.0, 0.0);\n    const vec3 c4 = vec3(1.0, 0.9, 0.0);\n    const vec3 c5 = vec3(0.1);\n    const vec3 c6 = vec3(0.9);\n    vec2 p = vec2(8.0*vTextureCoord.x, 8.0*vTextureCoord.y/texelHeight*texelWidth);\n    float q = fbm(p - time * 0.1);\n    vec2 r = vec2(fbm(p + q + time * speed.x - p.x - p.y), fbm(p + q - time * speed.y));\n    vec3 c = mix(c1, c2, fbm(p + r)) + mix(c3, c4, r.x) - mix(c5, c6, r.y);\n    vec4 fire = vec4(c * cos(shift * (orientation > 0 ? vTextureCoord.x : vTextureCoord.y)), 1.0);\n    gl_FragColor = mix(texture2D(sTexture, vTextureCoord), fire, intensity);\n}";
    private int orientation;
    private float paramIntensity = 50.0f;
    private float paramQuality = 20.0f;
    private float paramSpeed = 25.0f;
    private float texelHeight;
    private float texelWidth;

    public GlFireFilter() {
        super(FRAGMENT_SHADER);
        filterName = "Fire";
    }

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("texelWidth"), this.texelWidth);
        FilterUtilsKt.setFloat(getHandle("texelHeight"), this.texelHeight);
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
        FilterUtilsKt.setFloat(getHandle("paramSpeed"), this.paramSpeed);
        FilterUtilsKt.setFloat(getHandle("paramQuality"), this.paramQuality);
        FilterUtilsKt.setInteger(getHandle("orientation"), Orientation.HORIZONTAL);
    }

    public void setFrameSize(int i, int i2) {
        this.texelWidth = 1.0f / ((float) i);
        this.texelHeight = 1.0f / ((float) i2);
        this.orientation = Orientation.INSTANCE.getOrientation(i, i2);
    }
}
