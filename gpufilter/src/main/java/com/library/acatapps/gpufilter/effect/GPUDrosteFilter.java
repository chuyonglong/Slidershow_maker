package com.library.acatapps.gpufilter.effect;

import android.opengl.GLES20;


import java.nio.FloatBuffer;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

public class GPUDrosteFilter extends GPUImageFilter {
    private static final String FRAGMENT_SHADER = "\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float time;\nuniform float texelWidth;\nuniform float texelHeight;\nuniform float paramSize;\nuniform float paramSpeed;\nuniform float paramIntensity;\nconst float TWO_PI = 3.141592*2.0;\nvec2 complexExp(in vec2 z) { return vec2(exp(z.x)*cos(z.y),exp(z.x)*sin(z.y)); }\nvec2 complexLog(in vec2 z) { return vec2(log(length(z)), atan(z.y, z.x)); }\nvec2 complexMult(in vec2 a,in vec2 b) { return vec2(a.x*b.x - a.y*b.y, a.x*b.y + a.y*b.x); }\nfloat complexMag(in vec2 z) { return float(pow(length(z), 2.0)); }\nvec2 complexReciprocal(in vec2 z) { return vec2(z.x / complexMag(z), -z.y / complexMag(z)); }\nvec2 complexDiv(in vec2 a,in vec2 b) { return complexMult(a, complexReciprocal(b)); }\nvec2 complexPower(in vec2 a, in vec2 b) { return complexExp( complexMult(b,complexLog(a))); }\nfloat nearestPower(in float a, in float base) { return pow(base,  ceil(  log(abs(a))/log(base)  )-1.0 ); }\nfloat map(float value, float istart, float istop, float ostart, float ostop) { return ostart + (ostop - ostart) * ((value - istart) / (istop - istart)); }\nvoid main() {\n    float intensity = 1.0 + (paramIntensity / 100.0)*24.0;\n    float scale = 0.1 + (paramSize / 100.0)*0.6;\n    float speed = 0.1 + (paramSpeed / 100.0)*5.0;\n    vec2 uv = vTextureCoord - 0.5;\n    uv.y *= texelWidth / texelHeight;\n    float factor = pow(1.0/scale, intensity);\n    uv = complexPower(uv, complexDiv(vec2(log(factor), TWO_PI), vec2(0.0, TWO_PI) ) );\n    float FT = fract(speed*time);\n    FT = log(FT+1.0) / log(2.0);\n    uv *= 1.0+FT*(scale-1.0);\n    float npower = max(nearestPower(uv.x, scale), nearestPower(uv.y, scale));\n    uv.x = map(uv.x,-npower,npower,-1.0,1.0);\n    uv.y = map(uv.y,-npower,npower,-1.0,1.0);\n    gl_FragColor =  texture2D(sTexture, uv*0.5 + 0.5);\n}\n";
    private float paramIntensity;
    private float paramSize = 50.0f;
    private float paramSpeed = 20.0f;

    private int intensityLocation;
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

    public GPUDrosteFilter() {
        super(NO_FILTER_VERTEX_SHADER, FRAGMENT_SHADER);
    }

    @Override
    public void onInit() {
        super.onInit();
        intensityLocation = GLES20.glGetUniformLocation(getProgram(), "paramIntensity");
        speedLocation = GLES20.glGetUniformLocation(getProgram(), "paramSpeed");
        texelWidthLocation = GLES20.glGetUniformLocation(getProgram(), "texelWidth");
        texelHeightLocation = GLES20.glGetUniformLocation(getProgram(), "texelHeight");
        localeTime = GLES20.glGetUniformLocation(getProgram(), "time");
        sizeLocation = GLES20.glGetUniformLocation(getProgram(), "paramSize");
    }

    @Override
    public void onDraw(int textureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
        super.onDraw(textureId, cubeBuffer, textureBuffer);
        setFloat(texelWidthLocation, 1f / getOutputWidth());
        setFloat(texelHeightLocation, 1f / getOutputHeight());
        setFloat(intensityLocation, paramIntensity);
        setFloat(speedLocation, paramSpeed);
        setFloat(sizeLocation, paramSize);
    }

    public void setIntensity(float value) {
        setFloat(localeTime, value);
    }
}
