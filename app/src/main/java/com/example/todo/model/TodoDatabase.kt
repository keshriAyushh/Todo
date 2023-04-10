package com.example.todo.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = [Todo::class], version = 1)

abstract class TodoDatabase : RoomDatabase() {

    abstract fun todoDao(): TodoDao

    companion object{

        @Volatile // Volatile annotation notifies all the threads about the changed value of the variable
        private var INSTANCE: TodoDatabase? = null

        fun getDatabase(context: Context): TodoDatabase{
            if(INSTANCE == null){
                //there's a possibility that more than one thread try to create the database at the same time
                //due to which there will be multiple databases created
                //synchronized block makes sure that the instance is created only once
                synchronized(this){
                    INSTANCE = Room.databaseBuilder(
                        context,
                        TodoDatabase::class.java,
                        "contactDB"
                    ) .build()

                }

            }
            //!! makes the instance non nullable since we are sure that the instance is not null
            return INSTANCE!!
        }
    }
}