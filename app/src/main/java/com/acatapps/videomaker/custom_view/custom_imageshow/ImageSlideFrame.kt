package com.acatapps.videomaker.custom_view.custom_imageshow

import android.graphics.Bitmap
import com.acatapps.videomaker.utils.Utils

class ImageSlideFrame(
    val fromBitmap: Bitmap,
    val toBitmap: Bitmap,
    val fromLookupBitmap: Bitmap,
    val toLookupBitmap: Bitmap,
    var progress: Float,
    var slideId: Long,
    var zoomProgress: Float,
    var zoomProgress1: Float,
    var transitionType: Utils.TransitionType
)