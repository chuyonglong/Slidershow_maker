package com.library.acatapps.gpufilter.effect;

import android.opengl.GLES20;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

public class GPUDispersionFilter extends GPUImageFilter {
    private static final String FRAGMENT_SHADER = "\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float time;\nuniform float paramIntensity;\nuniform float paramSize;\nuniform float paramSpeed;\nvoid main() {\n    float intensity = 0.001 + (paramIntensity / 100.0)*0.12;\n    float size = 2.0 + (paramSize / 100.0)*36.0;\n    float speed = 1.0 + (paramSpeed / 100.0)*12.0;\n    vec4 m = vec4(0);\n    vec4 to = vec4(0);\n    for(float i=0.0 ; i<1.0 ; i+=.08) {\n        vec4 c = vec4(i, 4.3*pow((1.0-i)*i, 2.2), 1.0-i, 1.0);\n        m += c*c;\n        to += c*c*texture2D(sTexture, vTextureCoord+i*intensity*(pow(cos(vTextureCoord*size+speed*time), vec2(3.0))-0.15));\n    }\n    gl_FragColor = smoothstep(0.0, 1.0, to/m);\n}\n";
    private float paramIntensity = 40.0f;
    private float paramSize = 50.0f;
    private float paramSpeed = 25.0f;

    private int intensityLocation;
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

    public GPUDispersionFilter() {
        super(NO_FILTER_VERTEX_SHADER, FRAGMENT_SHADER);
    }

    @Override
    public void onInit() {
        super.onInit();
        speedLocation = GLES20.glGetUniformLocation(getProgram(), "paramSpeed");
        sizeLocation = GLES20.glGetUniformLocation(getProgram(), "paramSize");
        intensityLocation = GLES20.glGetUniformLocation(getProgram(), "paramIntensity");
        localeTime = GLES20.glGetUniformLocation(getProgram(), "time");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setFloat(speedLocation, paramSpeed);
        setFloat(sizeLocation, paramSize);
        setFloat(intensityLocation, paramIntensity);
    }

    public void setIntensity(float value){
        setFloat(localeTime, value);
    }
}
