package com.acatapps.videomaker.models

import java.io.File

class MyStudioDataModel(val filePath:String, var dateAdded:Long = 0, val duration:Int):Comparable<MyStudioDataModel> {

    var checked = false
    init {
        if(filePath.isNotEmpty()) {
            dateAdded = File(filePath).lastModified()
        }
    }

    override fun compareTo(other: MyStudioDataModel): Int = other.dateAdded.compareTo(dateAdded)
}