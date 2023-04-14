package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlCommodoreFilter  extends GlFilter {
    public static final String FRAGMENT_SHADER = "\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float texelWidth;\nuniform float texelHeight;\nconst int NUM_COLORS = 16;\nvec3 palette[NUM_COLORS];\nuniform float paramIntensity;\nuniform float paramQuality;\nvoid initPalette() {\n    #define RGB(r,g,b) vec3(r,g,b)/255.0\n    palette[0]  = RGB(0.,   0.,   0.);\n    palette[1]  = RGB(255., 255., 255.);\n    palette[2]  = RGB(116., 67.,  53.);\n    palette[3]  = RGB(124., 172., 186.);\n    palette[4]  = RGB(123., 72.,  144.);\n    palette[5]  = RGB(100., 151., 79.);\n    palette[6]  = RGB(64.,  50.,  133.);\n    palette[7]  = RGB(191., 205., 122.);\n    palette[8]  = RGB(123., 91.,  47.);\n    palette[9]  = RGB(79.,  69.,  0.);\n    palette[10] = RGB(163., 114., 101.);\n    palette[11] = RGB(80.,  80.,  80.);\n    palette[12] = RGB(120., 120., 120.);\n    palette[13] = RGB(164., 215., 142.);\n    palette[14] = RGB(120., 106., 189.);\n    palette[15] = RGB(159., 159., 150.);\n}\nvoid main() {\n    float intensity = (paramIntensity / 100.0)*1.5;\n    float quality = 0.1 + (paramQuality / 100.0)*0.9;\n    initPalette();\n    vec2 R = vec2(texelWidth, texelHeight);\n    vec4 ts = texture2D(sTexture, vec2(floor(vTextureCoord.x / R.x * quality), floor(vTextureCoord.y / R.y * quality)) / quality * R);\n    int ind = 0;\n    float minDist = distance(ts.rgb, palette[0]);\n    for(int i = 1; i < NUM_COLORS; ++i) {\n        float dist = distance(ts.rgb, palette[i]);\n        ind = dist < minDist ? i : ind;\n        minDist = min(dist, minDist);\n    }\n\tgl_FragColor = vec4(mix(ts.rgb, palette[ind], intensity), 1.0);\n}\n";
    private float paramIntensity = 60.0f;
    private float paramQuality = 50.0f;
    private float texelHeight;
    private float texelWidth;


    public GlCommodoreFilter() {
        super("attribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = aPosition;\nvTextureCoord = aTextureCoord.xy;\n}\n", FRAGMENT_SHADER);
        filterName = "Commodore";
    }

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("texelWidth"), this.texelWidth);
        FilterUtilsKt.setFloat(getHandle("texelHeight"), this.texelHeight);
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
        FilterUtilsKt.setFloat(getHandle("paramQuality"), this.paramQuality);
    }

    public void setFrameSize(int i, int i2) {
        this.texelWidth = 1.0f / ((float) i);
        this.texelHeight = 1.0f / ((float) i2);
    }
}
