package com.app.fitlife.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "video_progress")
data class VideoProgress(
    @PrimaryKey val date: String,
    var seconds: Int
)
