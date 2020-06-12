package com.example.clfilter

import android.app.Service
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_first.*
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.concurrent.atomic.AtomicBoolean


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : BaseFragment(), OnItemLongClickListener {
    private val baseUrl = "https://t66y.com/index.php"
    private lateinit var getDataJob: Job
    private val handling = AtomicBoolean(false)
    private var currentPage = 0
    private val onlineBeans: MutableList<OnlineBean> = mutableListOf()
    private var showLimit = 100
    private var commentsLimit = 3
    lateinit var myAdapter: MyAdapter
    private var showErrorTime = 0
    val my_headers = arrayOf(
        "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36",
        "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:30.0) Gecko/20100101 Firefox/30.0",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.75.14 (KHTML, like Gecko) Version/7.0.3 Safari/537.75.14",
        "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Win64; x64; Trident/6.0)",
        "Mozilla/5.0 (Windows; U; Windows NT 5.1; it; rv:1.8.1.11) Gecko/20071127 Firefox/2.0.0.11",
        "Opera/9.25 (Windows NT 5.1; U; en)",
        "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322; .NET CLR 2.0.50727)",
        "Mozilla/5.0 (compatible; Konqueror/3.5; Linux) KHTML/3.5.5 (like Gecko) (Kubuntu)",
        "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.0.12) Gecko/20070731 Ubuntu/dapper-security Firefox/1.5.0.12",
        "Lynx/2.8.5rel.1 libwww-FM/2.14 SSL-MM/1.4.1 GNUTLS/1.2.9",
        "Mozilla/5.0 (X11; Linux i686) AppleWebKit/535.7 (KHTML, like Gecko) Ubuntu/11.04 Chromium/16.0.912.77 Chrome/16.0.912.77 Safari/535.7",
        "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:10.0) Gecko/20100101 Firefox/10.0 "
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_first).setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        myAdapter = MyAdapter(this, onlineBeans)
        recyclerview.adapter = myAdapter
        recyclerview.layoutManager = LinearLayoutManager(context)
        btn_refresh.setOnClickListener { refreshData() }
        et_comments_limit.addTextChangedListener {
            it?.let { t ->
                if (it.toString().isEmpty())
                    return@let
                commentsLimit = t.toString().toInt()
            }
        }
        et_show_limit.addTextChangedListener {
            it?.let { r ->
                if (r.toString().isEmpty())
                    return@let
                showLimit = r.toString().toInt()
            }
        }
        btn_to_91.setOnClickListener { findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment) }
        getData()
    }

    private fun refreshData() {
        release()
        onlineBeans.clear()
        currentPage = 0
        refreshShow()
        getData()
    }

    private fun getData() {
        if (handling.get()) {
            return
        }
        progress.visibility = View.VISIBLE
        getDataJob = GlobalScope.launch(Dispatchers.IO) {
            try {
                val index = Jsoup.connect(baseUrl)
                    .headers(getHeaders())
                    .get()
                    .body()
                val indexMain = index.getElementById("main")
                val child = indexMain.child(1)
                val cate1 = child.getElementById("cate_1")//主页
                val onlineVideo = cate1.child(8)//在线yp板块
                val target = onlineVideo.child(1).allElements
                val topics = onlineVideo.child(2).getElementsByClass("f12")
                val articles = onlineVideo.child(3).getElementsByClass("f12")
                launch(Dispatchers.Main) {
                    tv_info.text = "主题: ${topics.text()}, 文章: ${articles.text()}"
                }
                val link = target.select("a").first()
                val absHref = link.attr("abs:href")
                parseOnlineVideoPage(absHref)
                launch(Dispatchers.Main) { progress?.visibility = View.GONE }
            } catch (e: Exception) {
                e.printStackTrace()
                launch(Dispatchers.Main) { progress?.visibility = View.GONE }
                handling.compareAndSet(true, false)
                val args = Bundle()
                args.putString(Constants.ARG_ERROR_MESSAGE, e.toString())
                if (showErrorTime < 2) {
                    findNavController().navigate(
                        R.id.action_FirstFragment_to_errorPageFragment,
                        args
                    )
                    showErrorTime++
                }
            }
        }
    }

    private suspend fun parseOnlineVideoPage(absHref: String) {
        while (true) {
            if (getDataJob.isCancelled) {
                break
            }
            if (onlineBeans.size > showLimit) {
                break
            }
            currentPage++
            Log.d("apollo", "currentPage: $currentPage")
            val pageUrl = "$absHref&search=&page=$currentPage"
            val page = Jsoup.connect(pageUrl)
                .headers(getHeaders())
                .get()
                .body()
            val main = page.getElementById("main")
            val listElement = main.child(4).child(0)
            val realList = listElement.child(1)
            parseLine(realList)
            delay(100)
        }
    }

    private fun getHeaders(): MutableMap<String, String>? {
        val header = mutableMapOf<String, String>()
//        header["Host"] = "https://t66y.com"
        header["User-Agent"] = my_headers.random()
        header["Accept"] = "  text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"
        header["Accept-Language"] = "zh-cn,zh;q=0.5"
        header["Accept-Charset"] = "  GB2312,utf-8;q=0.7,*;q=0.7"
        header["Connection"] = "keep-alive"
        return header
    }

    private fun parseLine(realList: Element) {
        try {
            var index = -1
            while (true) {
                if (getDataJob.isCancelled) {
                    break
                }
                if (onlineBeans.size > showLimit) {
                    break
                }
                index++
                val firstItem = realList.child(9 + index)
                val tal = firstItem.child(1)
                val responseCount = firstItem.child(3).text().toInt()
                Log.d("apollo", "responseCount: $responseCount")
                if (responseCount < commentsLimit) {
                    continue
                }
                val h3 = tal.child(0)
                val url = h3.select("a").first().attr("abs:href")
                val name = h3.text()
                val onlineBean = OnlineBean(name, url, responseCount.toString())
                onlineBeans.add(onlineBean)
                onlineBeans.sortBy { it.comments }
                onlineBeans.reverse()
                GlobalScope.launch(Dispatchers.Main) {
                    refreshShow()
                }
                Log.d("apollo", name + url)


            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun refreshShow() {
        myAdapter.notifyDataSetChanged()
        tv_total_item.text = "共搜索到 ${onlineBeans.size} 条数据,第 $currentPage 页"
    }

    override fun onDestroy() {
        super.onDestroy()
        release()
    }

    private fun release() {
        if (getDataJob.isActive && !getDataJob.isCancelled) {
            getDataJob.cancel()
        }
    }

    override fun onItemLongClick(position: Int, item: OnlineBean) {
        copy(item.url)
        ToastUtil.s(context, "复制成功!")
        vibrate()
    }

    private fun vibrate() {
        val vib = requireContext().getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        vib.vibrate(50)
    }

    //复制
    private fun copy(data: String) {
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