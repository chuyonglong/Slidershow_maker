package com.acatapps.videomaker.models

class ImageWithLookupDataModel(private val mImageWithLookupData: ImageWithLookupData) {

    val imagePath
    get() = mImageWithLookupData.imagePath

    val id
    get() = mImageWithLookupData.id

    var lookupType = mImageWithLookupData.lookupType
}