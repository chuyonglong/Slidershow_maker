package com.acatapps.videomaker.ui.slide_show

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SlideShowViewModel : ViewModel() {

    private val mCurrentSlideShowFrameLiveData = MutableLiveData<Int>()
    val currentSlideShowFrameLiveData get() = mCurrentSlideShowFrameLiveData

    private val mMaxSlideShowDurationLiveData = MutableLiveData<Int>()
    val maxSlideShowDurationLiveData get() = mMaxSlideShowDurationLiveData

    private val mPlayStateLiveData = MutableLiveData<Boolean>(false)
    val playStateLiveData get() = mPlayStateLiveData

    private val mSlideShowIsReady = MutableLiveData<Boolean>(false)
    val slideShowIsReady get() = mSlideShowIsReady

    fun setCurrentSlideFrame(numberFrame:Int) {
        mCurrentSlideShowFrameLiveData.postValue(numberFrame)
    }

    fun setMaxSlideDuration(maxDuration:Int) {
        mMaxSlideShowDurationLiveData.postValue(maxDuration)
    }

    fun pauseSlideShow() {
        if(mPlayStateLiveData.value == false) return
        mPlayStateLiveData.postValue(false)
    }

    fun startSlideShow() {
        if(mPlayStateLiveData.value == true) return
        mPlayStateLiveData.postValue(true)
    }

    fun setSlideShowIsReady(isReady:Boolean) {
        mSlideShowIsReady.postValue(isReady)
    }

}