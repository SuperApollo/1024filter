package com.example.clfilter

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.os.FileUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import com.example.clfilter.db.DbHelper
import com.example.clfilter.utils.FileUtil
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : BaseActivity() {
    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        verifyStoragePermissions()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_export_txt -> {
                exportTxt()
                true
            }
            R.id.action_clear_local -> clearLocalData()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun clearLocalData(): Boolean {
        val builder = AlertDialog.Builder(this)
        val dialog = builder.setTitle("确定要清除本地记录吗？")
            .setNegativeButton(
                "取消"
            ) { _, _ -> Log.d(TAG, "取消") }
            .setPositiveButton("确定") { _, _ ->
                DbHelper.getInstance(this).database().onlineBeanDao().deleteAll()
            }.create()
        dialog.show()
        return true
    }

    private fun exportTxt() {
        GlobalScope.launch(Dispatchers.IO) {
            val path =
                this@MainActivity.getExternalFilesDir(null)!!.absolutePath + File.separator + "clfilter"
            val fileName = "highStar.txt"
            val selectAll =
                DbHelper.getInstance(this@MainActivity).database().onlineBeanDao().selectAll()
            val sb = StringBuffer()
            selectAll.forEach {
                sb.append(it.name)
                    .append(", ")
                    .append(it.comments)
                    .append(", ")
                    .append(it.url)
                    .append("\n")
            }
            val result = FileUtil.writeTXT(path, fileName, sb.toString())
            if (result) {
                launch(Dispatchers.Main) {
                    ToastUtil.s(this@MainActivity, "导出成功")
                }
            }
        }
    }

    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE =
        arrayOf(
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"
        )


    private fun verifyStoragePermissions() {
        try {
            //检测是否有写的权限
            val permission = ActivityCompat.checkSelfPermission(
                this,
                "android.permission.WRITE_EXTERNAL_STORAGE"
            );
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
                );
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

