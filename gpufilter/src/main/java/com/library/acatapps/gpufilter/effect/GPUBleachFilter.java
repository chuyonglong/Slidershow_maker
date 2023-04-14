package com.library.acatapps.gpufilter.effect;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

public class GPUBleachFilter extends GPUImageFilter {
    private static final String FRAGMENT_SHADER = "\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float paramIntensity;\nvoid main() {\n    float intensity = (paramIntensity / 100.0)*4.0;\n    vec4 tc = texture2D(sTexture, vTextureCoord);\n    float grey = dot(tc.rgb, vec3(0.2125, 0.7154, 0.0721));\n    vec4 k = vec4(vec3(grey), 1.0);\n    float x = clamp(10.0*grey - 5.0, 0.0, 1.0);\n    vec4 t = 2.0 * tc * k;\n    vec4 w = 1.0 - (2.0 * (1.0 - tc) * (1.0 - k));\n    vec4 r = mix(t, w, x);\n    gl_FragColor = mix(tc, r, intensity);\n}\n";
    private float paramIntensity = 50.0f;

    private int inTensityLocation;

    public GPUBleachFilter() {
        super("attribute vec4 position;\nattribute vec4 inputTextureCoordinate;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = position;\nvTextureCoord = inputTextureCoordinate.xy;\n}\n", FRAGMENT_SHADER);
    }

    @Override
    public void onInit() {
        super.onInit();
        inTensityLocation = GLES20.glGetUniformLocation(getProgram(), "paramIntensity");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setFloat(inTensityLocation, paramIntensity);
    }

    @Override
    public void onDraw(int textureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
        super.onDraw(textureId, cubeBuffer, textureBuffer);
    }

    public void setIntensity(float value){
        setFloat(inTensityLocation, value);
    }
}
