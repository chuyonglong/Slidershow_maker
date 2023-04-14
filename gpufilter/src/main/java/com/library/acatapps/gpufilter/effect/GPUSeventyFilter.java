package com.library.acatapps.gpufilter.effect;

import android.opengl.GLES20;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

public class GPUSeventyFilter extends GPUImageFilter {
    private static final String FRAGMENT_SHADER = "\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float paramIntensity;\nvoid main() {\n   float intensity = (paramIntensity / 100.0)*2.0;\n    vec4 tc = texture2D(sTexture, vTextureCoord);\n    float grayscale = 0.2125 * tc.r + 0.7154 * tc.g + 0.0721 * tc.b;\n    vec4 to = vec4(tc.r*abs(cos(grayscale)), tc.g*abs(sin(grayscale)), tc.b*abs(atan(grayscale) * sin(grayscale)), tc.a);\n    to.rgb *= 1.5;\n    gl_FragColor = mix(tc, to, intensity);\n}";
    private float paramIntensity = 50.0f;

    private int intensityLocation;

    public GPUSeventyFilter() {
        super("attribute vec4 position;\nattribute vec4 inputTextureCoordinate;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = position;\nvTextureCoord = inputTextureCoordinate.xy;\n}\n", FRAGMENT_SHADER);
    }

    @Override
    public void onInit() {
        super.onInit();
        intensityLocation = GLES20.glGetUniformLocation(getProgram(), "paramIntensity");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setFloat(intensityLocation , paramIntensity);
    }

}
