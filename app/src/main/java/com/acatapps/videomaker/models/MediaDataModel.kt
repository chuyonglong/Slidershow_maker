package com.acatapps.videomaker.models

class MediaDataModel(private val mMediaData: MediaData):Comparable<MediaDataModel> {

    val filePath = mMediaData.filePath
    val dateAdded = mMediaData.dateAdded
    var count = 0
    val kind = mMediaData.mediaType
    val duration = mMediaData.duration
    override fun compareTo(other: MediaDataModel): Int = other.dateAdded.compareTo(dateAdded)
}