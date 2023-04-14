package com.library.acatapps.gpufilter.effect;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

public class GPUNightVisionFilter extends GPUImageFilter {
    private static final String FRAGMENT_SHADER = "\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float time;\nuniform float texelWidth;\nuniform float texelHeight;\nuniform float paramIntensity;\nuniform float paramSmoothness;\nuniform float paramSize;\nfloat hash(in float n) { return fract(sin(n)*43758.5453123); }\nvoid main() {\n    float intensity = 1.0 + (paramIntensity / 100.0)*3.0;\n    float quality = (1.0 - paramSmoothness / 100.0)*1.5;\n    float size = -0.4 + (paramSize / 100.0)*1.1;\n    vec2 n = (2.0*vTextureCoord - 1.0) * vec2(texelHeight / texelWidth, 1.0);\n    vec3 c = texture2D(sTexture, vTextureCoord).rgb;\n    c += quality * sin(hash(time)) * 0.01;\n    c += quality * hash((hash(n.x) + n.y) * (time + 2.0)) * 0.5;\n    c *= mix(1.5*smoothstep(length(n * n * n * vec2(0.075, 0.4)), 1.0, 0.4), 1.0, size);\n    c = dot(c, vec3(0.2126, 0.7152, 0.0722)) * vec3(0.2, intensity - quality * hash(time) * 0.1, 0.4);\n\t gl_FragColor = vec4(c, 1.0);\n}\n";
    private float paramIntensity = 30.0f;
    private float paramSize = 40.0f;
    private float paramSmoothness = 65.0f;

    private int intensityLocation;
    private int sizeLocation;
    private int smoothLocation;
    private int texelWidthLocation;
    private int texelHeightLocation;

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

    public GPUNightVisionFilter() {
        super(NO_FILTER_VERTEX_SHADER, FRAGMENT_SHADER);
    }

    @Override
    public void onInit() {
        super.onInit();
        intensityLocation = GLES20.glGetUniformLocation(getProgram(), "paramIntensity");
        sizeLocation = GLES20.glGetUniformLocation(getProgram(), "paramSize");
        smoothLocation = GLES20.glGetUniformLocation(getProgram(), "paramSmoothness");
        texelWidthLocation = GLES20.glGetUniformLocation(getProgram(), "texelWidth");
        texelHeightLocation = GLES20.glGetUniformLocation(getProgram(), "texelHeight");
    }

    @Override
    public void onDraw(int textureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
        super.onDraw(textureId, cubeBuffer, textureBuffer);
        setFloat(intensityLocation, paramIntensity);
        setFloat(sizeLocation, paramSize);
        setFloat(smoothLocation, paramSmoothness);
        setFloat(texelWidthLocation, 1f / getOutputWidth());
        setFloat(texelHeightLocation, 1f / getOutputHeight());
    }
}
