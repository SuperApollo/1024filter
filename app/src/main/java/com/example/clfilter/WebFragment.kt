package com.example.clfilter

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.fragment_web.*


class WebFragment : BaseFragment() {
    private val TAG = "WebFragment"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_web, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val url = arguments?.getString(Constants.BUNDLE_TAG_WEB_URL)
        val innerShow = arguments?.getBoolean(Constants.BUNDLE_TAG_INNERSHOW, true) ?: true
        if (innerShow) {
            web.webViewClient = (object : WebViewClient() {
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    web.loadUrl(request?.url!!.toString())
                    return true
                }

                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    view?.loadUrl(url)
                    return true
                }
            })
            web.settings.useWideViewPort = true
            web.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            web.settings.loadWithOverviewMode = true
            web.settings.setSupportZoom(true)
            web.settings.builtInZoomControls = true
            web.settings.displayZoomControls = false
            web.settings.javaScriptEnabled = true
        }

        web.loadUrl(url)
    }

    override fun onPause() {
        super.onPause()
        web.onPause()
    }

    override fun onResume() {
        super.onResume()
        web.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        web?.let {
            webParent.removeView(it)
        }
        web?.destroy()
    }
}