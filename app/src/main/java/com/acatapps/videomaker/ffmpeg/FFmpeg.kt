package com.acatapps.videomaker.ffmpeg

import android.annotation.SuppressLint
import android.content.Context
import android.os.PowerManager
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import com.acatapps.videomaker.application.MainApp
import com.acatapps.videomaker.utils.Logger

class FFmpeg(val cmd:Array<String>) {

    @SuppressLint("InvalidWakeLockTag")
    fun runCmd(onComplete:()->Unit) {
        val powerManager = MainApp.getContext().getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Lock")
        wakeLock.acquire()
        Thread{
            Config.enableStatisticsCallback { newStatistics ->
                Logger.e("time = ${newStatistics.time} , speed = ${newStatistics.speed}")
            }
            val status = FFmpeg.execute(cmd)
            onComplete.invoke()
        }.start()
    }

    @SuppressLint("InvalidWakeLockTag")
    fun runCmd(onUpdateProgress:(Int)->Unit, onComplete:()->Unit) {
        val powerManager = MainApp.getContext().getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Lock")
        wakeLock.acquire()
        Thread{
            Config.enableStatisticsCallback { newStatistics ->
                Logger.e("time = ${newStatistics.time} , speed = ${newStatistics.speed}")
                onUpdateProgress.invoke(newStatistics.time)
            }
            val status = FFmpeg.execute(cmd)



            onComplete.invoke()
        }.start()
    }

    fun cancel() {
        FFmpeg.cancel()

    }

}