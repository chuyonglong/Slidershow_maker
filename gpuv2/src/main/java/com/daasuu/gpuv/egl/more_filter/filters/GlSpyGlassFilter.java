package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.more_filter.FilterTimable;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlSpyGlassFilter extends FilterTimable {

    private static final String FRAGMENT_SHADER = "\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float texelWidth;\nuniform float texelHeight;\nuniform float time;\nuniform int orientation;\nuniform float paramSize;\nvec2 rotate_point(vec2 p, float angle, vec2 centre) {\n    float s = sin(angle);\n    float c = cos(angle);\n    p -= centre;\n    p = vec2(p.x * c - p.y * s, p.x * s + p.y * c);\n    p += centre;\n    return p;\n}\n  void main() {\n      float size = 0.15 + (paramSize / 100.0)*0.6;\n      vec2 res = vec2(1.0/texelWidth, 1.0/texelHeight);\n      float GRAD_DIST = res.y * 0.15;\n      float REFRACT_DIST = res.y * 0.15;\n      float CLEAR_DIST = res.y * size * (orientation == 1 ? 0.85 : 1.0);\n      vec2 centre_to_frag = vTextureCoord*res-res/2.0;\n      float angle = atan(centre_to_frag.x / centre_to_frag.y);\n      float rdist = length(centre_to_frag);\n      float rdist_grad_lin = clamp((rdist-CLEAR_DIST)/GRAD_DIST, 0.0, 1.0);\n      float rdist_grad = pow(rdist_grad_lin, 1.4);\n      float refract_dist = clamp((rdist - (CLEAR_DIST - (REFRACT_DIST*0.03)))/(REFRACT_DIST * 0.5), 0.0, 1.0);\n      refract_dist = sin(refract_dist * 1.0 * 3.414);\n      refract_dist = pow(refract_dist, 3.0);\n      vec2 uv = rotate_point(vTextureCoord, -0.08 * refract_dist, vec2(0.5, 0.5));\n      float zoom = (1.0 + sin(time)) * 0.5;\n      uv = mix(uv * 0.8, uv * 0.6, zoom) * (orientation == 1 ? 1.5 : 1.0) + 0.1;\n      vec4 raw = mix(texture2D(sTexture, uv), vec4(0.0), rdist_grad);\n  \t   gl_FragColor = raw; //mix(raw, raw * ring_noise, rdist_grad_lin);\n}\n";
    private float paramSize = 20.0f;
    private float texelHeight;
    private float texelWidth;


    public GlSpyGlassFilter() {
        super(FRAGMENT_SHADER);filterName = "Spy Glass";
    }

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("texelWidth"), this.texelWidth);
        FilterUtilsKt.setFloat(getHandle("texelHeight"), this.texelHeight);
        FilterUtilsKt.setFloat(getHandle("paramSize"), this.paramSize);
    }

    public void setFrameSize(int i, int i2) {
        this.texelWidth = 1.0f / ((float) i);
        this.texelHeight = 1.0f / ((float) i2);
    }
}
