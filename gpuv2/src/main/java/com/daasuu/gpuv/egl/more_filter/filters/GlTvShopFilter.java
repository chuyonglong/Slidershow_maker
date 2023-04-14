package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;
import com.daasuu.gpuv.egl.more_filter.Orientation;

public class GlTvShopFilter extends GlFilter {

    private static final String FRAGMENT_SHADER = "\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform int orientation;\nvoid main() {\n    gl_FragColor = texture2D(sTexture, fract(vTextureCoord*exp2(ceil(-log2(orientation == 1 ? vTextureCoord.x : vTextureCoord.y)))));\n}";
    private int orientation;


    public GlTvShopFilter() {
        super("attribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = aPosition;\nvTextureCoord = aTextureCoord.xy;\n}\n", FRAGMENT_SHADER);
        filterName = "Tv Show";
    }

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setInteger(getHandle("orientation"), this.orientation);
    }

    public void setFrameSize(int i, int i2) {
        this.orientation = Orientation.INSTANCE.getOrientation(i, i2);
    }

}
