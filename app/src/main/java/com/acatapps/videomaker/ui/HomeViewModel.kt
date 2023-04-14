package com.acatapps.videomaker.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.acatapps.videomaker.models.VideoSave
import com.datnt.slideshowmaker.data_local.MyDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(var context: Context) : ViewModel() {
    private val database = MyDatabase.getInstance(context).videoSaveDao()

    fun update(videoSave: VideoSave) = viewModelScope.launch(Dispatchers.IO) {
        database.update(videoSave)
    }.invokeOnCompletion {
        Log.d("error", it?.message.toString())
    }
    fun add(videoSave: VideoSave) = viewModelScope.launch (Dispatchers.IO){
        database.insert(videoSave)
    }.invokeOnCompletion {
        Log.d("error", it?.message.toString())
    }
    fun getAll() = database.getAll()
    fun delete(videoSave: VideoSave) = viewModelScope.launch(Dispatchers.IO) {
        database.delete(videoSave)
    }


    class HomeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(context) as T
            }
            throw IllegalArgumentException("loiN")
        }

    }
}