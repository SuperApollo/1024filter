package com.example.clfilter.db

import androidx.room.*
import com.example.clfilter.OnlineBean

@Dao
abstract class OnlineBeanDao {
    @Insert
    abstract fun insertOne(onlineBean: OnlineBean): Long

    @Update
    abstract fun update(onlineBean: OnlineBean): Int

    @Query("select * from online_bean where id = :id")
    abstract fun selectById(id: Long): OnlineBean?

    @Query("select * from online_bean where url = :url")
    abstract fun selectByUrl(url: String): OnlineBean?

    @Query("select * from online_bean")
    abstract fun selectAll(): List<OnlineBean>

    @Query("select * from online_bean where type = :type")
    abstract fun selectAllByType(type: String): List<OnlineBean>

    @Query("delete from online_bean")
    abstract fun deleteAll()

    @Query("delete from online_bean where type = :type")
    abstract fun deleteAllByType(type: String)

    @Transaction
    open fun saveOrUpdate(onlineBean: OnlineBean) {
        val onlineBeanEntity = selectByUrl(onlineBean.url!!)
        if (onlineBeanEntity == null) {
            insertOne(onlineBean)
        } else {
            onlineBeanEntity.name = onlineBean.name
            onlineBeanEntity.url = onlineBean.url
            onlineBeanEntity.comments = onlineBean.comments
            update(onlineBeanEntity)
        }
    }
}