package com.acatapps.videomaker.custom_view.custom_imageshow

import com.acatapps.videomaker.utils.Utils
import java.io.Serializable
import java.math.BigDecimal

class ImageSlideData(
    val slideId: Long,
    val fromImagePath: String,
    val toImagePath: String,
    var transitionType: Utils.TransitionType,
    var transitionPlaytime: Int = 3,
    var lookupType: Utils.LookupType = Utils.LookupType.NONE
) : Serializable