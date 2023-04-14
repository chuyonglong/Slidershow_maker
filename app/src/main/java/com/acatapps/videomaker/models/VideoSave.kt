package com.acatapps.videomaker.models


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.acatapps.videomaker.utils.Utils
import java.io.Serializable

@Entity(tableName = "note_table")
data class VideoSave(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "pathList") var pathList: String = "",
    @ColumnInfo(name = "pathMusic") var pathMusic: String = "",
    @ColumnInfo(name = "pathText") var pathText : String = "",
    @ColumnInfo(name = "itemLock") var mCurrentLookupType : Utils.LookupType = Utils.LookupType.NONE,
    @ColumnInfo(name = "transitionCodeId") var transitionCodeId: Int = 0,
    @ColumnInfo(name = "transitionName") var transitionName: String = "",
    @ColumnInfo(name = "time") var time: Int = 0
) : Serializable