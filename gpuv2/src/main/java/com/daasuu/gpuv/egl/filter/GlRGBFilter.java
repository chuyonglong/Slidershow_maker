package com.daasuu.gpuv.egl.filter;

import android.opengl.GLES20;


public class GlRGBFilter extends GlFilter {

    private static final String RGB_FRAGMENT_SHADER = "" +
            "precision mediump float;" +
            " varying vec2 vTextureCoord;\n" +
            "  \n" +
            " uniform lowp sampler2D sTexture;\n" +
            "  uniform highp float red;\n" +
            "  uniform highp float green;\n" +
            "  uniform highp float blue;\n" +
            "  \n" +
            "  void main()\n" +
            "  {\n" +
            "      highp vec4 textureColor = texture2D(sTexture, vTextureCoord);\n" +
            "      \n" +
            "      gl_FragColor = vec4(textureColor.r * red, textureColor.g * green, textureColor.b * blue, 1.0);\n" +
            "  }\n";

    public GlRGBFilter() {
        super(DEFAULT_VERTEX_SHADER, RGB_FRAGMENT_SHADER);filterName = "RGB";
    }

    private float red = 1f;
    private float green = 1f;
    private float blue = 1f;

    public void setRed(float red) {
        this.red = red;
    }

    public void setGreen(float green) {
        this.green = green;
    }

    public void setBlue(float blue) {
        this.blue = blue;
    }

    @Override
    public void onDraw() {
        GLES20.glUniform1f(getHandle("red"), red);
        GLES20.glUniform1f(getHandle("green"), green);
        GLES20.glUniform1f(getHandle("blue"), blue);
    }
}
