package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.more_filter.FilterTimable;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlRainFilter extends FilterTimable {

    private static final String FRAGMENT_SHADER = "\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float time;\nuniform float texelWidth;\nuniform float texelHeight;\nuniform float paramIntensity;\nuniform float paramSpeed;\nfloat N11(float t) { return fract(sin(t*12445.56)*7668.76); }\nvec3 N13(float p) {\n    vec3 p3 = fract(vec3(p) * vec3(.1152,.9378,.12586));\n    p3 += dot(p3, p3.yzx + 19.16);\n    return fract(vec3((p3.x + p3.y)*p3.z, (p3.x+p3.z)*p3.y, (p3.y+p3.z)*p3.x));\n}\nvec2 DropLayer2(vec2 uv, float t) {\n    vec2 UV = uv;\n    uv.y += t*0.74;\n    vec2 a = vec2(6.1, 1.);\n    vec2 grid = a*2.;\n    vec2 id = floor(uv*grid);\n    float colShift = N11(id.x);\n    uv.y += colShift;\n    id = floor(uv*grid);\n    vec3 n = N13(id.x*35.8+id.y*2323.2);\n    vec2 st = fract(uv*grid)-vec2(.5, 0);\n    float x = n.x-.5;\n    float y = UV.y*20.1;\n    float wiggle = sin(y+sin(y));\n    x += wiggle*(.5-abs(x))*(n.z-.5)*0.72;\n    float ti = fract(t+n.z);\n    y = ((smoothstep(0., .84, ti) * smoothstep(1., 0.84, ti))-.5)*.9+.5;\n    vec2 p = vec2(x, y);\n    float d = length((st-p)*a.yx);\n    float mainDrop = smoothstep(.4, .0, d);\n    float r = sqrt(smoothstep(1., y, st.y));\n    float cd = abs(st.x-x);\n    float trail = smoothstep(.22*r, .16*r*r, cd);\n    float trailFront = smoothstep(-.021, .021, st.y-y);\n    trail *= trailFront*r*r;\n    y = UV.y;\n    float trail2 = smoothstep(.2*r, .0, cd);\n    float droplets = max(0., (sin(y*(1.-y)*110.)-st.y))*trail2*trailFront*n.z;\n    y = fract(y*10.)+(st.y-.5);\n    float dd = length(st-vec2(x, y));\n    droplets = smoothstep(.3, 0., dd);\n    float m = mainDrop+droplets*r*trailFront;\n    return vec2(m, trail);\n}\nfloat StaticDrops(vec2 uv, float t) {\n    uv *= 40.;\n    vec2 id = floor(uv);\n    uv = fract(uv)-.5;\n    vec3 n = N13(id.x*106.34+id.y*3421.456);\n    vec2 p = (n.xy-.5)*.71;\n    float d = length(uv-p);\n    float fade = smoothstep(0., .025, fract(t+n.z)) * smoothstep(1., 0.025, fract(t+n.z));\n    float c = smoothstep(.3, 0., d)*fract(n.z*10.)*fade;\n    return c;\n}\nvec2 Drops(vec2 uv, float t, float l0, float l1, float l2) {\n    float s = StaticDrops(uv, t)*l0;\n    vec2 m1 = DropLayer2(uv, -t)*l1;\n    vec2 m2 = DropLayer2(uv*1.8, -t)*l2;\n    float c = s+m1.x+m2.x;\n    c = smoothstep(.3, 1., c);\n    return vec2(c, max(m1.y*l0, m2.y*l1));\n}\nvoid main() {\n    float intensity = paramIntensity / 100.0;\n    float speed = 0.5 + (paramSpeed / 100.0)*5.5;\n    vec2 uv = (vTextureCoord - 0.5);\n    uv.x = uv.x / texelWidth * texelHeight;\n    vec2 UV = vTextureCoord;\n    vec3 M = vec3(0.5, 0.5, 1.0);\n    float T = speed*time+M.x*2.;\n    float t = T*.2;\n    float rainAmount = sin(T*.05)*0.3 + intensity;\n    float staticDrops = smoothstep(-.5, 1., rainAmount)*2.;\n    float layer1 = smoothstep(.25, .75, rainAmount);\n    float layer2 = smoothstep(.0, .5, rainAmount);\n    vec2 c = Drops(uv, t, staticDrops, layer1, layer2);\n    vec2 e = vec2(.001, 0.);\n    float cx = Drops(uv+e, t, staticDrops, layer1, layer2).x;\n    float cy = Drops(uv+e.yx, t, staticDrops, layer1, layer2).x;\n    vec2 n = vec2(cx-c.x, cy-c.x);\n    gl_FragColor = vec4(texture2D(sTexture, UV+n).rgb, 1.);\n}\n";
    private float paramIntensity = 70.0f;
    private float paramSpeed = 20.0f;
    private float texelHeight;
    private float texelWidth;


    public GlRainFilter() {
        super(FRAGMENT_SHADER);
        filterName = "Rain";
    }

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("texelWidth"), this.texelWidth);
        FilterUtilsKt.setFloat(getHandle("texelHeight"), this.texelHeight);
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
        FilterUtilsKt.setFloat(getHandle("paramSpeed"), this.paramSpeed);
    }

    public void setFrameSize(int i, int i2) {
        this.texelWidth = 1.0f / ((float) i);
        this.texelHeight = 1.0f / ((float) i2);
    }
}
