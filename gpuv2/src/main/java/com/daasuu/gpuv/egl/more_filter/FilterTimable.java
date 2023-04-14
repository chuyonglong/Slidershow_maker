package com.daasuu.gpuv.egl.more_filter;

import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.egl.more_filter.composer.Timable;

public class FilterTimable extends GlFilter implements Timable {
    private int localeTime;
    private float time;

    public FilterTimable( String str) {
        super("attribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = aPosition;\nvTextureCoord = aTextureCoord.xy;\n}\n", str);
    }

    public void setup() {
        super.setup();
        this.localeTime = getHandle(LocationConst.TIME);
    }

    public void onDraw() {
        super.onDraw();
        if (this.localeTime != 0) {
            setTime(this.time);
        }
    }

    public void setTime(float f) {
        this.time = f;
        int i = this.localeTime;
        if (i != 0) {
            FilterUtilsKt.setFloat(i, -f);
        }
    }

    public void release() {
        this.localeTime = 0;
        super.release();
    }
}
