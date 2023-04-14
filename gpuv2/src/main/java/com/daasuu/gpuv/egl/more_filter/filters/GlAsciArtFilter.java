package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlAsciArtFilter extends GlFilter {

    private static final String FRAGMENT_SHADER = "\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float texelWidth;\nuniform float texelHeight;\nuniform float paramIntensity;\nuniform float paramSize;\nuniform float paramQuality;\nfloat character(float n, vec2 p) {\n    p = floor(p*vec2(4.0, -4.0) + 2.5);\n    return clamp(p.x, 0.0, 4.0) == p.x && clamp(p.y, 0.0, 4.0) == p.y && int(mod(n/exp2(p.x + 5.0*p.y), 2.0)) == 1 ? 1.0 : 0.0;\n}\nvoid main() {\n    float intensity = (paramIntensity / 100.0)*1.5;\n    float size = 3.0 + (paramSize / 100.0)*10.0;\n    float quality = 8.0 - (paramQuality / 100.0)*7.0;\n    vec2 R = vec2(texelWidth, texelHeight);\n    vec2 UV = vTextureCoord / R;\n    vec3 ts = texture2D(sTexture, floor(UV/quality)*quality*R).rgb;\n    float gray = dot(ts, vec3(0.3, 0.59, 0.11));\n    float n =  4096.0;\n    n = gray > 0.2 ? 65600.0 : n;\n    n = gray > 0.3 ? 332772.0 : n;\n    n = gray > 0.4 ? 15255086.0 : n;\n    n = gray > 0.5 ? 23385164.0 : n;\n    n = gray > 0.6 ? 15252014.0 : n;\n    n = gray > 0.7 ? 13199452.0 : n;\n    n = gray > 0.8 ? 11512810.0 : n;\n    vec2 p = mod(UV/size, 2.0) - vec2(1.0);\n    vec3 to = ts*character(n, p);\n    gl_FragColor = vec4(mix(ts, to, intensity), 1.0);\n}\n";
    private float paramIntensity = 65.0f;
    private float paramQuality = 50.0f;
    private float paramSize = 30.0f;
    private float texelHeight;
    private float texelWidth;


    public GlAsciArtFilter() {
        super("attribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = aPosition;\nvTextureCoord = aTextureCoord.xy;\n}\n", FRAGMENT_SHADER);
        filterName = "Asci Art";

    }

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("texelWidth"), this.texelWidth);
        FilterUtilsKt.setFloat(getHandle("texelHeight"), this.texelHeight);
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
        FilterUtilsKt.setFloat(getHandle("paramSize"), this.paramSize);
        FilterUtilsKt.setFloat(getHandle("paramQuality"), this.paramQuality);
    }

    public void setFrameSize(int i, int i2) {
        this.texelWidth = 1.0f / ((float) i);
        this.texelHeight = 1.0f / ((float) i2);
    }
}
