package com.acatapps.videomaker.application

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.LifecycleObserver
import androidx.multidex.MultiDexApplication

import com.acatapps.videomaker.modules.audio_manager.AudioManager
import com.acatapps.videomaker.modules.audio_manager.AudioManagerImpl
import com.acatapps.videomaker.modules.local_storage.LocalStorageData
import com.acatapps.videomaker.modules.local_storage.LocalStorageDataImpl
import com.acatapps.videomaker.modules.music_player.MusicPlayer
import com.acatapps.videomaker.modules.music_player.MusicPlayerImpl
import com.acatapps.videomaker.ui.pick_media.PickMediaViewModelFactory
import com.acatapps.videomaker.ui.select_music.SelectMusicViewModelFactory
import com.acatapps.videomaker.ui.slide_show.SlideShowViewModelFactory
import com.acatapps.videomaker.utils.Preference
import com.tencent.mmkv.MMKV
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class MainApp : MultiDexApplication(), KodeinAware, Application.ActivityLifecycleCallbacks,
    LifecycleObserver {
    private var currentActivity: Activity? = null

    override val kodein = Kodein.lazy {
        import(androidXModule(this@MainApp))

        bind<LocalStorageData>() with singleton { LocalStorageDataImpl() }
        bind() from provider { PickMediaViewModelFactory(instance()) }
        bind() from provider { SelectMusicViewModelFactory(instance()) }
        bind() from provider { SlideShowViewModelFactory() }
        bind<AudioManager>() with  provider { AudioManagerImpl() }
        bind<MusicPlayer>() with  provider { MusicPlayerImpl() }
    }



    companion object {
        lateinit var instance: MainApp
        fun getContext() = instance.applicationContext!!
    }
    private var preference: Preference? = null


    override fun onCreate() {
        MMKV.initialize(this)
        super.onCreate()
        instance = this
        preference = Preference.buildInstance(this)
        if (preference?.firstInstall == false) {
            preference?.firstInstall = true
            preference?.setValueCoin(100)
        }

    }
    fun getPreference(): Preference? {
        return preference
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

}