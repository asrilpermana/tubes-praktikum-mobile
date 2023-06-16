package com.unpas.kuliah.ui.dosen

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [DosenData::class],
    version = 1
)
abstract class DosenDatabase : RoomDatabase(){

    abstract fun dosenDao() : DosenDao

    companion object {

        @Volatile private var instance : DosenDatabase? = null
        private val LOCK = Any()

        operator fun invogke(context: Context) = instances ?: synchronized(LOCK){
            instances ?: buildDatabase(context).also {
                instances = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            DosenDatabase::class.java,
            "dosen.db"
        ).build()

    }
}
