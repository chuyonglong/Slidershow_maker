package com.library.acatapps.gpufilter.effect;

import android.opengl.GLES20;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

public class GPUWavyFilter extends GPUImageFilter {
    private static final String FRAGMENT_SHADER = "\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float time;\nuniform float paramIntensity;\nuniform float paramSize;\nuniform float paramSpeed;\nvoid main() {\n    float intensity = 0.005 + (paramIntensity / 100.0)*0.24;\n    float size = 1.0 + (paramSize / 100.0)*30.0;\n    float speed = 3.0 + (paramSpeed / 100.0)*27.0;\n    vec2 uv = vTextureCoord + vec2(sin(speed*time + vTextureCoord.y * size) * intensity, 0.0);\n    gl_FragColor = texture2D(sTexture, uv);\n}\n";
    private float paramIntensity = 20.0f;
    private float paramSize = 20.0f;
    private float paramSpeed = 25.0f;

    private int inTensityLocation;
    private int sizeLocation;
    private int speedLocation;
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


    public GPUWavyFilter() {
        super(NO_FILTER_VERTEX_SHADER, FRAGMENT_SHADER);
    }

    @Override
    public void onInit() {
        super.onInit();
        inTensityLocation = GLES20.glGetUniformLocation(getProgram(), "paramIntensity");
        sizeLocation = GLES20.glGetUniformLocation(getProgram(), "paramSize");
        speedLocation = GLES20.glGetUniformLocation(getProgram(), "paramSpeed");
        localeTime = GLES20.glGetUniformLocation(getProgram(), "time");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setFloat(inTensityLocation, paramIntensity);
        setFloat(sizeLocation, paramSize);
        setFloat(speedLocation, paramSpeed);
    }

    public void setIntensity(float value){
        setFloat(localeTime, value);
    }
}
