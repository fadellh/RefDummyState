package com.example.feature_home.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.feature_home.data.persistance.dao.RefferalDao
import com.example.feature_home.data.persistance.entity.RefferalList



@Database(
    entities = [RefferalList::class],
    version = 2
)

internal abstract class CoreDatabase : RoomDatabase(){

    abstract fun refferalDao() : RefferalDao

    companion object{

        @Volatile
        private var INSTANCE: CoreDatabase? = null

        fun getDatabase(context: Context): CoreDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        CoreDatabase::class.java,
                        REFFERAL_DATABASE
                ).build()
                INSTANCE = instance
                return instance
            }
        }

    }

}

private const val REFFERAL_DATABASE = "refferal_db"
