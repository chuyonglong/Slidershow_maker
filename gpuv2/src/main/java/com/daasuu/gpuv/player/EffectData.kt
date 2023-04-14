package com.daasuu.gpuv.player

import com.daasuu.gpuv.egl.filter.GlFilter

data class EffectData(
    var startTime: Long, var endTime: Long, val filter: GlFilter
)