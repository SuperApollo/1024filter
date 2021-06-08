package com.example.clfilter.utils

import android.util.Log
import android.widget.Toast

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception


object FileUtil {
    const val TAG = "FileUtil"

    /**
     * 功能：已知字符串内容，输出到文件
     *
     * @param filePath 要写文件的文件路径，如：data/data/com.test/files
     * @param fileName 要写文件的文件名，如：abc.txt
     * @param string   要写文件的文件内容
     */
    fun writeTXT(filePath: String, fileName: String, string: String): Boolean {
        var fileOutputStream: FileOutputStream? = null
        var result = false
        try {
            val file = File(filePath)

            // 首先判断文件夹是否存在
            if (!file.exists() || !file.isDirectory) {
                if (!file.mkdir()) {   // 文件夹不存在则创建文件
                    Log.e(TAG, "文件夹$file 创建失败")
                }
            } else {
                val fileWrite = File(filePath + File.separator + fileName)

                // 首先判断文件是否存在
                if (!fileWrite.exists()) {
                    if (!fileWrite.createNewFile()) {   // 文件不存在则创建文件
                        Log.e(TAG, "文件创建失败")
                    }
                }
                // 实例化对象：文件输出流
                fileOutputStream = FileOutputStream(fileWrite)

                // 写入文件
                fileOutputStream.write(string.toByteArray())

                // 清空输出流缓存
                fileOutputStream.flush()
                Log.i(TAG, "$fileWrite 写入成功")
                result = true
            }
        } catch (e: IOException) {
            e.printStackTrace()
            result = false
        } catch (e: Exception) {
            e.printStackTrace()
            result = false
        } finally {
            try {// 关闭输出流
                fileOutputStream?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }
    }

}