package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlBeveledFilter extends GlFilter {

    private static final String FRAGMENT_SHADER = "\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float paramSize;\nuniform float paramHorEdge;\nuniform float paramVerEdge;\nvoid main() {\n    float PI = 3.1415926535;\n    float hor = PI*(paramHorEdge / 100.0);\n    float ver = PI*(paramVerEdge / 100.0);\n    float size = 1.0 - (paramSize / 100.0)*0.8;\n    vec2 U = 2.0*vTextureCoord - 1.0;\n    float x = U.x;\n    float y = U.y;\n    float a = max(abs(x),abs(y));\n    float top = 1.0 - cos(ver);\n    float right = 1.0 - cos(11.0*hor);\n    float bottom = 1.0 - cos(11.0*ver);\n    float left = 1.0 - cos(hor);\n    float mult = a < size ? 1.0 :  x+y > 0. ? y > x ? top : right : y < x ? bottom : left;\n    gl_FragColor =  mult * texture2D(sTexture, vTextureCoord);\n}\n";
    private float paramHorEdge = 69.0f;
    private float paramSize = 40.0f;
    private float paramVerEdge = 31.0f;


    public GlBeveledFilter() {
        super("attribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = aPosition;\nvTextureCoord = aTextureCoord.xy;\n}\n", FRAGMENT_SHADER);
        filterName = "Beveled";
    }

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("paramSize"), this.paramSize);
        FilterUtilsKt.setFloat(getHandle("paramHorEdge"), this.paramHorEdge);
        FilterUtilsKt.setFloat(getHandle("paramVerEdge"), this.paramVerEdge);
    }

}
