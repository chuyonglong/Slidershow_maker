package com.daasuu.gpuv.egl.more_filter.filters;

import com.daasuu.gpuv.egl.filter.GlToneFilter;
import com.daasuu.gpuv.egl.more_filter.FilterUtilsKt;

public class GlFixedToneFilter extends GlToneFilter {
    public GlFixedToneFilter() {
        filterName = "Tone";
    }
    public void onDraw() {
        FilterUtilsKt.setFloat(getHandle("texelWidth"), getTexelWidth());
        FilterUtilsKt.setFloat(getHandle("texelHeight"), getTexelHeight());
        FilterUtilsKt.setFloat(getHandle("threshold"), getThreshold());
        FilterUtilsKt.setFloat(getHandle("quantizationLevels"), getQuantizationLevels());
    }
}
