package com.daasuu.gpuv.player

import com.daasuu.gpuv.composer.StickerDrawer

data class StickerDrawerData(
    val startOffset: Long,
    val endOffset: Long,
    val stickerDrawer: StickerDrawer
)