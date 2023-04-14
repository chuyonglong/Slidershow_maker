package com.library.acatapps.gpufilter.effect;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

public class GPUCrossStitchingFilter extends GPUImageFilter {
    public static final String FRAGMENT_SHADER = "\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float texelWidth;\nuniform float texelHeight;\nconst int invert = 1;\nuniform float paramIntensity;\nvoid main() {\n    float intensity = 3.5 - (paramIntensity / 100.0)*2.75;\n    vec2 uv = vTextureCoord;\n    float stitching_size = 9.0 * sqrt(pow(1.0/texelWidth/100.0, 2.0) + pow(1.0/texelHeight/100.0, 2.0)) / 16.0;\n    stitching_size = max(3.0, floor(intensity * stitching_size));\n    vec2 cPos = vec2(uv.x / texelWidth, uv.y / texelHeight);\n    vec2 tlPos = floor(cPos / vec2(stitching_size, stitching_size)) * stitching_size;\n    int remX = int(mod(cPos.x, stitching_size));\n    int remY = int(mod(cPos.y, stitching_size));\n    if (remX == 0 && remY == 0) tlPos = cPos;\n    vec2 blPos = tlPos;\n    blPos.y += (stitching_size - 1.0);\n    vec4 c;\n    if ((remX == remY) || (((int(cPos.x) - int(blPos.x)) == (int(blPos.y) - int(cPos.y))))) {\n\t     c = invert == 1 ? vec4(0.2, 0.15, 0.05, 1.0) : 1.4*texture2D(sTexture, tlPos * vec2(texelWidth, texelHeight));\n    } else {\n\t     c = invert == 1 ? 1.4*texture2D(sTexture, tlPos * vec2(texelWidth, texelHeight)) : vec4(0.0, 0.0, 0.0, 1.0);\n    }\n    gl_FragColor = c;\n}";
    private float paramIntensity = 75.0f;
    private float texelHeight;
    private float texelWidth;

    private int intensityLocation;
    private int texelWidthLocation;
    private int texelHeightLocation;

    public GPUCrossStitchingFilter() {
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
