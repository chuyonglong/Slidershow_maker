package com.acatapps.videomaker.models

class LookupDataModel(private val mLookupData: LookupData) {
    val name
    get() = mLookupData.name

    val lookupType
    get() = mLookupData.lookupType
}