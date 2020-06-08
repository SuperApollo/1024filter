package com.example.clfilter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_first.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.concurrent.atomic.AtomicBoolean


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment(), OnItemLongClickListener {
    private val baseUrl = "https://t66y.com/index.php"
    private lateinit var getDataJob: Job
    private val handling = AtomicBoolean(false)
    private var currentPage = 0
    private val onlineBeans: MutableList<OnlineBean> = mutableListOf()
    private var maxItems = 10
    private var commentsLimit = 3
    lateinit var myAdapter: MyAdapter
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
        getData()
    }

    private fun refreshData() {
        release()
        onlineBeans.clear()
        getData()
    }

    private fun getData() {
        if (handling.get()) {
            return
        }
        progress.visibility = View.VISIBLE
        getDataJob = GlobalScope.launch(Dispatchers.IO) {
            try {
                val index = Jsoup.connect(baseUrl).get().body()
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
                launch(Dispatchers.Main) { progress.visibility = View.GONE }
            } catch (e: Exception) {
                e.printStackTrace()
                launch(Dispatchers.Main) { progress.visibility = View.GONE }
                handling.compareAndSet(true, false)
            }
        }
    }

    private fun parseOnlineVideoPage(absHref: String) {
        while (true) {
            if (onlineBeans.size > maxItems) {
                break
            }
            if (getDataJob.isCancelled) {
                break
            }
            currentPage++
            Log.d("apollo", "currentPage: $currentPage")
            val pageUrl = "$absHref&search=&page=$currentPage"
            val page = Jsoup.connect(pageUrl).get().body()
            val main = page.getElementById("main")
            val listElement = main.child(4).child(0)
            val realList = listElement.child(1)
            parseLine(realList)
        }
    }

    private fun parseLine(realList: Element) {
        try {
            var index = -1
            while (true) {
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
                GlobalScope.launch(Dispatchers.Main) { myAdapter.notifyDataSetChanged() }
                Log.d("apollo", name + url)


            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

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
        ToastUtil.s(context, "复制成功!")
        copy(item.url)
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