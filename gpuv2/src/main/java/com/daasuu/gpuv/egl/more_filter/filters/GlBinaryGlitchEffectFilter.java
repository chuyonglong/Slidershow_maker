package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.more_filter.FilterTimable;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlBinaryGlitchEffectFilter extends FilterTimable {

    private static final String FRAGMENT_SHADER = "\nprecision mediump float;\nvarying highp vec2 vTextureCoord;\nuniform sampler2D sTexture;\n\nuniform float time;\n\nuniform float paramVolume;\nuniform vec2 paramDimen;\n\nvoid main( )\n{\n\tvec2 uv = vTextureCoord.xy / paramDimen.xy;\n//    uv.t = 1.0 - uv.t;\n\n    float x = uv.s;\n    float y = uv.t;\n    float tt=time;\n    float v=paramVolume;\n\n    //\n    float glitchStrength = (paramVolume+1.0) * (5.0+fract(tt)*0.2);\n\n    // get snapped position\n    float psize = 0.04 * glitchStrength;\n    float psq = 1.0 / psize;\n\n    float px = floor( x * psq + 0.5) * psize;\n    float py = floor( y * psq + 0.5) * psize;\n\n\tvec4 colSnap = texture2D( sTexture, vec2( px,py) );\n\n\tfloat lum = pow( 1.0 - (colSnap.r + colSnap.g + colSnap.b) / 3.0, glitchStrength ); // remove the minus one if you want to invert luma\n\n\n\n    // do move with lum as multiplying factor\n    float qsize = psize * lum;\n\n    float qsq = 1.0 / qsize;\n\n    float qx = floor( x * qsq + 0.5) * qsize;\n    float qy = floor( y * qsq + 0.5) * qsize;\n\n    float rx = (px - qx) * lum + x;\n    float ry = (py - qy) * lum + y;\n\n\tvec4 colMove = texture2D( sTexture, vec2( rx,ry) );\n\n\n    // final color\n    gl_FragColor = colMove;\n}\n";
    private float[] paramDimen = {1.0f, 1.0f};
    private float paramVolume = 0.1f;


    public GlBinaryGlitchEffectFilter() {
        super(FRAGMENT_SHADER);
        filterName = "Glitch";
    }

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("paramVolume"), this.paramVolume);
        FilterUtilsKt.setFloatVec2(getHandle("paramDimen"), this.paramDimen);
    }
}
