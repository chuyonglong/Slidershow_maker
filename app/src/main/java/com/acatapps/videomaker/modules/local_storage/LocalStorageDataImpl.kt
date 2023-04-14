package com.acatapps.videomaker.modules.local_storage

import android.database.Cursor
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import com.acatapps.videomaker.application.MainApp
import com.acatapps.videomaker.models.AudioData
import com.acatapps.videomaker.models.MediaData
import com.acatapps.videomaker.utils.MediaType
import com.acatapps.videomaker.utils.Logger
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File

class LocalStorageDataImpl :
    LocalStorageData {

    override val audioDataResponse = MutableLiveData<ArrayList<AudioData>>()

    override val mediaDataResponse = MutableLiveData<ArrayList<MediaData>>()


    override fun getAllAudio() {
        val audioList = arrayListOf<AudioData>()
        Observable.fromCallable<ArrayList<AudioData>> {
            val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val orderBy = MediaStore.Audio.Media.DATE_ADDED
            val selectionMusic = MediaStore.Audio.Media.IS_MUSIC + " != 0"
            val cursor: Cursor =
                MainApp.getContext().contentResolver.query(
                    uri,
                    null,
                    selectionMusic, null, "$orderBy DESC"
                )!!
            while (cursor.moveToNext()) {
                val filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val audioName =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                        ?: ""
                val mineType =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE)) ?: ""
                val duration = try {
                    cursor.getLong(cursor.getColumnIndex("duration"))
                } catch (e: Exception) {
                    continue
                }
                if (filePath.toLowerCase().contains(".m4a") || filePath.toLowerCase()
                        .contains(".mp3")
                ) {
                    if (duration > 10000) {
                        audioList.add(AudioData(filePath, audioName, mineType, duration))
                    }
                }
            }
            cursor.close()
            return@fromCallable audioList
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ArrayList<AudioData>> {
                override fun onNext(t: ArrayList<AudioData>) {
                    audioDataResponse.postValue(t)
                }

                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {}
            })
    }


    override fun getAllMedia(mediaType: MediaType) {
        when (mediaType) {
            MediaType.VIDEO -> getAllVideos()
            MediaType.PHOTO -> getAllPhoto()
        }
    }


    private fun getAllPhoto() {
        val mediaList = arrayListOf<MediaData>()
        Observable.fromCallable<ArrayList<MediaData>> {
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val orderBy = MediaStore.Images.Media.DATE_ADDED
            val cursor: Cursor =
                MainApp.getContext().contentResolver.query(
                    uri,
                    null,
                    null, null, "$orderBy DESC"
                )!!
            var file: File
            while (cursor.moveToNext()) {


                val dateAdded =
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED))
                val path =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)) ?: ""
                val folderContainName = File(path)?.parentFile?.name ?: ""
                if (path.toLowerCase().contains(".tif") || path.toLowerCase()
                        .contains(".psd") || path.toLowerCase().contains(".ai")
                ) continue
                Logger.e("image length = ${File(path).length()}")
                if (File(path).length() > 100)
                    if (!path.toLowerCase().contains(".gif") && !path.toLowerCase()
                            .contains("!\$&welcome@#image")
                    ) {
                        file = File(path)

                        if (file.exists()) {
                            val folderContainId = file.parentFile?.absolutePath ?: ""
                            mediaList.add(
                                MediaData(
                                    dateAdded * 1000,
                                    path,
                                    file.name,
                                    MediaType.PHOTO,
                                    folderContainId,
                                    folderContainName
                                )
                            )
                        }
                    }

            }
            cursor.close()
            return@fromCallable mediaList
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ArrayList<MediaData>> {
                override fun onNext(t: ArrayList<MediaData>) {
                    mediaDataResponse.postValue(t)
                }

                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {}
            })
    }

    private fun getAllVideos() {
        val mediaList = arrayListOf<MediaData>()
        Observable.fromCallable<ArrayList<MediaData>> {

            val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            val orderBy = MediaStore.Video.Media.DATE_ADDED
            val cursor: Cursor =
                MainApp.getContext().contentResolver.query(
                    uri,
                    null,
                    null, null, "$orderBy DESC"
                )!!
            var file: File


            while (cursor.moveToNext()) {

                val dateAdded =
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED))

                val path =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)) ?: ""
                val folderContainName = File(path).parentFile?.name ?: ""
                val duration = try {
                    cursor.getLong(cursor.getColumnIndex("duration"))
                } catch (e: Exception) {
                    continue
                }

                if (!path.toLowerCase().contains(".mp4")) continue
                file = File(path)
                if (duration > 1000)
                    if (file.exists()) {
                        try {

                            val folderContainId = file.parentFile?.absolutePath ?: ""
                            mediaList.add(
                                MediaData(dateAdded * 1000, path, file.name, MediaType.VIDEO, folderContainId, folderContainName, duration)
                            )
                        }catch (e:java.lang.Exception) {
                            continue
                        }

                    }
            }
            cursor.close()
            return@fromCallable mediaList
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ArrayList<MediaData>> {
                override fun onNext(t: ArrayList<MediaData>) {
                    mediaDataResponse.postValue(t)
                }

                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {}
            })
    }


}