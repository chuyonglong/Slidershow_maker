package com.library.acatapps.gpufilter.filter;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageColorMatrixFilter;

/**
 * Applies a simple sepia effect.
 */
public class GPUImageSepiaToneFilter extends GPUImageColorMatrixFilter {

    public GPUImageSepiaToneFilter() {
        this(1.0f);
    }

    public GPUImageSepiaToneFilter(final float intensity) {
        super(intensity, new float[]{
                0.3588f, 0.7044f, 0.1368f, 0.0f,
                0.2990f, 0.5870f, 0.1140f, 0.0f,
                0.2392f, 0.4696f, 0.0912f, 0.0f,
                0f, 0f, 0f, 1.0f
        });
    }
}
