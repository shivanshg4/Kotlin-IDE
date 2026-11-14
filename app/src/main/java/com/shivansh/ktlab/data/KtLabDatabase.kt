package com.shivansh.ktlab.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SavedProgram::class], version = 1, exportSchema = false)
abstract class KtLabDatabase : RoomDatabase() {
    abstract fun savedProgramDao(): SavedProgramDao

    companion object {
        @Volatile
        private var INSTANCE: KtLabDatabase? = null

        fun getDatabase(context: Context): KtLabDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KtLabDatabase::class.java,
                    "ktlab_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
