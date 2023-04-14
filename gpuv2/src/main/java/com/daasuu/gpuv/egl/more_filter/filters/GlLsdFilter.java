package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlLsdFilter extends GlFilter {

    private static final String FRAGMENT_SHADER = "\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float time;\nuniform float texelWidth;\nuniform float texelHeight;\nconst float PI = 3.1415926535;\nuniform float paramIntensity;\nuniform float paramSpeed;\nuniform float paramSize;\nvec2 linMap (vec2 v, vec2 a0, vec2 a1, vec2 b0, vec2 b1) { return b0 + (v - a0) * (b1 - b0) / (a1 - a0); }\nfloat hash(float f) { return fract(sin(f)*42758.5453123); }\nfloat noise(vec2 v) {\n    vec2 p = floor(v);\n    vec2 f = fract(v);\n    f = f * f * (3.0 - 2.0 * f);\n    float n = p.x + p.y * 57.0;\n    return mix(mix( hash(n + 0.0), hash(n + 1.0), f.x), mix(hash(n + 57.0), hash(n + 58.0), f.x), f.y);\n}\nfloat fbm(vec2 v){\n    float result = 0.0;\n    float mult = 1.0;\n    float multSum = 0.0;\n    for (int i=0 ; i<2 ; ++i) {\n        mult *= 0.5;\n        result += mult*noise(v);\n        v *= 2.02 * mat2(0.5, 0.5, -0.5, 0.9);\n        multSum += mult;\n    }\n    return result / multSum;\n}\nvoid main() {\n    float intensity = (paramIntensity / 100.0)*1.5;\n    float speed = (paramSpeed / 100.0)*1.2;\n    float size = paramSize / 100.0;\n    vec2 uv = vTextureCoord;\n    vec2 R = 1.0 / vec2(texelWidth, texelHeight);\n    vec2 p = - 1. + 2. * uv;\n    p.x *= texelHeight / texelWidth ;\n    vec2 control0  = vec2( 1.0, 0.0);\n    vec2 control1 = vec2(-1.0, 0.0);\n    vec2 x = R*vec2(size, fract(20.0*size));\n    vec2 m = linMap(vec2(2.0, 1.0)*x, vec2(0.0), R, vec2(1.5, -PI), vec2(6.0, 0.));\n    float r0 = length(p + control0) + m.y;\n    float r1 = length(p + control1) + m.y;\n    float a = r0 * r1;\n    a *= fbm(m.x * p);\n    a -= speed*time;\n    const vec3 COLOR0 = vec3(1.0, 0.6, 0.2);\n    const vec3 COLOR1   = vec3(0.2, 0.6, 0.6);\n    const vec3 COLOR2  = vec3(0.1, 0.0, 0.1);\n    vec3 col = COLOR2;\n    float f = smoothstep(0.15, 0.8, fbm(vec2(a * 30.0, r0 * r1)));\n    col =  mix(col, COLOR1, f);\n    f = smoothstep(0.35, 0.8, fbm(vec2(a * 30.0 , r0 * r1)));\n    col =  mix(col, COLOR0, f);\n    f = smoothstep(0.25, 0.8, fbm(vec2(a * 30.0, r0 * r1)));\n    col *=  1.9 - f ;\n    vec3 ts = texture2D(sTexture, uv).rgb;\n    vec3 to = step(1.0, ts + col);\n    gl_FragColor = vec4(mix(ts, to, intensity), 1.);\n}\n";
    private float paramIntensity = 50.0f;
    private float paramSize = 50.0f;
    private float paramSpeed = 25.0f;
    private float texelHeight;
    private float texelWidth;


    public GlLsdFilter() {
        super("attribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = aPosition;\nvTextureCoord = aTextureCoord.xy;\n}\n", FRAGMENT_SHADER);
        filterName = "Lsd";}

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("texelWidth"), this.texelWidth);
        FilterUtilsKt.setFloat(getHandle("texelHeight"), this.texelHeight);
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
        FilterUtilsKt.setFloat(getHandle("paramSize"), this.paramSize);
        FilterUtilsKt.setFloat(getHandle("paramSpeed"), this.paramSpeed);
    }

    public void setFrameSize(int i, int i2) {
        this.texelWidth = 1.0f / ((float) i);
        this.texelHeight = 1.0f / ((float) i2);
    }
}
