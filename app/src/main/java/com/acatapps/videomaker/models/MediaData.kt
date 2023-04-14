package com.acatapps.videomaker.models

import com.acatapps.videomaker.utils.MediaType

data class MediaData(val dateAdded:Long, val filePath:String = "", val fileName:String="", val mediaType: MediaType = MediaType.PHOTO, val folderId:String="", val folderName:String="", val duration:Long=0)