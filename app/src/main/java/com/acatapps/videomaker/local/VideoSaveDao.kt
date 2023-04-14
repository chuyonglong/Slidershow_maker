package com.datnt.slideshowmaker.data_local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.acatapps.videomaker.models.VideoSave
@Dao
interface VideoSaveDao {
    @Query("SELECT * FROM note_table")
    fun getAll(): LiveData<List<VideoSave>>

    @Query("SELECT * FROM note_table WHERE id = :id")
    fun getById(id: Int): VideoSave?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(videoSave: VideoSave)

    @Update
    suspend fun update(videoSave: VideoSave)

    @Delete
    fun delete(videoSave: VideoSave)
}
