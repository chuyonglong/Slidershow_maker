package com.library.acatapps.gpufilter.effect;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

public class GPUBeveledFilter extends GPUImageFilter {
    private static final String FRAGMENT_SHADER = "\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float paramSize;\nuniform float paramHorEdge;\nuniform float paramVerEdge;\nvoid main() {\n    float PI = 3.1415926535;\n    float hor = PI*(paramHorEdge / 100.0);\n    float ver = PI*(paramVerEdge / 100.0);\n    float size = 1.0 - (paramSize / 100.0)*0.8;\n    vec2 U = 2.0*vTextureCoord - 1.0;\n    float x = U.x;\n    float y = U.y;\n    float a = max(abs(x),abs(y));\n    float top = 1.0 - cos(ver);\n    float right = 1.0 - cos(11.0*hor);\n    float bottom = 1.0 - cos(11.0*ver);\n    float left = 1.0 - cos(hor);\n    float mult = a < size ? 1.0 :  x+y > 0. ? y > x ? top : right : y < x ? bottom : left;\n    gl_FragColor =  mult * texture2D(sTexture, vTextureCoord);\n}\n";
    private float paramHorEdge = 69.0f;
    private float paramSize = 40.0f;
    private float paramVerEdge = 31.0f;

    private int horEdgeLocation;
    private int sizeLocation;
    private int verEdgeLocation;

    public GPUBeveledFilter() {
        super("attribute vec4 position;\nattribute vec4 inputTextureCoordinate;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = position;\nvTextureCoord = inputTextureCoordinate.xy;\n}\n", FRAGMENT_SHADER);
    }

    @Override
    public void onInit() {
        super.onInit();
        horEdgeLocation = GLES20.glGetUniformLocation(getProgram(), "paramHorEdge");
        verEdgeLocation = GLES20.glGetUniformLocation(getProgram(), "paramVerEdge");
        sizeLocation = GLES20.glGetUniformLocation(getProgram(), "paramSize");
    }

    @Override
    public void onDraw(int textureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
        super.onDraw(textureId, cubeBuffer, textureBuffer);
        setFloat(horEdgeLocation , paramHorEdge);
        setFloat(verEdgeLocation , paramVerEdge);
        setFloat(sizeLocation , paramSize);
    }
}
