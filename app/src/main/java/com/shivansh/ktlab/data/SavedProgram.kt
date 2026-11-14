package com.shivansh.ktlab.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_programs")
data class SavedProgram(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val code: String,
    val timestamp: Long = System.currentTimeMillis()
)
