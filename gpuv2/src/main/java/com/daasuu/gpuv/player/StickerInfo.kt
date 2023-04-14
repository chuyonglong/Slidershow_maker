package com.daasuu.gpuv.player



data class StickerInfo(
    val id: Int,
    val path: String,
    var startTime: Long,
    var endTime: Long,
    var saveMatrix: FloatArray = FloatArray(9),
    var isSticker: Boolean = true,
    var width: Int = 1080,
    var height: Int = 1080
)