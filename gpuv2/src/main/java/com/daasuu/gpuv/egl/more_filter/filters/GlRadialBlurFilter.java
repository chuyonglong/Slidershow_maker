package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlRadialBlurFilter extends GlFilter {

    private static final String FRAGMENT_SHADER = "\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float paramIntensity;\nvoid main() {\n    float intensity = 0.995 - (paramIntensity / 100.0)*0.03;\n    vec3 p = vec3(vTextureCoord.x, vTextureCoord.y, 1.0) - 0.5;\n    vec3 o = texture2D(sTexture, 0.5+(p.xy *= .988)).rbb;\n    for (float i=0.0 ; i<32.0 ; i++) {\n        p.z += pow(max(0.,0.21-length(texture2D(sTexture, 0.5+(p.xy *= intensity)).r)),2.)*exp(-i*.08);\n    }\n    gl_FragColor=vec4(o*o+p.z, 1.0);\n}\n";
    private float paramIntensity = 50.0f;


    public GlRadialBlurFilter() {
        super("attribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = aPosition;\nvTextureCoord = aTextureCoord.xy;\n}\n", FRAGMENT_SHADER);
        filterName = "Radial Blur";}

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
    }
}
