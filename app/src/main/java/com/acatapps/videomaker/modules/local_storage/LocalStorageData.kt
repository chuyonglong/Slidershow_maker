package com.acatapps.videomaker.modules.local_storage

import androidx.lifecycle.MutableLiveData
import com.acatapps.videomaker.models.AudioData
import com.acatapps.videomaker.models.MediaData
import com.acatapps.videomaker.utils.MediaType

interface LocalStorageData {
    val audioDataResponse:MutableLiveData<ArrayList<AudioData>>
    val mediaDataResponse:MutableLiveData<ArrayList<MediaData>>

    fun getAllAudio()
    fun getAllMedia(mediaType: MediaType)
}