package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlTilesFilter extends GlFilter {

    public static final String FRAGMENT_SHADER = "\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nconst vec3 edgeColor = vec3(0.7);\nconst float threshhold = 0.15;\nuniform float texelWidth;\nuniform float paramIntensity;\nvec2 fmod(vec2 a, vec2 b) { return abs(fract(abs(a / b)) * abs(b)); }\nvoid main() {\n    float numTiles = 30.0 + (paramIntensity * texelWidth * 500.0);\n    vec2 uv = vTextureCoord;\n    float size = 1.0 / numTiles;\n    vec2 pBase = uv - fmod(uv, vec2(size));\n    vec2 pCenter = pBase + vec2(size / 2.0);\n    vec2 st = (uv - pBase) / size;\n    vec4 invOff = vec4((1.0 - edgeColor), 1.0);\n    float threshholdB = 1.0 - threshhold;\n    vec4 c1 = st.x > st.y ? invOff : vec4(0);\n    vec4 cBottom = st.x > threshholdB || st.y > threshholdB ? c1 : vec4(0);\n    c1 = st.x > st.y ? invOff : vec4(0);\n    vec4 cTop = st.x < threshhold || st.y < threshhold ? c1 : vec4(0);\n    vec4 tileColor = texture2D(sTexture, pCenter);\n    gl_FragColor = tileColor + cTop - cBottom;\n}";
    private float paramIntensity = 40.0f;
    private float texelWidth;


    public GlTilesFilter() {
        super("attribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = aPosition;\nvTextureCoord = aTextureCoord.xy;\n}\n", FRAGMENT_SHADER);
        filterName = "Tiles";}

    public void onDraw() {
        super.onDraw();
        FilterUtilsKt.setFloat(getHandle("texelWidth"), this.texelWidth);
        FilterUtilsKt.setFloat(getHandle("paramIntensity"), this.paramIntensity);
    }

    public void setFrameSize(int i, int i2) {
        this.texelWidth = 1.0f / ((float) i);
    }
}
