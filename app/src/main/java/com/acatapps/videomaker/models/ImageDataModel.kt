package com.acatapps.videomaker.models

class ImageDataModel {
    val filePath:String
    var count = 0
    val dateAdded:Long
    constructor(imageData: ImageData) {
        this.filePath = imageData.filePath
        this.dateAdded = imageData.dateAdded
    }

    constructor(dateAdded:Long) {
        this.dateAdded = dateAdded
        filePath = ""
    }
}