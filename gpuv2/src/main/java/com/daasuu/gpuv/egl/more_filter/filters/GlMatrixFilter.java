package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.more_filter.FilterTimable;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlMatrixFilter extends FilterTimable {

    private static final String FRAGMENT_SHADER = "precision highp float;varying vec2 vTextureCoord;uniform sampler2D sTexture;uniform float texelWidth;uniform float texelHeight;\nuniform float time;\nuniform float paramSpeed;\nuniform float paramIntensity;\nfloat size;\nfloat speed;\nfloat rand(vec2 v) { return fract(sin(dot(v.xy ,vec2(12.9898, 78.233))) * 43758.5453); }\nfloat rchar(vec2 outer, vec2 inner, float globalTime) {\n    vec2 seed = floor(inner * 5.0) + outer.y;\n    seed += rand(vec2(outer.y, 27.0)) > 0.92 ? floor((globalTime + rand(vec2(outer.y, 45.4))) * 2.5) : 0.0;\n    return float(rand(seed) > 0.55);\n}\nvec4 effect(vec2 offset, float width) {\n    float globalTime = -1.0 * (time + 2.0) * speed;\n    vec2 position = vTextureCoord + offset;\n    float rx = vTextureCoord.x / (texelWidth * 45.0 * size);\n    float mx = 45.0*size*fract(position.x * 32.0 * size);\n    if (mx > 15.0 * size) {\n        return vec4(0.0);\n    } else {\n        float x = floor(rx);\n        float r1x = floor(vTextureCoord.x / (texelWidth * width));\n        float ry = position.y*700.0 + rand(vec2(x, x * 3.0)) * 20000.0 + globalTime* rand(vec2(r1x, 22.0)) * 135.0;\n        float my = mod(ry, 15.0);\n        if (my > 15.0 * size) {\n            return vec4(0.0);\n        } else {\n            float y = floor(ry / 15.0);\n            float b = rchar(vec2(rx, floor((ry) / 15.0)), vec2(mx, my) / 15.0, globalTime);\n            float col = max(mod(-y, 24.0) - 4.0, 0.0) / 20.0;\n            vec3 c = col < 0.85 ? vec3(0.0, col / 0.85, 0.0) : mix(vec3(0.0, 1.0, 0.0), vec3(1.0), (col - 0.85) / 0.2);\n            return vec4(c * b, 1.0);\n        }\n    }\n}\nvoid main() {\n    size = 1.0 + (paramIntensity / 100.0)*4.0;\n    speed = 0.75 + (paramSpeed / 100.0)*4.0;\n    float stripeWidth = 0.5 * sqrt(pow(1.0/texelWidth/100.0, 2.0) + pow(1.0/texelHeight/100.0, 2.0));\n    vec4 result = effect(vec2(0.0), 1.25*stripeWidth) + effect(vec2(0.05, 0.0), stripeWidth);\n    vec4 ti = texture2D(sTexture, vTextureCoord);\n    result = result * length(ti.rgb) + 0.48 * vec4(0.0, ti.g, 0.0, 1.0);    result.b = result.b < 0.5 ? result.g * 0.5 : result.b;   gl_FragColor = vec4(result.rgb, 1.0);}";
    private float paramIntensity = 25.0f;
    private float paramSpeed = 25.0f;
    private float texelHeight;
    private float texelWidth;

    public GlMatrixFilter() {
        super(FRAGMENT_SHADER);filterName = "Matrix";
    }

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("texelWidth"), this.texelWidth);
        FilterUtilsKt.setFloat(getHandle("texelHeight"), this.texelHeight);
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
        FilterUtilsKt.setFloat(getHandle("paramSpeed"), this.paramSpeed);
    }

    public void setFrameSize(int i, int i2) {
        this.texelWidth = 1.0f / ((float) i);
        this.texelHeight = 1.0f / ((float) i2);
    }
}
