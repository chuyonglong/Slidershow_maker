package com.library.acatapps.gpufilter.effect;

import android.opengl.GLES20;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

public class GPUWispFilter extends GPUImageFilter {

    private static final String FRAGMENT_SHADER = "\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\n\nuniform float time;\nuniform float paramIntensity;\nconst float cloudIntensity = 2.0;\nfloat clouds(vec2 uv) { return 0.6; }\nvoid main() {\n\t vec2 uv = vTextureCoord;\n     float lightIntensity = 0.001 + (paramIntensity / 100.0)*0.1;\n\t vec2 center = vec2(0.5, 0.5);\n\t vec2 light1 = vec2(sin(time*1.2+45.0)*1.0 + cos(time*0.4+32.0)*0.6, sin(time*1.2+99.0)*1.2 + cos(time*0.2-15.0)*-0.4)*0.25+center;\n\t vec3 lightColor1 = vec3(1.0, 0.25, 0.25);\n\t vec2 light2 = vec2(sin(time+2.0)*-1.7, cos(time+8.0)*1.0)*0.25+center;\n\t vec3 lightColor2 = vec3(0.25, 1.0, 0.25);\n\t vec2 light3 = vec2(sin(time+3.0)*1.6, cos(time+14.0)*-1.2)*0.25+center;\n\t vec3 lightColor3 = vec3(0.25, 0.25, 1.0);\n\t float cloudIntensity1 = 1.0 - (cloudIntensity*distance(uv, light1));\n\t float lightIntensity1 = lightIntensity / max(0.0001,distance(uv,light1));\n\t float cloudIntensity2 = 1.0 - (cloudIntensity*distance(uv, light2));\n\t float lightIntensity2 = lightIntensity / max(0.0001,distance(uv,light2));\n\t float cloudIntensity3 = 1.0 - (cloudIntensity*distance(uv, light3));\n\t float lightIntensity3 = lightIntensity / max(0.0001,distance(uv,light3));\n\t vec4 ts = texture2D(sTexture, uv);\n\t vec3 tl =  vec3(cloudIntensity1*clouds(uv))*lightColor1 + lightIntensity1*lightColor1 +\n\t\t\t\tvec3(cloudIntensity2*clouds(uv))*lightColor2 + lightIntensity2*lightColor2 +\n\t\t\t\tvec3(cloudIntensity3*clouds(uv))*lightColor3 + lightIntensity3*lightColor3;\n\t gl_FragColor = vec4(ts.rgb*tl.rgb, 1.0);\n}\n";
    private float paramIntensity = 25.0f;
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
    private int inTensityLocation;

    public GPUWispFilter() {
        super(NO_FILTER_VERTEX_SHADER, FRAGMENT_SHADER);
    }

    @Override
    public void onInit() {
        super.onInit();
        inTensityLocation = GLES20.glGetUniformLocation(getProgram(), "paramIntensity");
        localeTime = GLES20.glGetUniformLocation(getProgram(), "time");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setFloat(inTensityLocation, paramIntensity);
    }

    public void setIntensity(float value){
        setFloat(localeTime, value);
    }
}
