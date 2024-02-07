package com.hoxy.hlivv.ui.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hoxy.hlivv.databinding.FragmentMyBinding

class MyFragment : Fragment() {
    private var _binding: FragmentMyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyBinding.inflate(inflater, container, false)
        //웹뷰 테스트
//        val myWebView: WebView = binding.webview
//        val webSettings = myWebView.settings
//        webSettings.javaScriptEnabled = true
//        myWebView.loadUrl("http://192.168.100.66:5173/")
        //myWebView.loadUrl("http://10.0.2.2:5173")
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}