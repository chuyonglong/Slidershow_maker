package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.more_filter.FilterTimable;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlPolygonsFilter extends FilterTimable {

    public static final String FRAGMENT_SHADER = "\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\n#define PI 3.14159265359\nuniform float time;\nuniform float texelWidth;\nuniform float texelHeight;\nuniform float paramIntensity;\nuniform float paramSpeed;\nvec2 random2( vec2 p ) { return fract(sin(vec2(dot(p,vec2(127.1,311.7)),dot(p,vec2(269.5,183.3))))*43758.5453); }\nvoid main() {\n    float intensity = 0.5 + (paramIntensity / 100.0)*3.0;\n    float speed = 0.25 + (paramSpeed / 100.0)*6.0;\n    float screenRatio = texelHeight/texelWidth;\n    float largerScreenDimSize = max(1.0/texelWidth, 1.0/texelHeight);\n    vec2 st = vTextureCoord * vec2(screenRatio, 1.0);\n    float pSize = 5.0;\n    float scale = intensity*20.0;\n    st *= scale;\n    vec2 stInt = floor(st);\n    vec2 stFract = fract(st);\n    float minDist = 100.0;\n    vec2 quad;\n    for (int j=-1; j <= 1; j++ ) {\n        for (int i=-1; i<=1; i++ ) {\n            vec2 neighbor = vec2(float(i),float(j));\n            vec2 point = 0.5 + 0.5*sin(speed*time + 2.0*PI*random2(stInt + neighbor));\n            float dist = length(neighbor + point - stFract);\n            quad = dist <= minDist ? neighbor : quad;\n            minDist = dist <= minDist ? dist : minDist;\n        }\n    }\n    vec2 midPoint = (stInt + 0.5 + quad) / (vec2(screenRatio, 1.0)*scale);\n    vec2 m = mod(vec2(vTextureCoord.x / texelWidth, vTextureCoord.y / texelHeight), pSize) / vec2(largerScreenDimSize);\n    vec3 tc1 = texture2D(sTexture, midPoint + m).rgb;\n    vec3 tc2 = texture2D(sTexture, midPoint + pSize/largerScreenDimSize - m).rgb;\n    gl_FragColor = vec4(mix(tc1, tc2, 0.5), 1.0);\n}\n";
    private float paramIntensity = 30.0f;
    private float paramSpeed = 30.0f;
    private float texelHeight;
    private float texelWidth;

    public GlPolygonsFilter() {
        super(FRAGMENT_SHADER);filterName = "Polygon";
    }

    /* access modifiers changed from: protected */
    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("texelHeight"), this.texelHeight);
        FilterUtilsKt.setFloat(getHandle("texelWidth"), this.texelWidth);
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
        FilterUtilsKt.setFloat(getHandle("paramSpeed"), this.paramSpeed);
    }

    public void setFrameSize(int i, int i2) {
        this.texelWidth = 1.0f / ((float) i);
        this.texelHeight = 1.0f / ((float) i2);
    }
}
