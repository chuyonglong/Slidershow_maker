package com.acatapps.videomaker.gs_effect

import com.acatapps.videomaker.models.GSEffectData

object GSEffectUtils {

    enum class EffectType {
        NONE,
        SNOW,
        RAIN,
        WISP,
        WAVY,
        ZOOM_BLUR,
        CROSS_HATCHING,
        CROSS,
        GLITCH,
        TV_SHOW,
        MIRROR_H2,
        TILES,
        GRAY_SCALE,
        SPLIT_COLOR,
        POLYGON,
        DAWN,
        HALF_TONE
    }

    fun getEffectByType(effectType: EffectType) :GSEffect{
        return when(effectType) {
            EffectType.NONE -> GSEffect()
            EffectType.SNOW -> GSEffectSnow()
            EffectType.RAIN -> GSEffectRain()
            EffectType.WISP -> GSEffectWisp()
            EffectType.WAVY -> GSEffectWavy()
            EffectType.ZOOM_BLUR -> GSEffectZoomBlur()
            EffectType.CROSS_HATCHING -> GSEffectCrossHatching()
            EffectType.CROSS -> GSEffectCross()
            EffectType.GLITCH -> GSEffectGlitch()
            EffectType.TV_SHOW -> GSEffectTVShow()
            EffectType.MIRROR_H2 -> GSEffectMirrorH2()
            EffectType.TILES -> GSEffectTiles()
            EffectType.GRAY_SCALE -> GSEffectGrayScale()
            EffectType.SPLIT_COLOR -> GSEffectSplitColor()
            EffectType.POLYGON -> GSEffectPolygon()
            EffectType.DAWN -> GSEffectDawn()
            EffectType.HALF_TONE -> GSEffectHalfTone()
        }
    }

    fun getAllGSEffectData():ArrayList<GSEffectData> {
        val effectDataList = ArrayList<GSEffectData>()
        for(item in EffectType.values()) {
            effectDataList.add(GSEffectData(item))
        }
        return effectDataList
    }

}