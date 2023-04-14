package com.library.acatapps.gpufilter.effect;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

public class GPUSplitColorFilter extends GPUImageFilter {
    private static final String FRAGMENT_SHADER = "\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float time;\nuniform float paramIntensity;\nuniform float paramSpeed;\nvoid main() {\n    float intensity = (paramIntensity / 100.0)*8.0;\n    float speed = 0.25 + (paramSpeed / 100.0)*4.0;\n    vec2 deltaR = intensity*0.01*vec2(sin(speed*time)+sin(speed*0.3*time)+sin(speed*0.06*time)*0.25+sin(speed*0.9*time), 0);\n    vec2 deltaB = intensity*0.01*vec2(sin(speed*1.2*time)+sin(speed*0.15*time)+sin(speed*0.02*time)*0.3+sin(speed*0.8*time), 0);\n    vec4 tc = texture2D(sTexture, vTextureCoord);\n    tc.r = texture2D(sTexture, vTextureCoord + deltaR).r;\n    tc.b = texture2D(sTexture, vTextureCoord - deltaB).b;\n    gl_FragColor = vec4(tc.rgb, 1.0);\n}\n";
    private float paramIntensity = 50.0f;
    private float paramSpeed = 20.0f;

    private int intensityLocation;
    private int speedLocation;
    private int timeLocation;

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

    public GPUSplitColorFilter() {
        super(NO_FILTER_VERTEX_SHADER, FRAGMENT_SHADER);
    }

    @Override
    public void onInit() {
        super.onInit();
        intensityLocation = GLES20.glGetUniformLocation(getProgram(), "paramIntensity");
        speedLocation = GLES20.glGetUniformLocation(getProgram(), "paramSpeed");
        timeLocation = GLES20.glGetUniformLocation(getProgram(), "time");
    }

    @Override
    public void onDraw(int textureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
        super.onDraw(textureId, cubeBuffer, textureBuffer);
        setFloat(intensityLocation , paramIntensity);
        setFloat(speedLocation , paramSpeed);
        setFloat(timeLocation , 20f);
    }

    public void setIntensity(float value){
        setFloat(timeLocation, value);
    }
}
