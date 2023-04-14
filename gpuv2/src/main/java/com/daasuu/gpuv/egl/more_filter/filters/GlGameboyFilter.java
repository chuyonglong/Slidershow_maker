package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlGameboyFilter extends GlFilter {

    private static final String FRAGMENT_SHADER = "\nprecision highp float;\n#define BRIGHTNESS 1.0\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float texelWidth;\nuniform float texelHeight;\nuniform float ditherMat[64];\nuniform float paramIntensity;\nvec3 find_closest (vec3 ref) {\n\tvec3 old = vec3 (100.0*255.0);\n\t#define TRY_COLOR(new) old = mix (new, old, step (length (old-ref), length (new-ref)));\n\tTRY_COLOR (vec3 (156.0, 189.0, 15.0));\n\tTRY_COLOR (vec3 (140.0, 173.0, 15.0));\n\tTRY_COLOR (vec3 (48.0, 98.0, 48.0));\n\tTRY_COLOR (vec3 (15.0, 56.0, 15.0));\n\treturn old ;\n}\nfloat dither_matrix (float x, float y) { return ditherMat[8*int(x)+int(y)]; }\nvec3 dither (vec3 color, vec2 uv) {\n\tcolor *= 255.0 * BRIGHTNESS;\n\tcolor += dither_matrix (mod (uv.x, 8.0), mod (uv.y, 8.0));\n\tcolor = find_closest(clamp(color, 0.0, 255.0));\n\treturn color / 255.0;\n}\nvoid main() {\n    float intensity = (paramIntensity / 100.0)*2.0;\n\tvec3 tc = texture2D(sTexture, vTextureCoord).rgb;\n    vec3 to = dither(tc, vTextureCoord/vec2(texelWidth, texelHeight));\n\tgl_FragColor = vec4(mix(tc, to, intensity), 1.0);\n}\n";
    private float paramIntensity = 50.0f;
    private float texelHeight;
    private float texelWidth;

    public GlGameboyFilter() {
        super("attribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = aPosition;\nvTextureCoord = aTextureCoord.xy;\n}\n", FRAGMENT_SHADER);
        filterName = "Game Boy";}

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("texelWidth"), this.texelWidth);
        FilterUtilsKt.setFloat(getHandle("texelHeight"), this.texelHeight);
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
    }

    public void setFrameSize(int i, int i2) {
        this.texelWidth = 1.0f / ((float) i);
        this.texelHeight = 1.0f / ((float) i2);
    }
}
