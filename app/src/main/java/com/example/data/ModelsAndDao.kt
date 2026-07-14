package com.example.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "user_preferences")
data class UserPreference(
    @PrimaryKey val key: String,
    val value: String
)

@Entity(tableName = "study_scans")
data class StudyScan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val prompt: String,
    val response: String,
    val imageBase64: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isBookmarked: Boolean = false
)

@Dao
interface UserPreferenceDao {
    @Query("SELECT * FROM user_preferences WHERE `key` = :key LIMIT 1")
    suspend fun getPreference(key: String): UserPreference?

    @Query("SELECT * FROM user_preferences WHERE `key` = :key LIMIT 1")
    fun getPreferenceFlow(key: String): Flow<UserPreference?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreference(preference: UserPreference)
}

@Dao
interface StudyScanDao {
    @Query("SELECT * FROM study_scans ORDER BY timestamp DESC")
    fun getAllScans(): Flow<List<StudyScan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: StudyScan): Long

    @Query("UPDATE study_scans SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmark(id: Long, isBookmarked: Boolean)

    @Query("DELETE FROM study_scans WHERE id = :id")
    suspend fun deleteScanById(id: Long)

    @Query("DELETE FROM study_scans")
    suspend fun deleteAllScans()
}
