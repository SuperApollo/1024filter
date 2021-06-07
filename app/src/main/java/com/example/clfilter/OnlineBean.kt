package com.example.clfilter

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.clfilter.db.DbConstant.Companion.TABLE_ONLINE_BEAN

@Entity(tableName = TABLE_ONLINE_BEAN)
data class OnlineBean(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var name: String? = null,
    var url: String? = null,
    var comments: String? = null
)