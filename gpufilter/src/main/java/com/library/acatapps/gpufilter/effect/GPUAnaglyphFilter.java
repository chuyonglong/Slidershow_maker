package com.library.acatapps.gpufilter.effect;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

public class GPUAnaglyphFilter extends GPUImageFilter {
    private static final float ASPECT = 54.0f;
    private static final float DEFAULT_OFFSET = 0.018518519f;
    private static final String FRAGMENT_SHADER = "precision highp float;" +
            "uniform lowp sampler2D sTexture;" +
            "varying highp vec2 gbCoordinate;" +
            "varying highp vec2 rCoordinate;" +
            "void main() {" +
            "gl_FragColor = vec4(texture2D(sTexture, rCoordinate).r, texture2D(sTexture, gbCoordinate).gb, 1.0);" +
            "}";
    private static final String VERTEX_SHADER = "attribute vec4 position;" +
            "attribute vec4 inputTextureCoordinate;" +
            "varying highp vec2 gbCoordinate;" +
            "varying highp vec2 rCoordinate;" +
            "uniform float imageWidthFactor;" +
            "uniform float imageHeightFactor;" +
            "void main() {" +
            "gl_Position = position;" +
            "mediump vec2 offset = vec2( -imageWidthFactor, imageHeightFactor);" +
            "gbCoordinate = inputTextureCoordinate.xy;" +
            "rCoordinate = inputTextureCoordinate.xy + offset;" +
            "}";
    private float imageHeightFactor;
    private float imageWidthFactor;

    private int imageWidthLocation;
    private int imageHeightLocation;

    public GPUAnaglyphFilter() {
        this(0.0f, 0.0f, 3);
    }

    public GPUAnaglyphFilter(float f, float f2) {
        super(VERTEX_SHADER, FRAGMENT_SHADER);
        this.imageWidthFactor = f;
        this.imageHeightFactor = f2;
    }

    public GPUAnaglyphFilter(float f, float f2, int i) {
        this(f, f2);
        if ((i & 1) != 0) {
            f = DEFAULT_OFFSET;
        }
        if ((i & 2) != 0) {
            f2 = DEFAULT_OFFSET;
        }

    }

    @Override
    public void onInit() {
        super.onInit();
        imageWidthLocation = GLES20.glGetUniformLocation(getProgram(), "imageWidthFactor");
        imageHeightLocation = GLES20.glGetUniformLocation(getProgram(), "imageHeightFactor");
    }

    @Override
    public void onDraw(int textureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
        super.onDraw(textureId, cubeBuffer, textureBuffer);
        setFloat(imageWidthLocation , (getOutputHeight() / ASPECT) / getOutputWidth());
        setFloat(imageHeightLocation , DEFAULT_OFFSET);
    }
}
