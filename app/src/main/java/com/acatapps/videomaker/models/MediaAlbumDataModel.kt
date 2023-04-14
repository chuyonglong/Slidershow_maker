package com.acatapps.videomaker.models

class MediaAlbumDataModel {
    val mediaItemPaths = arrayListOf<MediaData>()
    val folderId:String
    val albumName:String

    constructor(albumName:String, folderId:String) {
        this.folderId = folderId
        this.albumName = albumName
    }

}