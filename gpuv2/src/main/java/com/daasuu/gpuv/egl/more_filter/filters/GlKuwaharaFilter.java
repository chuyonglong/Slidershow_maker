package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlKuwaharaFilter extends GlFilter {

    private static final String FRAGMENT_SHADER = "\nvarying highp vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform int paramRadius;\n\nprecision highp float;\n\nconst vec2 src_size = vec2 (1.0 / 768.0, 1.0 / 1024.0);\n\nvoid main (void)\n{\n    vec2 uv = vTextureCoord;\n    float n = float((paramRadius + 1) * (paramRadius + 1));\n    int i; int j;\n    vec3 m0 = vec3(0.0); vec3 m1 = vec3(0.0); vec3 m2 = vec3(0.0); vec3 m3 = vec3(0.0);\n    vec3 s0 = vec3(0.0); vec3 s1 = vec3(0.0); vec3 s2 = vec3(0.0); vec3 s3 = vec3(0.0);\n    vec3 c;\n\n    for (j = -paramRadius; j <= 0; ++j) {\n        for (i = -paramRadius; i <= 0; ++i) {\n            c = texture2D(sTexture, uv + vec2(i,j) * src_size).rgb;\n            m0 += c;\n            s0 += c * c;\n        }\n    }\n\n    for (j = -paramRadius; j <= 0; ++j) {\n        for (i = 0; i <= paramRadius; ++i) {\n            c = texture2D(sTexture, uv + vec2(i,j) * src_size).rgb;\n            m1 += c;\n            s1 += c * c;\n        }\n    }\n\n    for (j = 0; j <= paramRadius; ++j) {\n        for (i = 0; i <= paramRadius; ++i) {\n            c = texture2D(sTexture, uv + vec2(i,j) * src_size).rgb;\n            m2 += c;\n            s2 += c * c;\n        }\n    }\n\n    for (j = 0; j <= paramRadius; ++j) {\n        for (i = -paramRadius; i <= 0; ++i) {\n            c = texture2D(sTexture, uv + vec2(i,j) * src_size).rgb;\n            m3 += c;\n            s3 += c * c;\n        }\n    }\n\n\n    float min_sigma2 = 1e+2;\n    m0 /= n;\n    s0 = abs(s0 / n - m0 * m0);\n\n    float sigma2 = s0.r + s0.g + s0.b;\n    if (sigma2 < min_sigma2) {\n        min_sigma2 = sigma2;\n        gl_FragColor = vec4(m0, 1.0);\n    }\n\n    m1 /= n;\n    s1 = abs(s1 / n - m1 * m1);\n\n    sigma2 = s1.r + s1.g + s1.b;\n    if (sigma2 < min_sigma2) {\n        min_sigma2 = sigma2;\n        gl_FragColor = vec4(m1, 1.0);\n    }\n\n    m2 /= n;\n    s2 = abs(s2 / n - m2 * m2);\n\n    sigma2 = s2.r + s2.g + s2.b;\n    if (sigma2 < min_sigma2) {\n        min_sigma2 = sigma2;\n        gl_FragColor = vec4(m2, 1.0);\n    }\n\n    m3 /= n;\n    s3 = abs(s3 / n - m3 * m3);\n\n    sigma2 = s3.r + s3.g + s3.b;\n    if (sigma2 < min_sigma2) {\n        min_sigma2 = sigma2;\n        gl_FragColor = vec4(m3, 1.0);\n    }\n}\n";
    private int paramRadius;

    public GlKuwaharaFilter() {
        super("attribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = aPosition;\nvTextureCoord = aTextureCoord.xy;\n}\n", FRAGMENT_SHADER);
        filterName = "Kuwahara";}

    public void onDraw() {
        FilterUtilsKt.setInteger(getHandle("paramRadius"), this.paramRadius);
    }

    public void setFrameSize(int i, int i2) {
        double d = (double) i2;
        Double.isNaN(d);
        this.paramRadius = (int) Math.ceil(d / 100.0d);
    }
}
