package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlMirrorFilter extends GlFilter {
    private static final String FRAGMENT_SHADER = "\n            precision mediump float;\n\n            varying highp vec2 vTextureCoord;\n            uniform lowp sampler2D sTexture;\n\n            uniform highp float verticalMirror;\n            uniform highp float horizontalMirror;\n            void main() {\n                highp vec2 p = vTextureCoord;\n\n                if (verticalMirror < 0.0) {\n                    if (p.x > -verticalMirror) {\n                        p.x = -2.0 * verticalMirror - p.x;\n                    }\n                } else if (verticalMirror > 0.0) {\n                    if (p.x < verticalMirror) {\n                        p.x = 2.0 * verticalMirror - p.x;\n                    }\n                }\n                if (horizontalMirror < 0.0) {\n                    if (p.y > -horizontalMirror) {\n                        p.y = -2.0 * horizontalMirror - p.y;\n                    }\n                } else if (horizontalMirror > 0.0) {\n                    if (p.y < horizontalMirror) {\n                        p.y = 2.0 * horizontalMirror - p.y;\n                    }\n                }\n\n                if (p.x > 1.0 || p.x < 0.0 || p.y > 1.0 || p.y < 0.0) {\n                    gl_FragColor = vec4(0.0);\n                } else {\n                    gl_FragColor = texture2D(sTexture, p);\n                }\n            }\n        ";
    private final float horizontalMirror;
    private final float verticalMirror;



    public GlMirrorFilter() {
        this(0.0f, -0.5f);
        filterName = "Mirror";
    }

    public static GlMirrorFilter bottomToTop() {
        GlMirrorFilter glMirrorFilter = new GlMirrorFilter(-0.5f, 0.0f);
        glMirrorFilter.filterName = "MirV1";
        return glMirrorFilter;
    }

    public static GlMirrorFilter topToBottom() {
        GlMirrorFilter glMirrorFilter = new GlMirrorFilter(0.0f, 0.5f);
        glMirrorFilter.filterName = "MirV2";
        return glMirrorFilter;
    }

    public static GlMirrorFilter rightToLeft() {
        GlMirrorFilter glMirrorFilter = new GlMirrorFilter(0.0f, -0.5f);
        glMirrorFilter.filterName = "MirH1";
        return glMirrorFilter;
    }

    public static GlMirrorFilter leftToRight() {
        GlMirrorFilter glMirrorFilter = new GlMirrorFilter(-0.5f, 0.0f);
        glMirrorFilter.filterName = "MirH2";
        return glMirrorFilter;
    }

    public static GlMirrorFilter moreMirror() {
        GlMirrorFilter glMirrorFilter = new GlMirrorFilter(-0.5f, 0.5f);
        glMirrorFilter.filterName = "MirM";
        return glMirrorFilter;
    }

    public GlMirrorFilter(float f, float f2) {
        super("attribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = aPosition;\nvTextureCoord = aTextureCoord.xy;\n}\n", FRAGMENT_SHADER);
        this.verticalMirror = f;
        this.horizontalMirror = f2;
    }

    public void onDraw() {
        FilterUtilsKt.setFloat(getHandle("verticalMirror"), this.verticalMirror);
        FilterUtilsKt.setFloat(getHandle("horizontalMirror"), this.horizontalMirror);
    }
}
