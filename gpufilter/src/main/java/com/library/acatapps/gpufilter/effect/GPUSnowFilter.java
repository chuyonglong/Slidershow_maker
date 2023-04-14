package com.library.acatapps.gpufilter.effect;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

public class GPUSnowFilter extends GPUImageFilter {
    private static final String FRAGMENT_SHADER = "\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float texelWidth;\nuniform float texelHeight;\nuniform float time;\nuniform int orientation;\n#define cc vec2(0.5,0.5)\n#define SNOW_COL vec4(1.0,1.0,1.0,1.0)\n#define SNOW_ALPHA 0.75\nuniform float paramIntensity;\nuniform float paramSize;\nuniform float paramSpeed;\nfloat smoothness;\nfloat smoothCircle(vec2 position, float relativeSize) {\n    float d = distance(cc,position)*2./relativeSize;\n    return d > 1.0 ? 0.0 : clamp(smoothness/d-smoothness,-1.0,1.0);\n}\nfloat randF(float n) { return fract(sin(n) * 43758.5453123); }\nbool rand2d(float i, float j, float probability) { return  (randF(i + j*7.8124861) > probability); }\nfloat circleGrid(vec2 position, float spacing, float dotSize) {\n    float idx = floor(1./spacing * position.x);\n    float yIdx = floor(1./spacing * position.y);\n    if (rand2d(idx,yIdx,0.06)) { return 0.0; }\n    float relativeSize = (0.5 + 0.5*randF(yIdx))*dotSize / spacing;\n    return smoothCircle(vec2(fract(1./spacing*position.x),fract(1./spacing*position.y + yIdx)),relativeSize);\n}\nvoid main() {\n    float layers = 1.0 + (paramIntensity / 100.0)*24.0;\n    smoothness = 0.1 + (paramSize / 100.0)*1.2;\n    float speed = -(0.1 + (paramSpeed / 100.0)*0.9);\n    vec2 uvsq = vec2(vTextureCoord.x * texelHeight / texelWidth, vTextureCoord.y);\n    uvsq = orientation > 0 ? uvsq.yx : uvsq;\n    float amnt = 0.0;\n    float rotX = 0.0;\n    float rotY = 0.0;\n    for (float i = 0.0; i < layers; i++) {\n        float p = 0.5 + ((i+1.) / layers)*0.4;\n        vec2 fallPosition = vec2(\n            rotX * (1.0-p) + uvsq.x + i + p*sin(time/2.+i)/4.*speed,\n            rotY * (1.0-p) + i * 3.0 + uvsq.y + time*p/1.*speed);\n    \tamnt = amnt + SNOW_ALPHA * circleGrid(fallPosition, 0.06* p, 0.04* p*p);\n    }\n    gl_FragColor = mix(SNOW_COL, texture2D(sTexture, vTextureCoord), 1.0-amnt);\n}\n";
    private int orientation;
    private float paramIntensity = 20.0f;
    private float paramSize = 25.0f;
    private float paramSpeed = 25.0f;
    private float texelHeight;
    private float texelWidth;

    private int orientationLocation;
    private int inTensityLocation;
    private int sizeLocation;
    private int speedLocation;
    private int texelWidthLocation;
    private int texelHeightLocation;

    private int localeTime;

    public static final String NO_FILTER_VERTEX_SHADER = "" +
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            " \n" +
            "varying vec2 vTextureCoord;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = position;\n" +
            "    vTextureCoord = inputTextureCoordinate.xy;\n" +
            "}";

    public GPUSnowFilter() {
        super(NO_FILTER_VERTEX_SHADER, FRAGMENT_SHADER);
    }

    @Override
    public void onInit() {
        super.onInit();
        inTensityLocation = GLES20.glGetUniformLocation(getProgram(), "paramIntensity");
        speedLocation = GLES20.glGetUniformLocation(getProgram(), "paramSpeed");
        texelWidthLocation = GLES20.glGetUniformLocation(getProgram(), "texelWidth");
        texelHeightLocation = GLES20.glGetUniformLocation(getProgram(), "texelHeight");
        localeTime = GLES20.glGetUniformLocation(getProgram(), "time");
        orientationLocation = GLES20.glGetUniformLocation(getProgram(), "orientation");
    }

    @Override
    public void onDraw(int textureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
        super.onDraw(textureId, cubeBuffer, textureBuffer);
        setFloat(texelWidthLocation, 1f / getOutputWidth());
        setFloat(texelHeightLocation, 1f / getOutputHeight());
        setFloat(inTensityLocation, paramIntensity);
        setFloat(speedLocation, paramSpeed);
        setFloat(orientationLocation, getOutputWidth() > getOutputHeight() ? 0 : 1);
    }

    public void setIntensity(float value){
        setFloat(localeTime, value);
    }
}
