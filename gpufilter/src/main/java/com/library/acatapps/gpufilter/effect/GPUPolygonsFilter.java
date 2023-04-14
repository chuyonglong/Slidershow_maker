package com.library.acatapps.gpufilter.effect;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

public class GPUPolygonsFilter extends GPUImageFilter {
    public static final String FRAGMENT_SHADER = "\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\n#define PI 3.14159265359\nuniform float time;\nuniform float texelWidth;\nuniform float texelHeight;\nuniform float paramIntensity;\nuniform float paramSpeed;\nvec2 random2( vec2 p ) { return fract(sin(vec2(dot(p,vec2(127.1,311.7)),dot(p,vec2(269.5,183.3))))*43758.5453); }\nvoid main() {\n    float intensity = 0.5 + (paramIntensity / 100.0)*3.0;\n    float speed = 0.25 + (paramSpeed / 100.0)*6.0;\n    float screenRatio = texelHeight/texelWidth;\n    float largerScreenDimSize = max(1.0/texelWidth, 1.0/texelHeight);\n    vec2 st = vTextureCoord * vec2(screenRatio, 1.0);\n    float pSize = 5.0;\n    float scale = intensity*20.0;\n    st *= scale;\n    vec2 stInt = floor(st);\n    vec2 stFract = fract(st);\n    float minDist = 100.0;\n    vec2 quad;\n    for (int j=-1; j <= 1; j++ ) {\n        for (int i=-1; i<=1; i++ ) {\n            vec2 neighbor = vec2(float(i),float(j));\n            vec2 point = 0.5 + 0.5*sin(speed*time + 2.0*PI*random2(stInt + neighbor));\n            float dist = length(neighbor + point - stFract);\n            quad = dist <= minDist ? neighbor : quad;\n            minDist = dist <= minDist ? dist : minDist;\n        }\n    }\n    vec2 midPoint = (stInt + 0.5 + quad) / (vec2(screenRatio, 1.0)*scale);\n    vec2 m = mod(vec2(vTextureCoord.x / texelWidth, vTextureCoord.y / texelHeight), pSize) / vec2(largerScreenDimSize);\n    vec3 tc1 = texture2D(sTexture, midPoint + m).rgb;\n    vec3 tc2 = texture2D(sTexture, midPoint + pSize/largerScreenDimSize - m).rgb;\n    gl_FragColor = vec4(mix(tc1, tc2, 0.5), 1.0);\n}\n";
    private float paramIntensity = 30.0f;
    private float paramSpeed = 30.0f;

    private int intensityLocation;
    private int speedLocation;
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

    public GPUPolygonsFilter() {
        super(NO_FILTER_VERTEX_SHADER, FRAGMENT_SHADER);
    }

    @Override
    public void onInit() {
        super.onInit();
        intensityLocation = GLES20.glGetUniformLocation(getProgram(), "paramIntensity");
        texelWidthLocation = GLES20.glGetUniformLocation(getProgram(), "texelWidth");
        texelHeightLocation = GLES20.glGetUniformLocation(getProgram(), "texelHeight");
        speedLocation = GLES20.glGetUniformLocation(getProgram(), "paramSpeed");
    }

    @Override
    public void onDraw(int textureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
        super.onDraw(textureId, cubeBuffer, textureBuffer);
        setFloat(intensityLocation, paramIntensity);
        setFloat(speedLocation, paramSpeed);
        setFloat(texelWidthLocation, 1f / getOutputWidth());
        setFloat(texelHeightLocation, 1f / getOutputHeight());
    }
}
