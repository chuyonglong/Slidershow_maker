package com.library.acatapps.gpufilter.effect;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

public class GPUBokehFilter extends GPUImageFilter {
    private static final String FRAGMENT_SHADER = "\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float texelWidth;\nuniform float texelHeight;\nconst float PI = 3.14159535;\nconst float ITER_ANGLE = 2.3999632;\nconst float ITERS = 16.0 * ITER_ANGLE;\nuniform float paramIntensity;\nuniform float paramSize;\nvoid main() {\n    float intensity = (paramIntensity / 100.0)*0.01;\n    float size = 0.001 + (paramSize / 100.0)*0.01;\n    vec2 uv = vTextureCoord;\n    vec2 R = 1.0 / vec2(texelWidth, texelHeight);\n    float radius = intensity*distance(uv, vec2(0.5))*length(R);\n    float bokehMult = 150.0;\n    vec3 to = vec3(0.0);\n    vec3 d = vec3(0.0);\n    vec2 pxl = size * radius * vec2(texelWidth/texelHeight, 1.0);\n    float t = 1.0;\n    for (float angle = 0.0; angle < ITERS; angle += ITER_ANGLE) {\n        t = t + (1.0 / t);\n        vec2 offset = (t - 1.0) * vec2(cos(angle), sin(angle));\n        vec3 tc = texture2D(sTexture, uv + pxl * offset).rgb;\n        vec3 bokeh = vec3(4.0) + pow(tc, vec3(8.0)) * bokehMult;\n        to += tc * bokeh;\n        d += bokeh;\n    }\n    gl_FragColor = vec4(to/d, 1.0);\n}\n";
    private float paramIntensity = 50.0f;
    private float paramSize = 50.0f;

    private int intensityLocation;
    private int sizeLocation;
    private int texelWidthLocation;
    private int texelHeightLocation;

    public GPUBokehFilter() {
        super("attribute vec4 position;\nattribute vec4 inputTextureCoordinate;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = position;\nvTextureCoord = inputTextureCoordinate.xy;\n}\n", FRAGMENT_SHADER);
    }

    @Override
    public void onInit() {
        super.onInit();
        intensityLocation = GLES20.glGetUniformLocation(getProgram(), "paramIntensity");
        sizeLocation = GLES20.glGetUniformLocation(getProgram(), "paramSize");
        texelWidthLocation = GLES20.glGetUniformLocation(getProgram(), "texelWidth");
        texelHeightLocation = GLES20.glGetUniformLocation(getProgram(), "texelHeight");
    }

    @Override
    public void onDraw(int textureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
        super.onDraw(textureId, cubeBuffer, textureBuffer);
        setFloat(texelWidthLocation, 1f / getOutputWidth());
        setFloat(texelHeightLocation, 1f / getOutputHeight());
        setFloat(intensityLocation, paramIntensity);
        setFloat(sizeLocation, paramSize);
    }
}
