package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlLaplaceFilter extends GlFilter {

    private static final String FRAGMENT_SHADER = "\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float paramSmoothness;\nuniform float paramIntensity;\nvoid main() {\n    float intensity = paramIntensity / 100.0;\n    float smoothness = paramSmoothness / 100.0;\n    vec4 color = vec4(0.0);\n    vec4 ts = texture2D(sTexture, vTextureCoord);\n    float step = .01;\n    for (int i=0 ; i<5 ; ++i) {\n        for (int j=0 ; j<5 ; ++j) {\n            float mult = i*5 + j == 12 ? 24. : -1.;\n            color += mult * texture2D(sTexture, vTextureCoord + vec2(i-2, j-2)*step);\n        }\n    }\n    vec3 c = (color.r + color.g + color.b) / 3. < smoothness ? vec3(0.) : color.rgb;\n    gl_FragColor = vec4(mix(ts.rgb, c, intensity), 1.0);\n}\n";
    private float paramIntensity = 75.0f;
    private float paramSmoothness = 25.0f;


    public GlLaplaceFilter() {
        super("attribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = aPosition;\nvTextureCoord = aTextureCoord.xy;\n}\n", FRAGMENT_SHADER);
        filterName = "Laplace";}

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
        FilterUtilsKt.setFloat(getHandle("paramSmoothness"), this.paramSmoothness);
    }
}
