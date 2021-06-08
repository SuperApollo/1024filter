package com.example.clfilter.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.clfilter.OnlineBean


@Database(entities = [OnlineBean::class], version = 2)

@TypeConverters(TimeStampConverter::class)
abstract class MyDatabase : RoomDatabase() {
    abstract fun onlineBeanDao(): OnlineBeanDao
}
