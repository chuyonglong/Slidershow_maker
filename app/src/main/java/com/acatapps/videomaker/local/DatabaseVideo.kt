package com.datnt.slideshowmaker.data_local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import com.acatapps.videomaker.models.VideoSave

@Database(entities = [VideoSave::class], version = 1, exportSchema = false)
abstract class MyDatabase : RoomDatabase() {
    abstract fun videoSaveDao(): VideoSaveDao

    companion object {
        @Volatile
        private var INSTANCE: MyDatabase? = null

        fun getInstance(context: Context): MyDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    MyDatabase::class.java, "my_database.db"
                ).build().also { INSTANCE = it }
            }
    }
}