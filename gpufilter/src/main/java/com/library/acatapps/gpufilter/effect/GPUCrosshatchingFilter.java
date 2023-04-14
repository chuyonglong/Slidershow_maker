package com.library.acatapps.gpufilter.effect;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

public class GPUCrosshatchingFilter extends GPUImageFilter {
    public static final String FRAGMENT_SHADER = "precision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float texelWidth;\nuniform float texelHeight;\nuniform float paramIntensity;\nconst float lum_threshold_1 = 1.0;\nconst float lum_threshold_2 = 0.7;\nconst float lum_threshold_3 = 0.5;\nconst float lum_threshold_4 = 0.3;\nvoid main() {\n    float absHatchOffset = 9.0 - (paramIntensity / 100.0)*16.0;\n    vec3 tc = vec3(1.0, 1.0, 1.0);\n    vec2 absCoords = vec2(vTextureCoord.x/texelWidth, vTextureCoord.y/texelHeight);\n    float absHatchMod = 2.0*absHatchOffset;\n    float lum = length(texture2D(sTexture, vTextureCoord).rgb);\n    if (lum < lum_threshold_1) {\n        if (int(mod(floor(absCoords.x + absCoords.y), absHatchMod)) == 0) tc = vec3(0.0, 0.0, 0.0);\n    }\n    if (lum < lum_threshold_2) {\n        if (int(mod(floor(absCoords.x - absCoords.y), absHatchMod)) == 0) tc = vec3(0.0, 0.0, 0.0);\n    }\n    if (lum < lum_threshold_3) {\n        if (int(mod(floor(absCoords.x + absCoords.y - absHatchOffset), absHatchMod)) == 0) tc = vec3(0.0, 0.0, 0.0);\n    }\n    if (lum < lum_threshold_4) {\n        if (int(mod(floor(absCoords.x - absCoords.y - absHatchOffset), absHatchMod)) == 0) tc = vec3(0.0, 0.0, 0.0);\n    }\n    gl_FragColor = vec4(tc.rgb, 1.0);\n}\n";
    private float paramIntensity = 70.0f;
    private float texelHeight;
    private float texelWidth;

    private int intensityLocation;
    private int texelWidthLocation;
    private int texelHeightLocation;

    public GPUCrosshatchingFilter() {
        super("attribute vec4 position;\nattribute vec4 inputTextureCoordinate;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = position;\nvTextureCoord = inputTextureCoordinate.xy;\n}\n", FRAGMENT_SHADER);
    }

    @Override
    public void onInit() {
        super.onInit();
        intensityLocation = GLES20.glGetUniformLocation(getProgram(), "paramIntensity");
        texelHeightLocation = GLES20.glGetUniformLocation(getProgram(), "texelHeight");
        texelWidthLocation = GLES20.glGetUniformLocation(getProgram(), "texelWidth");
    }

    @Override
    public void onDraw(int textureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
        super.onDraw(textureId, cubeBuffer, textureBuffer);
        setFloat(intensityLocation, paramIntensity);
        setFloat(texelWidthLocation, 1f / getOutputWidth());
        setFloat(texelHeightLocation, 1f / getOutputHeight());
    }
}
