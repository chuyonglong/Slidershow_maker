package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlBlackBodyFilter extends GlFilter {

    public static final String FRAGMENT_SHADER = "\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float paramIntensity;\nvec3 blackBody(float t) {\n    float u = (0.860117757 + 1.54118254e-4*t + 1.28641212e-7*t*t) / (1.0 + 8.42420235e-4*t + 7.08145163e-7*t*t);\n    float v = (0.317398726 + 4.22806245e-5*t + 4.20481691e-8*t*t) / (1.0 - 2.89741816e-5*t + 1.61456053e-7*t*t);\n    float x = 3.0 * u / (2.0 * u - 8.0 * v + 4.0);\n    float y = 2.0 * v / (2.0 * u - 8.0 * v + 4.0);\n    float z = 1.0 - x - y;\n    float Y = 1.0;\n    float X = (Y/y) * x;\n    float Z = (Y/y) * z;\n    mat3 XYZtosRGB = mat3(\n        3.2404542,-1.5371385,-0.4985314,\n        -0.9692660, 1.8760108, 0.0415560,\n        0.0556434,-0.2040259, 1.0572252);\n    vec3 RGB = vec3(X,Y,Z) * XYZtosRGB;\n    return RGB * pow(0.0004*t, 4.0);\n}\nvoid main() {\n    float tempScale = 2000.0 + (paramIntensity / 100.0)*4000.0;\n    float lum = dot(texture2D(sTexture, vTextureCoord).rgb, vec3(0.2126, 0.7152, 0.0722));\n    gl_FragColor = vec4(blackBody(lum * tempScale), 1.0);\n}\n";
    private float paramIntensity = 50.0f;




    public GlBlackBodyFilter() {
        super("attribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = aPosition;\nvTextureCoord = aTextureCoord.xy;\n}\n", FRAGMENT_SHADER);
        filterName = "Black Body";
    }

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
    }
}
