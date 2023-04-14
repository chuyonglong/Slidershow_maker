package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.more_filter.FilterTimable;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlDawnbringerFilter extends FilterTimable {

    private static final String FRAGMENT_SHADER = "\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float time;\nuniform float texelWidth;\nuniform float texelHeight;\nuniform float paramIntensity;\nuniform float paramSize;\nuniform float paramColor;\nfloat color;\nfloat hash(vec2 v) { return fract(9831.0 * cos(289.0*v.x + 0.2*v.y) * abs(sin(2.0*v.x + 17.0*v.y))); }\nvoid compare(vec3 prepA, vec3 b, inout vec3 c0, inout vec3 c1, inout float minDist0, inout float minDist1) {\n    vec3 orgB = b;\n    b = max(vec3(0.0), b - min(b.r, min(b.g, b.b)) * color);\n    float dist = distance(prepA, b*b*b);\n    if (dist < minDist0) {\n        minDist1 = minDist0;\n        minDist0 = dist;\n        c1 = c0;\n        c0 = orgB;\n    }\n}\nvoid main() {\n    float intensity = (paramIntensity / 100.0)*1.5;\n    float size = 1.0 + (paramSize / 100.0)*8.0;\n    color = 0.05 + (paramColor / 100.0)*0.4;\n    vec2 R = vec2(texelWidth, texelHeight);\n    vec2 UV = vTextureCoord / R;\n    vec2 coord = floor(UV / size);\n    vec3 ts = texture2D(sTexture, coord * size * R).rgb;\n    vec3 c0 = vec3(0);\n    vec3 c1 = c0;\n    float minDist0 = 1000.0;\n    float minDist1 = minDist0;\n    vec3 prepTs = max(vec3(0.0), ts - min(ts.r, min(ts.g, ts.b)) * color);\n    prepTs = prepTs*prepTs*prepTs;\n    # define cmp(R,G,B) compare(prepTs, vec3(R,G,B), c0, c1, minDist0, minDist1);\n    cmp(0.078431, 0.047059, 0.109804);\n    cmp(0.266667, 0.141176, 0.203922);\n    cmp(0.188235, 0.203922, 0.427451);\n    cmp(0.305882, 0.290196, 0.305882);\n    cmp(0.521569, 0.298039, 0.188235);\n    cmp(0.203922, 0.396078, 0.141176);\n    cmp(0.815686, 0.274510, 0.282353);\n    cmp(0.458824, 0.443137, 0.380392);\n    cmp(0.349020, 0.490196, 0.807843);\n    cmp(0.823529, 0.490196, 0.172549);\n    cmp(0.521569, 0.584314, 0.631373);\n    cmp(0.427451, 0.666667, 0.172549);\n    cmp(0.823529, 0.666667, 0.600000);\n    cmp(0.427451, 0.760784, 0.792157);\n    cmp(0.854902, 0.831373, 0.368627);\n    cmp(0.870588, 0.933333, 0.839216);\n    minDist0 = sqrt(minDist0);\n    minDist1 = sqrt(minDist1);\n    float h = (hash(3.0*coord + fract(cos(vec2(floor(1.7*time))))) * 0.75) + (minDist1 / (minDist0 + minDist1));\n    gl_FragColor = vec4(mix(ts, mod(coord.x + coord.y, 2.0) > h  ? c1 : c0, intensity), 1.0);\n}\n";
    private float paramColor = 50.0f;
    private float paramIntensity = 60.0f;
    private float paramSize = 30.0f;
    private float texelHeight;
    private float texelWidth;

    public GlDawnbringerFilter() {
        super(FRAGMENT_SHADER);
        filterName = "Dawn Bringer";
    }

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("texelWidth"), this.texelWidth);
        FilterUtilsKt.setFloat(getHandle("texelHeight"), this.texelHeight);
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
        FilterUtilsKt.setFloat(getHandle("paramSize"), this.paramSize);
        FilterUtilsKt.setFloat(getHandle("paramColor"), this.paramColor);
    }

    public void setFrameSize(int i, int i2) {
        this.texelWidth = 1.0f / ((float) i);
        this.texelHeight = 1.0f / ((float) i2);
    }
}
