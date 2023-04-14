package com.acatapps.videomaker.models

class RecordedDataModel(private val mRecordedData: RecordedData) {
    val path = mRecordedData.recordFilePath
    var isSelect = false
    val startOffset = mRecordedData.startMs
    val endOffset = mRecordedData.endMs
    fun checkTime(timeMs:Int):Boolean {
        if(timeMs >= mRecordedData.startMs && timeMs <= mRecordedData.endMs) return true
        return false
    }
}