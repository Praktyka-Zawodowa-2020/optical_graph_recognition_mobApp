package com.pzpg.ogr

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment


class AboutFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_about, container, false)

        // Inflate the layout for this fragment

        val htmlAsString = getString(R.string.about) // used by WebView


        val webView: WebView? = view?.findViewById<WebView>(R.id.webView)
        webView?.let {
            webView.loadDataWithBaseURL(null, htmlAsString, "text/html", "utf-8", null)
        }

        return view
    }
}