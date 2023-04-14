package com.acatapps.videomaker.models

import androidx.annotation.DrawableRes

data class IconModel(
    var id: Int? = null,
    @DrawableRes
    var image: Int? = null,
    var textIcon: String? = null
)
