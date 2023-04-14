package com.acatapps.videomaker.models

import com.acatapps.videomaker.transition.GSTransition
import java.io.Serializable

data class RenderImageSlideData(
    val imageList: ArrayList<String>,
    val bitmapHashMap:HashMap<String, String>,
    val videoQuality: Int,
    val delayTimeSec: Int,
    val themeData: ThemeData,
    val musicReturnData: MusicReturnData?,
    val gsTransition: com.acatapps.videomaker.transition.GSTransition,
    val stickerAddedForRender : ArrayList<StickerForRenderData>
) :Serializable