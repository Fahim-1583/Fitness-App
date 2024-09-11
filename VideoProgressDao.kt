package com.app.fitlife.room

import androidx.room.*
import java.util.Date

@Dao
interface VideoProgressDao {

    @Query("SELECT * FROM video_progress WHERE date = :date")
        suspend fun getProgressByDate(date: String): VideoProgress?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProgress(videoProgress: VideoProgress)

    @Update
    suspend fun updateProgress(videoProgress: VideoProgress)
}
