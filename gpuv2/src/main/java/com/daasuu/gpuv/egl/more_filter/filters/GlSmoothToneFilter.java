package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.filter.GlFilterGroup;
import com.daasuu.gpuv.egl.filter.GlGaussianBlurFilter;

public class GlSmoothToneFilter extends GlFilterGroup {
    public GlSmoothToneFilter() {
        super(new GlGaussianBlurFilter(), new GlFixedToneFilter());
        filterName = "Smooth";
    }
}
