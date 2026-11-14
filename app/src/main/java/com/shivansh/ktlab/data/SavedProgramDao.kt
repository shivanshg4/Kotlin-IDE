package com.shivansh.ktlab.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedProgramDao {
    @Query("SELECT * FROM saved_programs ORDER BY timestamp DESC")
    fun getAllPrograms(): Flow<List<SavedProgram>>

    @Query("SELECT * FROM saved_programs WHERE id = :id")
    suspend fun getProgramById(id: Long): SavedProgram?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgram(program: SavedProgram): Long

    @Update
    suspend fun updateProgram(program: SavedProgram)

    @Delete
    suspend fun deleteProgram(program: SavedProgram)
}
