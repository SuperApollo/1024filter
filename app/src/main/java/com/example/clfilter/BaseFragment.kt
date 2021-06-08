package com.example.clfilter

import android.app.Service
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Vibrator
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

open class BaseFragment : Fragment() {
    protected fun hideKeyboard() {
        val imm: InputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // 隐藏软键盘
        // 隐藏软键盘
        imm.hideSoftInputFromWindow(requireActivity().window.decorView.windowToken, 0)

    }

    protected fun vibrate() {
        val vib = requireContext().getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        vib.vibrate(50)
    }

    //复制
    protected fun copy(data: String) {
        // 获取系统剪贴板
        val clipboard: ClipboardManager? =
            context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        // 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）,其他的还有
        // newHtmlText、
        // newIntent、
        // newUri、
        // newRawUri
        val clipData = ClipData.newPlainText(null, data)

        // 把数据集设置（复制）到剪贴板
        clipboard?.setPrimaryClip(clipData)
    }
}