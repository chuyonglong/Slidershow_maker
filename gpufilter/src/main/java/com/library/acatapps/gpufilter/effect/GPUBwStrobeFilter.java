package com.library.acatapps.gpufilter.effect;

import android.opengl.GLES20;


import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

public class GPUBwStrobeFilter extends GPUImageFilter {
    private static final String FRAGMENT_SHADER = "\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\n#define PI 3.14159265359\nuniform float time;\nuniform float paramIntensity;\nuniform float paramSpeed;\nvoid main() {\n    float intensity = paramIntensity / 100.0;\n    float speed = 0.1 + (paramSpeed / 100.0)*1.25;\n    vec3 ts = texture2D(sTexture, vTextureCoord).rgb;\n    float extraLum = 1.25 - 1.5 * intensity * sin(speed*time*6.4) * cos(speed*time*10.0);\n    float lum = dot(ts, vec3(0.3, 0.59, 0.11)) * extraLum;\n    gl_FragColor = vec4(vec3(lum), 1.0);\n}\n";
    private float paramIntensity = 75.0f;
    private float paramSpeed = 50.0f;

    private int intensityLocation;
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

    public GPUBwStrobeFilter() {
        super(NO_FILTER_VERTEX_SHADER, FRAGMENT_SHADER);
    }

    @Override
    public void onInit() {
        super.onInit();
        intensityLocation = GLES20.glGetUniformLocation(getProgram(), "paramIntensity");
        speedLocation = GLES20.glGetUniformLocation(getProgram(), "paramSpeed");
        localeTime = GLES20.glGetUniformLocation(getProgram(), "time");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setFloat(intensityLocation , paramIntensity);
        setFloat(speedLocation , paramSpeed);
    }

    public void setIntensity(float value){
        setFloat(localeTime, value);
    }
}
