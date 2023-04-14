package com.daasuu.gpuv.egl.more_filter;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

public class FilterUtilsKt {
    public static final float range(int i, float f, float f2) {
        return (((f2 - f) * ((float) i)) / 100.0f) + f;
    }

    public static final void setInteger(int i, int i2) {
        GLES20.glUniform1i(i, i2);
    }

    public static final void setFloat(int i, float f) {
        GLES20.glUniform1f(i, f);
    }

    public static final void setUniformMatrix3f(int i, float[] fArr) {
        GLES20.glUniformMatrix3fv(i, 1, false, fArr, 0);
    }

    public static final void setFloatVec2(int i, float[] fArr) {
        GLES20.glUniform2fv(i, 1, FloatBuffer.wrap(fArr));
    }

    public static final void setFloatVec3(int i, float[] fArr) {
        GLES20.glUniform3fv(i, 1, FloatBuffer.wrap(fArr));
    }
}
