package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.more_filter.FilterTimable;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;
import com.daasuu.gpuv.egl.more_filter.Orientation;

public class GlCrtFilter extends FilterTimable {

    public static final String FRAGMENT_SHADER = "\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float time;\nuniform int orientation;\nuniform float paramIntensity;\nuniform float paramSpeed;\nvoid main() {\n    float intensity = (paramIntensity / 100.0)*2.5;\n    float speed = 0.4 + (paramSpeed / 100.0)*2.4;\n    float f  = sin( (orientation > 0 ? vTextureCoord.x : vTextureCoord.y) * 320.0 * 3.14 );\n    float o  = f * (0.35 / 320.0);\n    float s  = f * .03 + 0.97;\n    float l  = sin(speed * time * 32. )*.03 + 0.97;\n    float r = texture2D(sTexture, vec2( vTextureCoord.x+o, vTextureCoord.y+o ) ).r;\n    float g = texture2D(sTexture, vec2( vTextureCoord.x-o, vTextureCoord.y+o ) ).g;\n    float b = texture2D(sTexture, vec2( vTextureCoord.x, vTextureCoord.y-o ) ).b;\n    vec4 ts = texture2D(sTexture, vTextureCoord);\n    gl_FragColor = mix(ts, vec4( r*0.7, g, b*0.9, l)*l*s, intensity);\n}\n";
    private int orientation;
    private float paramIntensity = 40.0f;
    private float paramSpeed = 25.0f;


    public GlCrtFilter() {
        super(FRAGMENT_SHADER);
        filterName = "Crt";
    }

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
        FilterUtilsKt.setFloat(getHandle("paramSpeed"), this.paramSpeed);
        FilterUtilsKt.setInteger(getHandle("orientation"), this.orientation);
    }

    public void setFrameSize(int i, int i2) {
        this.orientation = Orientation.INSTANCE.getOrientation(i, i2);
    }

}
