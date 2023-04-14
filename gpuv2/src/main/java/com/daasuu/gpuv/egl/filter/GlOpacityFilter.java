package com.daasuu.gpuv.egl.filter;

import android.opengl.GLES20;


public class GlOpacityFilter extends GlFilter {

    private static final String OPACITY_FRAGMENT_SHADER = "" +
            "precision mediump float;" +
            " varying highp vec2 vTextureCoord;\n" +
            "  \n" +
            " uniform lowp sampler2D sTexture;\n" +
            " uniform lowp float opacity;\n" +
            "  \n" +
            "  void main()\n" +
            "  {\n" +
            "      lowp vec4 textureColor = texture2D(sTexture, vTextureCoord);\n" +
            "      \n" +
            "      gl_FragColor = vec4(textureColor.rgb, textureColor.a * opacity);\n" +
            "  }\n";

    public GlOpacityFilter() {
        super(DEFAULT_VERTEX_SHADER, OPACITY_FRAGMENT_SHADER);
        filterName = "Opacity";
    }

    private float opacity = 1f;

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    @Override
    public void onDraw() {
        GLES20.glUniform1f(getHandle("opacity"), opacity);
    }

}
