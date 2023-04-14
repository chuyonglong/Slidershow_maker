package com.acatapps.videomaker.models

import com.acatapps.videomaker.gs_effect.GSEffectUtils

class GSEffectDataModel (val gsEffectData: GSEffectData) {
    val gsEffect = GSEffectUtils.getEffectByType(gsEffectData.effectType)
    val name = gsEffect.name
    var isSelect = false
}