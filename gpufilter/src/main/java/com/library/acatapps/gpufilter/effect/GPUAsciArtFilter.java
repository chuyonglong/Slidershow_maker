package com.library.acatapps.gpufilter.effect;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

public class GPUAsciArtFilter extends GPUImageFilter {
    private static final String FRAGMENT_SHADER = "\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float texelWidth;\nuniform float texelHeight;\nuniform float paramIntensity;\nuniform float paramSize;\nuniform float paramQuality;\nfloat character(float n, vec2 p) {\n    p = floor(p*vec2(4.0, -4.0) + 2.5);\n    return clamp(p.x, 0.0, 4.0) == p.x && clamp(p.y, 0.0, 4.0) == p.y && int(mod(n/exp2(p.x + 5.0*p.y), 2.0)) == 1 ? 1.0 : 0.0;\n}\nvoid main() {\n    float intensity = (paramIntensity / 100.0)*1.5;\n    float size = 3.0 + (paramSize / 100.0)*10.0;\n    float quality = 8.0 - (paramQuality / 100.0)*7.0;\n    vec2 R = vec2(texelWidth, texelHeight);\n    vec2 UV = vTextureCoord / R;\n    vec3 ts = texture2D(sTexture, floor(UV/quality)*quality*R).rgb;\n    float gray = dot(ts, vec3(0.3, 0.59, 0.11));\n    float n =  4096.0;\n    n = gray > 0.2 ? 65600.0 : n;\n    n = gray > 0.3 ? 332772.0 : n;\n    n = gray > 0.4 ? 15255086.0 : n;\n    n = gray > 0.5 ? 23385164.0 : n;\n    n = gray > 0.6 ? 15252014.0 : n;\n    n = gray > 0.7 ? 13199452.0 : n;\n    n = gray > 0.8 ? 11512810.0 : n;\n    vec2 p = mod(UV/size, 2.0) - vec2(1.0);\n    vec3 to = ts*character(n, p);\n    gl_FragColor = vec4(mix(ts, to, intensity), 1.0);\n}\n";
    private float paramIntensity = 65.0f;
    private float paramQuality = 50.0f;
    private float paramSize = 30.0f;

    private int inTensityLocation;
    private int qualityLocation;
    private int sizeLocation;
    private int texelWidthLocation;
    private int texelHeightLocation;

    public GPUAsciArtFilter() {
        super("attribute vec4 position;\nattribute vec4 inputTextureCoordinate;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = position;\nvTextureCoord = inputTextureCoordinate.xy;\n}\n", FRAGMENT_SHADER);
    }

    @Override
    public void onInit() {
        super.onInit();
        inTensityLocation = GLES20.glGetUniformLocation(getProgram(), "paramIntensity");
        qualityLocation = GLES20.glGetUniformLocation(getProgram(), "paramQuality");
        sizeLocation = GLES20.glGetUniformLocation(getProgram(), "paramSize");
        texelWidthLocation = GLES20.glGetUniformLocation(getProgram(), "texelWidth");
        texelHeightLocation = GLES20.glGetUniformLocation(getProgram(), "texelHeight");
    }

    @Override
    public void onDraw(int textureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
        super.onDraw(textureId, cubeBuffer, textureBuffer);
        setFloat(texelWidthLocation, 1f / getOutputWidth());
        setFloat(texelHeightLocation, 1f / getOutputHeight());
        setFloat(inTensityLocation, paramIntensity);
        setFloat(sizeLocation, paramSize);
        setFloat(qualityLocation, paramQuality);
    }
}
