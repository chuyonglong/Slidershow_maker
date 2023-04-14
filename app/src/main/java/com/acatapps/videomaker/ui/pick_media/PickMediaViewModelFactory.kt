package com.acatapps.videomaker.ui.pick_media

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.acatapps.videomaker.modules.local_storage.LocalStorageData

class PickMediaViewModelFactory (private val localStorageData: LocalStorageData) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PickMediaViewModel(localStorageData) as T
    }



}