package com.library.acatapps.gpufilter.effect;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

public class GPUFireFilter extends GPUImageFilter {
    private static final String FRAGMENT_SHADER = "\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float texelWidth;\nuniform float texelHeight;\nuniform float time;\nuniform int orientation;\nconst float shift = 1.6;\nuniform float paramIntensity;\nuniform float paramQuality;\nuniform float paramSpeed;\nint quality;\nfloat rand(vec2 n) {\n    return fract(cos(dot(n, vec2(12.9898, 4.1414))) * 43758.5453);\n}\nfloat noise(vec2 n) {\n    const vec2 d = vec2(0.0, 1.0);\n    vec2 b = floor(n), f = smoothstep(vec2(0.0), vec2(1.0), fract(n));\n    return mix(mix(rand(b), rand(b + d.yx), f.x), mix(rand(b + d.xy), rand(b + d.yy), f.x), f.y);\n}\nfloat fbm(vec2 n) {\n    float total = 0.0, amplitude = 1.0;\n    for (int i = 0; i < quality; i++) {\n        total += noise(n) * amplitude;\n        n += n;\n        amplitude *= 0.5;\n    }\n    return total;\n}\nvoid main() {\n    float intensity = (paramIntensity / 100.0)*1.0;\n    quality = 2 + int(paramQuality / 100.0 * 6.0);\n    vec2 speed = vec2(1.05, 0.6) * (0.25 + (paramSpeed / 100.0)*3.0);\n    const vec3 c1 = vec3(0.5, 0.0, 0.1);\n    const vec3 c2 = vec3(0.9, 0.0, 0.0);\n    const vec3 c3 = vec3(0.2, 0.0, 0.0);\n    const vec3 c4 = vec3(1.0, 0.9, 0.0);\n    const vec3 c5 = vec3(0.1);\n    const vec3 c6 = vec3(0.9);\n    vec2 p = vec2(8.0*vTextureCoord.x, 8.0*vTextureCoord.y/texelHeight*texelWidth);\n    float q = fbm(p - time * 0.1);\n    vec2 r = vec2(fbm(p + q + time * speed.x - p.x - p.y), fbm(p + q - time * speed.y));\n    vec3 c = mix(c1, c2, fbm(p + r)) + mix(c3, c4, r.x) - mix(c5, c6, r.y);\n    vec4 fire = vec4(c * cos(shift * (orientation > 0 ? vTextureCoord.x : vTextureCoord.y)), 1.0);\n    gl_FragColor = mix(texture2D(sTexture, vTextureCoord), fire, intensity);\n}";
    private int orientation;
    private float paramIntensity = 50.0f;
    private float paramQuality = 20.0f;
    private float paramSpeed = 25.0f;

    private int intensityLocation;
    private int speedLocation;
    private int texelWidthLocation;
    private int texelHeightLocation;
    private int orientationLocation;

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

    public GPUFireFilter() {
        super(NO_FILTER_VERTEX_SHADER, FRAGMENT_SHADER);
    }

    @Override
    public void onInit() {
        super.onInit();
        intensityLocation = GLES20.glGetUniformLocation(getProgram(), "paramIntensity");
        speedLocation = GLES20.glGetUniformLocation(getProgram(), "paramSpeed");
        texelWidthLocation = GLES20.glGetUniformLocation(getProgram(), "texelWidth");
        texelHeightLocation = GLES20.glGetUniformLocation(getProgram(), "texelHeight");
        localeTime = GLES20.glGetUniformLocation(getProgram(), "time");
        orientationLocation = GLES20.glGetUniformLocation(getProgram(), "orientation");
    }

    @Override
    public void onDraw(int textureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
        super.onDraw(textureId, cubeBuffer, textureBuffer);
        setFloat(texelWidthLocation, 1f / getOutputWidth());
        setFloat(texelHeightLocation, 1f / getOutputHeight());
        setFloat(intensityLocation, paramIntensity);
        setFloat(speedLocation, paramSpeed);
        setFloat(orientationLocation, getOutputWidth() > getOutputHeight() ? 0 : 1);
    }

    public void setIntensity(float value) {
        setFloat(localeTime, value);
    }
}
