package com.example.clfilter

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_first.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jsoup.Jsoup

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {
    private val baseUrl = "https://t66y.com/thread0806.php?fid=22"
    private lateinit var getDataJob: Job
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

        btn_refresh.setOnClickListener { refreshData() }
        getData()
    }

    private fun refreshData() {
        release()
        getData()
    }

    private fun getData() {
        getDataJob = GlobalScope.launch(Dispatchers.IO) {
            val base = Jsoup.connect(baseUrl).get().body()
            val main = base.getElementById("main")
            Log.d("apollo", "main: $main")
        }
    }

    override fun onPause() {
        super.onPause()
        release()
    }

    private fun release() {
        if (getDataJob.isActive && !getDataJob.isCancelled) {
            getDataJob.cancel()
        }
    }
}