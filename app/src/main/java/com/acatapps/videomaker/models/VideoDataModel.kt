package com.acatapps.videomaker.models

class VideoDataModel {
    val filePath:String
    var count = 0
    val dateAdded:Long
    val duration:Long
    constructor(videoData: VideoData) {
        this.filePath = videoData.path
        this.dateAdded = videoData.dateAdded
        this.duration = videoData.duration
    }

    constructor(dateAdded:Long) {
        this.dateAdded = dateAdded
        filePath = ""
        duration = 0
    }

}