package com.example.clfilter

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.clfilter.db.DbConstant
import com.example.clfilter.db.DbConstant.Companion.TABLE_ONLINE_BEAN

@Entity(tableName = TABLE_ONLINE_BEAN)
data class OnlineBean(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var name: String? = null,
    var url: String? = null,
    var comments: String? = null,
    var createTime: String? = null,
    var type: String? = DbConstant.TYPE_ONLINE_VIDEO
) {
    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }
        if (other == null || other !is OnlineBean) {
            return false
        }

        return name == other.name && url == other.url && comments == other.comments && createTime == other.createTime
    }
}