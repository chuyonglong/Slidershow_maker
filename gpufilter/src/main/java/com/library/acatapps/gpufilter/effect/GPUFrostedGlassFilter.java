package com.library.acatapps.gpufilter.effect;

import android.opengl.GLES20;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

public class GPUFrostedGlassFilter extends GPUImageFilter {
    private static final String FRAGMENT_SHADER = "\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float paramIntensity;\nuniform float paramSmoothness;\nconst float rnd_factor = 0.05;\nconst vec2 v1 = vec2(92.,80.);\nconst vec2 v2 = vec2(41.,62.);\nfloat rnd_scale;\nfloat rand(vec2 co) {\n  return fract(sin(dot(co ,v1)) + cos(dot(co ,v2)) * rnd_scale); }\nvoid main() {\n    rnd_scale = 0.5 + (paramSmoothness / 100.0)*9.2;\n    float intensity = paramIntensity / 100.0;\n    vec4 tc = texture2D(sTexture, vTextureCoord);\n    vec2 rnd = vec2(rand(vTextureCoord.xy), rand(vTextureCoord.yx));\n    gl_FragColor = mix(tc, texture2D(sTexture, vTextureCoord+rnd*rnd_factor), intensity);\n}";
    private float paramIntensity = 80.0f;
    private float paramSmoothness = 50.0f;

    private int intensityLocation;
    private int smoothnessLocation;

    public GPUFrostedGlassFilter() {
        super("attribute vec4 position;\nattribute vec4 inputTextureCoordinate;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = position;\nvTextureCoord = inputTextureCoordinate.xy;\n}\n", FRAGMENT_SHADER);
    }

    @Override
    public void onInit() {
        super.onInit();
        intensityLocation = GLES20.glGetUniformLocation(getProgram(), "paramIntensity");
        smoothnessLocation = GLES20.glGetUniformLocation(getProgram(), "paramSmoothness");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setFloat(intensityLocation , paramIntensity);
        setFloat(smoothnessLocation , paramSmoothness);
    }
}
