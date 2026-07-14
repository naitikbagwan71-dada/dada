package com.example.data

import kotlinx.coroutines.flow.Flow

class StudyRepository(private val database: AppDatabase) {
    private val preferenceDao = database.userPreferenceDao()
    private val scanDao = database.studyScanDao()

    fun getPreferenceFlow(key: String): Flow<UserPreference?> = preferenceDao.getPreferenceFlow(key)

    suspend fun getPreference(key: String): String? = preferenceDao.getPreference(key)?.value

    suspend fun savePreference(key: String, value: String) {
        preferenceDao.insertPreference(UserPreference(key, value))
    }

    val allScans: Flow<List<StudyScan>> = scanDao.getAllScans()

    suspend fun insertScan(prompt: String, response: String, imageBase64: String?): Long {
        return scanDao.insertScan(StudyScan(prompt = prompt, response = response, imageBase64 = imageBase64))
    }

    suspend fun deleteScan(id: Long) {
        scanDao.deleteScanById(id)
    }

    suspend fun updateBookmark(id: Long, isBookmarked: Boolean) {
        scanDao.updateBookmark(id, isBookmarked)
    }

    suspend fun clearHistory() {
        scanDao.deleteAllScans()
    }
}
