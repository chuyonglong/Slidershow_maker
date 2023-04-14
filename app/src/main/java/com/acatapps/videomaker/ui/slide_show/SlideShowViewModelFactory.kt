package com.acatapps.videomaker.ui.slide_show

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SlideShowViewModelFactory : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SlideShowViewModel() as T
    }

}