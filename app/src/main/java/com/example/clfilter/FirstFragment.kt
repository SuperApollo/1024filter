package com.example.clfilter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clfilter.db.DbConstant
import com.example.clfilter.db.DbHelper
import com.example.clfilter.network.Params
import kotlinx.android.synthetic.main.fragment_first.*
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.concurrent.atomic.AtomicBoolean


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : BaseFragment(), OnItemLongClickListener, ItemClickListener {
    private var getDataJob: Job? = null
    private val handling = AtomicBoolean(false)
    private var currentPage = 0
    private val onlineBeans: MutableList<OnlineBean> = mutableListOf()
    lateinit var myAdapter: MyAdapter
    private var showErrorTime = 0
    private var run = true

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
        btn_refresh.setOnClickListener {
            hideKeyboard()
            refreshData(true)
        }
        et_comments_limit.addTextChangedListener {
            it?.let { t ->
                if (it.toString().isEmpty())
                    return@let
                Params.commentsLimit = t.toString().toInt()
            }
        }
        et_show_limit.addTextChangedListener {
            it?.let { r ->
                if (r.toString().isEmpty())
                    return@let
                Params.showLimit = r.toString().toInt()
            }
        }
        swipe.setOnRefreshListener {
            refreshData(false)
            swipe.isRefreshing = false
        }
        btn_to_91.setOnClickListener { findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment) }
        getLocalData()
    }

    private fun refreshData(add: Boolean) {
        release()
        if (!add) {
            onlineBeans.clear()
        }
        currentPage = 0
        refreshShow()
        getData()
    }

    private fun getLocalData() {
        GlobalScope.launch(Dispatchers.IO) {
            //获取本地记录
            val savedList = DbHelper.getInstance(context).database().onlineBeanDao()
                .selectAllByType(DbConstant.TYPE_ONLINE_VIDEO)
            if (savedList.isNotEmpty()) {
                onlineBeans.clear()
                onlineBeans.addAll(savedList)
                launch(Dispatchers.Main) {
                    myAdapter.notifyDataSetChanged()
                    tv_total_item?.text = "历史记录：${onlineBeans.size}条"
                }
            }
        }
    }

    private fun getData() {
        if (handling.get()) {
            return
        }
        progress.visibility = View.VISIBLE
        getDataJob = GlobalScope.launch(Dispatchers.IO) {
            try {
                val index = Jsoup.connect(Params.baseUrl)
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
                    tv_info?.text = "主题: ${topics.text()}, 文章: ${articles.text()}"
                }
                val link = target.select("a").first()
                val absHref = link.attr("abs:href")
                run = true
                parseOnlineVideoPage(absHref)
                launch(Dispatchers.Main) { progress?.visibility = View.GONE }
            } catch (e: Exception) {
                e.printStackTrace()
                launch(Dispatchers.Main) { progress?.visibility = View.GONE }
                val args = Bundle()
                args.putString(Constants.ARG_ERROR_MESSAGE, e.toString())
                try {
                    if (showErrorTime < 2 && run) {
                        findNavController().navigate(
                            R.id.action_FirstFragment_to_errorPageFragment,
                            args
                        )
                        showErrorTime++
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } finally {
                handling.compareAndSet(true, false)
            }
        }
    }

    private suspend fun parseOnlineVideoPage(absHref: String) {
        while (run) {
            if (getDataJob == null || getDataJob!!.isCancelled) {
                break
            }
            if (onlineBeans.size >= Params.showLimit) {
                break
            }
            currentPage++
            Log.d("apollo", "currentPage: $currentPage")
            val pageUrl = "$absHref&search=&page=$currentPage"
            Log.d("apollo", "pageUrl:$pageUrl")
            val page = Jsoup.connect(pageUrl)
                .headers(getHeaders())
                .get()
                .body()
            val main = page.getElementById("main")
            val listElement = main.child(5).child(0)
            val realList = listElement.child(1)
            parseLine(realList)
            GlobalScope.launch(Dispatchers.Main) {
                tv_total_item?.text = "共搜索到 ${onlineBeans.size} 条数据,第 $currentPage 页"
            }
            val time = Params.delayTimes.random()
            delay(time)
        }
    }

    private fun getHeaders(): MutableMap<String, String>? {
        val header = mutableMapOf<String, String>()
//        header["Host"] = "https://t66y.com"
        header["User-Agent"] = Params.my_headers.random()
        header["Accept"] = "  text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"
        header["Accept-Language"] = "zh-cn,zh;q=0.5"
        header["Accept-Charset"] = "  GB2312,utf-8;q=0.7,*;q=0.7"
        header["Connection"] = "keep-alive"
        return header
    }

    private fun parseLine(realList: Element) {
        try {
            var index = -1
            while (run) {
                if (getDataJob == null || getDataJob!!.isCancelled) {
                    break
                }
                if (onlineBeans.size >= Params.showLimit) {
                    break
                }
                index++
                if (index > realList.childrenSize() - 1) {
                    break
                }
                val firstItem = realList.child(index)
                if (firstItem.childrenSize() < 2) {
                    continue
                }
                val tal = firstItem.child(1)
                val time = firstItem.child(2).child(1).text()
                val responseCount = firstItem.child(3).text().toInt()
                Log.d("apollo", "responseCount: $responseCount")
                if (responseCount < Params.commentsLimit) {
                    continue
                }

                val h3 = tal.child(0)
                val url = h3.select("a").first().attr("abs:href")
                val name = h3.text()
                if (et_key_words?.text != null && !name.contains(et_key_words.text)) {
                    continue
                }
                val onlineBean = OnlineBean()
                onlineBean.name = name
                onlineBean.url = url
                onlineBean.comments = responseCount.toString()
                onlineBean.type = DbConstant.TYPE_ONLINE_VIDEO
                onlineBean.createTime = time
                if (onlineBeans.contains(onlineBean)) {
                    continue
                }
                onlineBeans.add(onlineBean)
                onlineBeans.sortBy { it.comments }
                onlineBeans.reverse()
                DbHelper.getInstance(context).database().onlineBeanDao().saveOrUpdate(onlineBean)
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
        tv_total_item?.text = "共搜索到 ${onlineBeans.size} 条数据,第 $currentPage 页"
    }

    override fun onDestroy() {
        super.onDestroy()
        release()
    }

    private fun release() {
        run = false
        if (getDataJob == null) {
            return
        }
        if (getDataJob!!.isActive && !getDataJob!!.isCancelled) {
            getDataJob!!.cancel()
            getDataJob = null
        }
    }

    override fun onItemLongClick(position: Int, bean: OnlineBean) {
        copy(bean.url!!)
        ToastUtil.s(context, "复制成功!")
        vibrate()
    }

    override fun onItemClick(position: Int, bean: OnlineBean) {
        findNavController().navigate(
            R.id.action_FirstFragment_to_WebFragment,
            bundleOf(Constants.BUNDLE_TAG_WEB_URL to bean.url)
        )
    }

}