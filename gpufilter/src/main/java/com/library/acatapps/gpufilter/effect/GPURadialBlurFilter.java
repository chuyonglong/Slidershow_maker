package com.library.acatapps.gpufilter.effect;

import android.opengl.GLES20;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

public class GPURadialBlurFilter extends GPUImageFilter {
    private static final String FRAGMENT_SHADER = "\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float paramIntensity;\nvoid main() {\n    float intensity = 0.995 - (paramIntensity / 100.0)*0.03;\n    vec3 p = vec3(vTextureCoord.x, vTextureCoord.y, 1.0) - 0.5;\n    vec3 o = texture2D(sTexture, 0.5+(p.xy *= .988)).rbb;\n    for (float i=0.0 ; i<32.0 ; i++) {\n        p.z += pow(max(0.,0.21-length(texture2D(sTexture, 0.5+(p.xy *= intensity)).r)),2.)*exp(-i*.08);\n    }\n    gl_FragColor=vec4(o*o+p.z, 1.0);\n}\n";
    private float paramIntensity = 50.0f;

    private int intensityLocation;

    public GPURadialBlurFilter() {
        super("attribute vec4 position;\nattribute vec4 inputTextureCoordinate;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = position;\nvTextureCoord = inputTextureCoordinate.xy;\n}\n", FRAGMENT_SHADER);
    }

    @Override
    public void onInit() {
        super.onInit();
        intensityLocation = GLES20.glGetUniformLocation(getProgram(), "paramIntensity");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setFloat(intensityLocation, paramIntensity);
    }
}
