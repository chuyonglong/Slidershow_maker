package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlThermalFilter extends GlFilter {

    private static final String FRAGMENT_SHADER = "\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float paramIntensity;\nvoid main() {\n    float intensity = (paramIntensity / 100.0)*2.0;\n    vec4 pixcol = texture2D(sTexture, vTextureCoord);\n    vec3 color0 = vec3(0.,0.,1.);\n    vec3 color1 = vec3(1.,1.,0.);\n    vec3 color2 = vec3(1.,0.,0.);\n    float lum = (pixcol.r+pixcol.g+pixcol.b)/3.;\n    int ix = (lum < 0.5) ? 0 : 1;\n    vec3 tc = mix(ix == 0 ? color0 : color1, ix == 0 ? color1 : color2, (lum-float(ix)*0.5)/0.5);\n    gl_FragColor = vec4(mix(pixcol.rgb, tc, intensity), 1.0);\n}\n";
    private float paramIntensity = 50.0f;

    public GlThermalFilter() {
        super("attribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = aPosition;\nvTextureCoord = aTextureCoord.xy;\n}\n", FRAGMENT_SHADER);
        filterName = "Thermal";}

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
    }
}
