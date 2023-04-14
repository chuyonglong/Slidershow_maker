package com.library.acatapps.gpufilter.effect;

import android.opengl.GLES20;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

public class GPULowQualityFilter extends GPUImageFilter {
    private static final String FRAGMENT_SHADER = "\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float time;\nuniform float randoms[8];\n#define PI 3.14159265358979323846264\nconst vec3 grayMult = vec3(0.2125, 0.7154, 0.0721);\nuniform float paramIntensity;\nhighp float rand(vec2 co) {\n    highp float a = 12.9898;\n    highp float b = 78.233;\n    highp float c = 43758.5453;\n    highp float dt= dot(co.xy ,vec2(a,b));\n    highp float sn= mod(dt,3.14);\n    highp float r = fract(sin(sn) * c);\n    return r == 0.0 ? 0.0000001 : r;\n}\nfloat nrand( vec2 n ) {\n    return fract(sin(dot(n.xy, vec2(12.9898, 78.233)))* 43758.5453);\n}\nfloat n2rand( vec2 n ) {\n    float t = fract( time );\n    float nrnd0 = nrand( n + 0.07*t );\n    float nrnd1 = nrand( n + 0.11*t );\n    return (nrnd0+nrnd1) / 2.0;\n}\nvec4 gaussrand(vec2 co) {\n    float U, V, R, Z;\n    U = rand(co + vec2(randoms[0]));\n    V = rand(co + vec2(randoms[1]));\n    R = rand(co + vec2(randoms[2]));\n    Z = 0.001 * sqrt (-2.0 * log (U)) * cos (2.0 * PI * V);\n    Z = R < 0.5 ? sqrt(-2.0 * log(U)) * sin(2.0 * PI * V) : sqrt(-2.0 * log(U)) * cos(2.0 * PI * V);\n    Z = Z * 0.15 + 0.0;\n    return vec4(Z, Z, Z, 0.0);\n}\nvec3 mod289(vec3 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }\nvec2 mod289(vec2 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }\nvec3 permute(vec3 x) { return mod289(((x*34.0)+1.0)*x); }\nfloat snoise(vec2 v) {\n  const vec4 C = vec4(0.211324865405187, 0.366025403784439, -0.577350269189626, 0.024390243902439);  // (3.0-sqrt(3.0))/6.0, 0.5*(sqrt(3.0)-1.0), -1.0 + 2.0 * C.x, 1.0 / 41.0\n  vec2 i  = floor(v + dot(v, C.yy) );\n  vec2 x0 = v -   i + dot(i, C.xx);\n  vec2 i1 = (x0.x > x0.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);\n  vec4 x12 = x0.xyxy + C.xxzz;\n  x12.xy -= i1;\n  i = mod289(i);\n  vec3 p = permute( permute( i.y + vec3(0.0, i1.y, 1.0 )) + i.x + vec3(0.0, i1.x, 1.0 ));\n  vec3 m = max(0.5 - vec3(dot(x0,x0), dot(x12.xy,x12.xy), dot(x12.zw,x12.zw)), 0.0);\n  m = m*m ;\n  m = m*m ;\n  vec3 x = 2.0 * fract(p * C.www) - 1.0;\n  vec3 h = abs(x) - 0.5;\n  vec3 ox = floor(x + 0.5);\n  vec3 a0 = x - ox;\n  m *= 1.79284291400159 - 0.85373472095314 * ( a0*a0 + h*h );\n  vec3 g;\n  g.x  = a0.x  * x0.x  + h.x  * x0.y;\n  g.yz = a0.yz * x12.xz + h.yz * x12.yw;\n  return 130.0 * dot(m, g);\n}\nvoid main() {\n    float intensity = (paramIntensity / 100.0)*4.0;\n    vec4 a = texture2D(sTexture, vTextureCoord);\n    float noise = n2rand( vTextureCoord );\n    vec4 b = vec4(noise,noise,noise,1.0);\n    float lum = dot(a.rgb, grayMult);\n    vec4 r = lum < 0.5 ? 2.0*a*b : 1.0 - 2.0*(1.0-a)*(1.0-b);\n    gl_FragColor = vec4(mix(a.rgb, r.rgb, intensity), 1.0);\n}\n";
    private float paramIntensity = 25.0f;

    private int intensityLocation;

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

    public GPULowQualityFilter() {
        super(NO_FILTER_VERTEX_SHADER, FRAGMENT_SHADER);
    }

    @Override
    public void onInit() {
        super.onInit();
        intensityLocation = GLES20.glGetUniformLocation(getProgram(), "paramIntensity");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setFloat(intensityLocation , paramIntensity);
    }
}
