package com.daasuu.gpuv.egl.more_filter;

public class Orientation {
    public static final int HORIZONTAL = 0;
    public static final Orientation INSTANCE = new Orientation();
    public static final int VERTICAL = 1;

    public final int getOrientation(int i, int i2) {
        return i > i2 ? 0 : 1;
    }

    private Orientation() {
    }
}
