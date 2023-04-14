package com.library.acatapps.gpufilter.effect;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

public class GPUOldMovieFilter extends GPUImageFilter {
    private static final String FRAGMENT_SHADER = "\nprecision highp float;\n#define FLICKERING_SIZE 8\n#define BLOTCHING_SIZE 6\n#define RANDOMS_SIZE 6\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float texelWidth;\nuniform float texelHeight;\nuniform float time;\nuniform float oldmovie_flickering_a[FLICKERING_SIZE];\nuniform float oldmovie_flickering_b[FLICKERING_SIZE];\nuniform float oldmovie_flickering_c[FLICKERING_SIZE];\nuniform float oldmovie_flickering_mu[FLICKERING_SIZE];\nuniform float oldmovie_blotching_x[BLOTCHING_SIZE];\nuniform float oldmovie_blotching_y[BLOTCHING_SIZE];\nuniform float oldmovie_blotching_s[BLOTCHING_SIZE];\nuniform float oldmovie_randoms[RANDOMS_SIZE];\nconst bool BLACK_AND_WHITE = true;\nconst bool GRAIN = true;\nuniform float paramIntensity;\nfloat rand(vec2 co) { return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453); }\nfloat rand(float c) { return rand(vec2(c,1.0)); }\nfloat randomLine(int i) {\n   float a = oldmovie_flickering_a[i];\n   float b = oldmovie_flickering_b[i];\n   float c = oldmovie_flickering_c[i];\n   float mu = oldmovie_flickering_mu[i];\n\tfloat l = mu > 0.2 ? pow(abs(a*vTextureCoord.x + b*vTextureCoord.y + c), 1.0/8.0) : 2.0-pow(abs(a*vTextureCoord.x + b*vTextureCoord.y + c), 1.0/8.0);\n\treturn mix(0.5, 1.0, l);\n}\nfloat randomBlotch(int i) {\n\tfloat x = oldmovie_blotching_x[i];\n\tfloat y = oldmovie_blotching_y[i];\n\tfloat s = oldmovie_blotching_s[i];\n\tvec2 p = vec2(x,y) - vTextureCoord;\n\tp.x *= texelHeight / texelWidth;\n\tfloat a = atan(p.y,p.x);\n\tfloat ss = s*s * (sin(6.2831*a*x)*0.1 + 1.0);\n\tfloat v = dot(p,p) < ss ? 0.2 : pow(dot(p,p) - ss, 1.0/16.0);\n\treturn mix(0.3 + 0.2 * (1.0 - (s / 0.02)), 1.0, v);\n}\nvoid main() {\n   float intensity = (paramIntensity / 100.0)*1.2;\n\tvec2 uv = vTextureCoord;\n\tfloat t = float(int(time * 15.0));\n\tvec2 suv = uv + 0.002 * vec2(oldmovie_randoms[0], oldmovie_randoms[1]);\n\tvec3 image = texture2D(sTexture, vec2(suv.x, suv.y) ).xyz;\n\tvec3 oldImage = BLACK_AND_WHITE ? dot(vec3(0.2126, 0.7152, 0.0722), image)*vec3(0.7) : image;\n\tfloat vI = 16.0 * (uv.x * (1.0-uv.x) * uv.y * (1.0-uv.y)) * mix( 0.7, 1.0, oldmovie_randoms[2]);\n\tvI += 1.0 + 0.4 * oldmovie_randoms[3];\n\tvI *= pow(16.0 * uv.x * (1.0-uv.x) * uv.y * (1.0-uv.y), 0.4);\n\t\tint l = int(oldmovie_randoms[4]);\n\t\tvI *= 0 < l ? randomLine(0) : 1.0;\n\t\tvI *= 1 < l ? randomLine(1) : 1.0;\n\t\tvI *= 2 < l ? randomLine(2) : 1.0;\n\t\tvI *= 3 < l ? randomLine(3) : 1.0;\n\t\tvI *= 4 < l ? randomLine(4) : 1.0;\n\t\tvI *= 5 < l ? randomLine(5) : 1.0;\n\t\tvI *= 6 < l ? randomLine(6) : 1.0;\n\t\tvI *= 7 < l ? randomLine(7) : 1.0;\n\t\tint s = int(oldmovie_randoms[5]);\n\t\tvI *= 0 < s ? randomBlotch(0) : 1.0;\n\t\tvI *= 1 < s ? randomBlotch(1) : 1.0;\n\t\tvI *= 2 < s ? randomBlotch(2) : 1.0;\n\t\tvI *= 3 < s ? randomBlotch(3) : 1.0;\n\t\tvI *= 4 < s ? randomBlotch(4) : 1.0;\n\t\tvI *= 5 < s ? randomBlotch(5) : 1.0;\n    vec3 tc = vec3(oldImage * vI * (GRAIN ? (1.0+(rand(uv+t*.01)-.2)*.15) : 1.0));\n    gl_FragColor = vec4(mix(image, tc, intensity), 1.0);\n}\n";
    private float paramIntensity = 80.0f;

    private int intensityLocation;
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

    public GPUOldMovieFilter() {
        super(NO_FILTER_VERTEX_SHADER, FRAGMENT_SHADER);
    }

    @Override
    public void onInit() {
        super.onInit();
        intensityLocation = GLES20.glGetUniformLocation(getProgram(), "paramIntensity");
        texelWidthLocation = GLES20.glGetUniformLocation(getProgram(), "texelWidth");
        texelHeightLocation = GLES20.glGetUniformLocation(getProgram(), "texelHeight");
    }

    @Override
    public void onDraw(int textureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
        super.onDraw(textureId, cubeBuffer, textureBuffer);
        setFloat(intensityLocation, paramIntensity);
        setFloat(texelWidthLocation, 1f / getOutputWidth());
        setFloat(texelHeightLocation, 1f / getOutputHeight());
    }
}
