package com.library.acatapps.gpufilter.effect;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

public class GPUTilesFilter extends GPUImageFilter {
    public static final String FRAGMENT_SHADER = "\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nconst vec3 edgeColor = vec3(0.7);\nconst float threshhold = 0.15;\nuniform float texelWidth;\nuniform float paramIntensity;\nvec2 fmod(vec2 a, vec2 b) { return abs(fract(abs(a / b)) * abs(b)); }\nvoid main() {\n    float numTiles = 30.0 + (paramIntensity * texelWidth * 500.0);\n    vec2 uv = vTextureCoord;\n    float size = 1.0 / numTiles;\n    vec2 pBase = uv - fmod(uv, vec2(size));\n    vec2 pCenter = pBase + vec2(size / 2.0);\n    vec2 st = (uv - pBase) / size;\n    vec4 invOff = vec4((1.0 - edgeColor), 1.0);\n    float threshholdB = 1.0 - threshhold;\n    vec4 c1 = st.x > st.y ? invOff : vec4(0);\n    vec4 cBottom = st.x > threshholdB || st.y > threshholdB ? c1 : vec4(0);\n    c1 = st.x > st.y ? invOff : vec4(0);\n    vec4 cTop = st.x < threshhold || st.y < threshhold ? c1 : vec4(0);\n    vec4 tileColor = texture2D(sTexture, pCenter);\n    gl_FragColor = tileColor + cTop - cBottom;\n}";
    private float paramIntensity = 40.0f;

    private int intensityLocation;
    private int texelWidthLocation;

    public GPUTilesFilter() {
        super("attribute vec4 position;\nattribute vec4 inputTextureCoordinate;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = position;\nvTextureCoord = inputTextureCoordinate.xy;\n}\n", FRAGMENT_SHADER);
    }

    @Override
    public void onInit() {
        super.onInit();
        intensityLocation = GLES20.glGetUniformLocation(getProgram(), "paramIntensity");
        texelWidthLocation = GLES20.glGetUniformLocation(getProgram(), "texelWidth");
    }

    @Override
    public void onDraw(int textureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
        super.onDraw(textureId, cubeBuffer, textureBuffer);
        setFloat(intensityLocation, paramIntensity);
        setFloat(texelWidthLocation, 1f / getOutputWidth());
    }
}
