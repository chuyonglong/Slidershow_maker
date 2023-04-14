package com.library.acatapps.gpufilter.effect;

import android.opengl.GLES20;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

public class GPUMirrorFilter extends GPUImageFilter {
    private static final String FRAGMENT_SHADER = "\n            precision mediump float;\n\n            varying highp vec2 vTextureCoord;\n            uniform lowp sampler2D sTexture;\n\n            uniform highp float verticalMirror;\n            uniform highp float horizontalMirror;\n            void main() {\n                highp vec2 p = vTextureCoord;\n\n                if (verticalMirror < 0.0) {\n                    if (p.x > -verticalMirror) {\n                        p.x = -2.0 * verticalMirror - p.x;\n                    }\n                } else if (verticalMirror > 0.0) {\n                    if (p.x < verticalMirror) {\n                        p.x = 2.0 * verticalMirror - p.x;\n                    }\n                }\n                if (horizontalMirror < 0.0) {\n                    if (p.y > -horizontalMirror) {\n                        p.y = -2.0 * horizontalMirror - p.y;\n                    }\n                } else if (horizontalMirror > 0.0) {\n                    if (p.y < horizontalMirror) {\n                        p.y = 2.0 * horizontalMirror - p.y;\n                    }\n                }\n\n                if (p.x > 1.0 || p.x < 0.0 || p.y > 1.0 || p.y < 0.0) {\n                    gl_FragColor = vec4(0.0);\n                } else {\n                    gl_FragColor = texture2D(sTexture, p);\n                }\n            }\n        ";
    private final float horizontalMirror;
    private final float verticalMirror;

    private int verticalLocation;
    private int horizontalLocation;

    public GPUMirrorFilter(float horizontalMirror, float verticalMirror) {
        super("attribute vec4 position;\nattribute vec4 inputTextureCoordinate;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = position;\nvTextureCoord = inputTextureCoordinate.xy;\n}\n", FRAGMENT_SHADER);
        this.horizontalMirror = horizontalMirror;
        this.verticalMirror = verticalMirror;
    }

    public static GPUMirrorFilter bottomToTop() {
        GPUMirrorFilter glMirrorFilter = new GPUMirrorFilter(-0.5f, 0.0f);
        return glMirrorFilter;
    }

    public static GPUMirrorFilter topToBottom() {
        GPUMirrorFilter glMirrorFilter = new GPUMirrorFilter(0.0f, 0.5f);
        return glMirrorFilter;
    }

    public static GPUMirrorFilter rightToLeft() {
        GPUMirrorFilter glMirrorFilter = new GPUMirrorFilter(0.0f, -0.5f);
        return glMirrorFilter;
    }

    public static GPUMirrorFilter leftToRight() {
        GPUMirrorFilter glMirrorFilter = new GPUMirrorFilter(-0.5f, 0.0f);
        return glMirrorFilter;
    }

    public static GPUMirrorFilter moreMirror() {
        GPUMirrorFilter glMirrorFilter = new GPUMirrorFilter(-0.5f, 0.5f);
        return glMirrorFilter;
    }

    @Override
    public void onInit() {
        super.onInit();
        verticalLocation = GLES20.glGetUniformLocation(getProgram(), "verticalMirror");
        horizontalLocation = GLES20.glGetUniformLocation(getProgram(), "horizontalMirror");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setFloat(verticalLocation , verticalMirror);
        setFloat(horizontalLocation , horizontalMirror);
    }
}
