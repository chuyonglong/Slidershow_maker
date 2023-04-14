package com.acatapps.videomaker.models

import com.acatapps.videomaker.utils.Utils

class ImageWithLookupData(val id:Int, val imagePath:String, var lookupType: Utils.LookupType=Utils.LookupType.NONE) {
}