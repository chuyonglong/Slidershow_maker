package com.acatapps.videomaker.custom_view.custom_imageshow

import com.acatapps.videomaker.utils.Utils
import java.io.Serializable

class ImageSlideData(val slideId:Long, val fromImagePath:String, val toImagePath:String, var lookupType: Utils.LookupType = Utils.LookupType.NONE):Serializable