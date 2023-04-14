package com.library.acatapps.gpufilter.effect;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

public class GPUTvShopFilter extends GPUImageFilter {
    private static final String FRAGMENT_SHADER = "\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform int orientation;\nvoid main() {\n    gl_FragColor = texture2D(sTexture, fract(vTextureCoord*exp2(ceil(-log2(orientation == 1 ? vTextureCoord.x : vTextureCoord.y)))));\n}";
    private int orientation;

    public GPUTvShopFilter() {
        super("attribute vec4 position;\nattribute vec4 inputTextureCoordinate;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = position;\nvTextureCoord = inputTextureCoordinate.xy;\n}\n", FRAGMENT_SHADER);
    }

    @Override
    public void onInit() {
        super.onInit();
        orientation = GLES20.glGetUniformLocation(getProgram(), "orientation");
    }

    @Override
    public void onDraw(int textureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
        super.onDraw(textureId, cubeBuffer, textureBuffer);
        setInteger(orientation , getOutputWidth() > getOutputHeight() ? 0 : 1);
    }
}
