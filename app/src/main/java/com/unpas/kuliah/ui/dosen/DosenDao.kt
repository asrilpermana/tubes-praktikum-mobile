package com.unpas.kuliah.ui.dosen

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete

@Dao
interface DosenDao {
    @Query("SELECT * FROM DosenData")
    suspend fun getAllDosens(): List<DosenData>

    @Insert
    suspend fun insertDosen(dosen: DosenData)

    @Update
    suspend fun updateDosen(dosen: DosenData)

    @Delete
    suspend fun deleteDosen(dosen: DosenData)
}