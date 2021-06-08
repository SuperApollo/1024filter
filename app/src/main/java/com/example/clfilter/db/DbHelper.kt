package com.example.clfilter.db

import android.content.Context
import androidx.room.Room

class DbHelper private constructor(context: Context) {
    private var myDatabase: MyDatabase =
        Room.databaseBuilder(context.applicationContext, MyDatabase::class.java, DbConstant.DB_NAME)
            .addMigrations(MIGRATION_1_2)
            .build()

    fun database(): MyDatabase {
        return myDatabase
    }

    fun release() {
        myDatabase.close()
        dbHelper = null
    }

    companion object {

        private var dbHelper: DbHelper? = null

        fun getInstance(context: Context?): DbHelper {
            if (null == dbHelper) {
                synchronized(DbHelper::class.java) {
                    if (null == dbHelper) {
                        dbHelper = DbHelper(context!!.applicationContext)
                    }
                }
            }

            return dbHelper!!
        }
    }

}
