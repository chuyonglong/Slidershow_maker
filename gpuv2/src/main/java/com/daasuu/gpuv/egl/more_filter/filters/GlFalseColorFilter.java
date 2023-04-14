package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlFalseColorFilter extends GlFilter {

    private static final String FALSE_COLOR_FRAGMENT_SHADER = "\nprecision lowp float;\nvarying highp vec2 vTextureCoord;\n\nuniform sampler2D sTexture;\nuniform float intensity;\nuniform vec3 firstColor;\nuniform vec3 secondColor;\n\nconst mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);\n\nvoid main()\n{\n    lowp vec4 textureColor = texture2D(sTexture, vTextureCoord);\n    float luminance = dot(textureColor.rgb, luminanceWeighting);\n\n    gl_FragColor = vec4( mix(firstColor.rgb, secondColor.rgb, luminance), textureColor.a);\n}\n";
    private final float[] firstColor;
    private final float[] secondColor;

    public GlFalseColorFilter() {
        this(0.0f, 0.0f, 0.5f, 1.0f, 0.0f, 0.0f);
    }

    public GlFalseColorFilter(float f, float f2, float f3, float f4, float f5, float f6) {
        super("attribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = aPosition;\nvTextureCoord = aTextureCoord.xy;\n}\n", FALSE_COLOR_FRAGMENT_SHADER);
        this.firstColor = new float[]{f, f2, f3};
        this.secondColor = new float[]{f4, f5, f6};
        filterName = "False Color";
    }

    public void onDraw() {
        FilterUtilsKt.setFloatVec3(getHandle("firstColor"), this.firstColor);
        FilterUtilsKt.setFloatVec3(getHandle("secondColor"), this.secondColor);
    }
}
