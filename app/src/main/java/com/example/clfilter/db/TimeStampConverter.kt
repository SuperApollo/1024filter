package com.example.clfilter.db

import androidx.room.TypeConverter
import java.sql.Timestamp

class TimeStampConverter {

    @TypeConverter
    fun revertTimeStamp(value: Long?): Timestamp? {
        return value?.let { Timestamp(it) }
    }

    @TypeConverter
    fun converterTimeStamp(value: Timestamp?): Long? {
        return value?.time
    }

}
