package com.example.clfilter

import android.app.Service
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clfilter.db.DbHelper
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
    private var getDataJob: Job? = null
    private val handling = AtomicBoolean(false)
    private var currentPage = 0
    private val onlineBeans: MutableList<OnlineBean> = mutableListOf()
    private var showLimit = 10
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
        "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:10.0) Gecko/20100101 Firefox/10.0 ",
        "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; AcooBrowser; .NET CLR 1.1.4322; .NET CLR 2.0.50727)",
        "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; Acoo Browser; SLCC1; .NET CLR 2.0.50727; Media Center PC 5.0; .NET CLR 3.0.04506)",
        "Mozilla/4.0 (compatible; MSIE 7.0; AOL 9.5; AOLBuild 4337.35; Windows NT 5.1; .NET CLR 1.1.4322; .NET CLR 2.0.50727)",
        "Mozilla/5.0 (Windows; U; MSIE 9.0; Windows NT 9.0; en-US)",
        "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Win64; x64; Trident/5.0; .NET CLR 3.5.30729; .NET CLR 3.0.30729; .NET CLR 2.0.50727; Media Center PC 6.0)",
        "Mozilla/5.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; .NET CLR 1.0.3705; .NET CLR 1.1.4322)",
        "Mozilla/4.0 (compatible; MSIE 7.0b; Windows NT 5.2; .NET CLR 1.1.4322; .NET CLR 2.0.50727; InfoPath.2; .NET CLR 3.0.04506.30)",
        "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN) AppleWebKit/523.15 (KHTML, like Gecko, Safari/419.3) Arora/0.3 (Change: 287 c9dfb30)",
        "Mozilla/5.0 (X11; U; Linux; en-US) AppleWebKit/527+ (KHTML, like Gecko, Safari/419.3) Arora/0.6",
        "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.2pre) Gecko/20070215 K-Ninja/2.1.1",
        "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9) Gecko/20080705 Firefox/3.0 Kapiko/3.0",
        "Mozilla/5.0 (X11; Linux i686; U;) Gecko/20070322 Kazehakase/0.4.5",
        "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.8) Gecko Fedora/1.9.0.8-1.fc10 Kazehakase/0.5.6",
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_3) AppleWebKit/535.20 (KHTML, like Gecko) Chrome/19.0.1036.7 Safari/535.20",
        "Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; fr) Presto/2.9.168 Version/11.52",
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.11 (KHTML, like Gecko) Chrome/20.0.1132.11 TaoBrowser/2.0 Safari/536.11",
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.71 Safari/537.1 LBBROWSER",
        "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; LBBROWSER)",
        "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 732; .NET4.0C; .NET4.0E; LBBROWSER)",
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.84 Safari/535.11 LBBROWSER",
        "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E)",
        "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; QQBrowser/7.0.3698.400)",
        "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 732; .NET4.0C; .NET4.0E)",
        "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; SV1; QQDownload 732; .NET4.0C; .NET4.0E; 360SE)",
        "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 732; .NET4.0C; .NET4.0E)",
        "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E)",
        "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1",
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1",
        "Mozilla/5.0 (iPad; U; CPU OS 4_2_1 like Mac OS X; zh-cn) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8C148 Safari/6533.18.5",
        "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:2.0b13pre) Gecko/20110307 Firefox/4.0b13pre",
        "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:16.0) Gecko/20100101 Firefox/16.0",
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11",
        "Mozilla/5.0 (X11; U; Linux x86_64; zh-CN; rv:1.9.2.10) Gecko/20100922 Ubuntu/10.10 (maverick) Firefox/3.6.10"
    )
    private val delayTimes = arrayOf(1000L, 2000L, 3000L)
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
            val savedList = DbHelper.getInstance(context).database().onlineBeanDao().selectAll()
            Log.d("apollo", "localData: $savedList")
            if (savedList.isNotEmpty()) {
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
                    if (showErrorTime < 2) {
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
            if (onlineBeans.size >= showLimit) {
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
            val time = delayTimes.random()
            delay(time)
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
            while (run) {
                if (getDataJob == null || getDataJob!!.isCancelled) {
                    break
                }
                if (onlineBeans.size > showLimit) {
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
                val responseCount = firstItem.child(3).text().toInt()
                Log.d("apollo", "responseCount: $responseCount")
                if (responseCount < commentsLimit) {
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
                onlineBeans.add(onlineBean)
                onlineBeans.toSet().toMutableList()
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

    override fun onItemLongClick(position: Int, item: OnlineBean) {
        copy(item.url!!)
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

    private fun hideKeyboard() {
        val imm: InputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // 隐藏软键盘
        // 隐藏软键盘
        imm.hideSoftInputFromWindow(requireActivity().window.decorView.windowToken, 0)

    }

}