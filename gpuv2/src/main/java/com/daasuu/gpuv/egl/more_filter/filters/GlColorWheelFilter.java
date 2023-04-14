package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.more_filter.FilterTimable;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlColorWheelFilter extends FilterTimable {

    public static final String FRAGMENT_SHADER = "\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float time;\nuniform float paramSpeed;\nvoid main() {\n    vec2 uv = vTextureCoord;\n    float speed = 1.0 + (paramSpeed / 100.0)*8.0;\n    float c = cos(time*speed);\n    float s = sin(time*speed);\n    mat4 hueRotation =\n    \tmat4(0.299,  0.587,  0.114, 0.0,\n    \t\t 0.299,  0.587,  0.114, 0.0,\n    \t\t 0.299,  0.587,  0.114, 0.0,\n    \t\t 0.000,  0.000,  0.000, 1.0) +\n    \tmat4(0.701, -0.587, -0.114, 0.0,\n    \t\t -0.299,  0.413, -0.114, 0.0,\n    \t\t -0.300, -0.588,  0.886, 0.0,\n    \t\t 0.000,  0.000,  0.000, 0.0) * c +\n    \tmat4(0.168,  0.330, -0.497, 0.0,\n    \t\t -0.328,  0.035,  0.292, 0.0,\n    \t\t 1.250, -1.050, -0.203, 0.0,\n    \t\t 0.000,  0.000,  0.000, 0.0) * s;\n    gl_FragColor = vec4((texture2D(sTexture, uv) * hueRotation).rgb, 1.0);\n}\n";
    private float speed = 50.0f;


    public GlColorWheelFilter() {
        super(FRAGMENT_SHADER);
        filterName = "Color Wheel";
    }

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("paramSpeed"), this.speed);
    }

}
