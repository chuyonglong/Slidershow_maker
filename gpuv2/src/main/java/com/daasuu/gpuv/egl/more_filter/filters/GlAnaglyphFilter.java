package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.filter.GlFilter;
import android.opengl.GLES20;

public class GlAnaglyphFilter extends GlFilter {

    private static final float ASPECT = 54.0f;
    private static final float DEFAULT_OFFSET = 0.018518519f;
    private static final String FRAGMENT_SHADER = "precision highp float;" +
            "uniform lowp sampler2D sTexture;" +
            "varying highp vec2 gbCoordinate;" +
            "varying highp vec2 rCoordinate;" +
            "void main() {" +
            "gl_FragColor = vec4(texture2D(sTexture, rCoordinate).r, texture2D(sTexture, gbCoordinate).gb, 1.0);" +
            "}";
    private static final String VERTEX_SHADER = "attribute vec4 aPosition;" +
            "attribute vec4 aTextureCoord;" +
            "varying highp vec2 gbCoordinate;" +
            "varying highp vec2 rCoordinate;" +
            "uniform float imageWidthFactor;" +
            "uniform float imageHeightFactor;" +
            "void main() {" +
            "gl_Position = aPosition;" +
            "mediump vec2 offset = vec2( -imageWidthFactor, imageHeightFactor);" +
            "gbCoordinate = aTextureCoord.xy;" +
            "rCoordinate = aTextureCoord.xy + offset;" +
            "}";
    private float imageHeightFactor;
    private float imageWidthFactor;



    public GlAnaglyphFilter() {
        this(0.0f, 0.0f, 3);
        filterName = "Anaglyph";
    }

    public final float getImageWidthFactor() {
        return this.imageWidthFactor;
    }

    public final void setImageWidthFactor(float f) {
        this.imageWidthFactor = f;
    }

    public GlAnaglyphFilter(float f, float f2) {
        super(VERTEX_SHADER, FRAGMENT_SHADER);
        this.imageWidthFactor = f;
        this.imageHeightFactor = f2;
    }

    public GlAnaglyphFilter(float f, float f2, int i) {
        this(f, f2);
        if ((i & 1) != 0) {
            f = DEFAULT_OFFSET;
        }
        if ((i & 2) != 0) {
            f2 = DEFAULT_OFFSET;
        }

    }

    public final float getImageHeightFactor() {
        return this.imageHeightFactor;
    }

    public final void setImageHeightFactor(float f) {
        this.imageHeightFactor = f;
    }

    public void setFrameSize(int i, int i2) {
        this.imageWidthFactor = (((float) i2) / ASPECT) / ((float) i);
        this.imageHeightFactor = DEFAULT_OFFSET;
    }

    @Override
    public void onDraw() {
        GLES20.glUniform1f(getHandle("imageWidthFactor"), this.imageWidthFactor);
        GLES20.glUniform1f(getHandle("imageHeightFactor"), this.imageHeightFactor);
    }



}
