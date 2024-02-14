package com.example.ensinativa.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.webkit.WebView
import android.widget.Button
import android.widget.ViewSwitcher
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ensinativa.R
import com.example.ensinativa.databinding.FragmentMessageBinding
import com.example.ensinativa.databinding.FragmentNewsBinding
import com.example.ensinativa.model.ChatWithHash
import com.example.ensinativa.model.News
import com.example.ensinativa.viewmodel.adapters.MessageFragmentChatAdapter
import com.example.ensinativa.viewmodel.adapters.NewsFragmentNewsListAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

private var firebaseAuth: FirebaseAuth = Firebase.auth

private lateinit var binding: FragmentNewsBinding

class NewsFragment() : Fragment() {
    private lateinit var news: List<News>
    lateinit var viewSwitcher : ViewSwitcher
    var page = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    private fun configNews(){
        news = listOf(News("Ensinativa - Launch Notes","Notes of Ensinativa launch on Android","file:///android_res/raw/news01.html"))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewsBinding.inflate(inflater, container, false)
        viewSwitcher = binding.newsViewSwitcher
        // Carregar o arquivo HTML do diretório res/raw
        configNews()
        configNewsAdapter()
        configSwitcher()
        return binding.root
    }
    fun configNewsFromAdapter(news: News){
        binding.newsWebView.settings.javaScriptEnabled = true
        binding.newsWebView.loadUrl(news.path)
    }
    private fun configSwitcher() {
        binding.newsViewSwitcher.setInAnimation(
            AnimationUtils.loadAnimation(
                requireContext(),
                android.R.anim.slide_in_left
            )
        )
        binding.newsViewSwitcher.setOutAnimation(
            AnimationUtils.loadAnimation(
                requireContext(),
                android.R.anim.slide_out_right
            )
        )
        binding.newsBackButton.setOnClickListener {
            if (page == 2) {
                binding.newsBackButton.startAnimation(
                    AnimationUtils.loadAnimation(
                        requireContext(),
                        androidx.appcompat.R.anim.abc_fade_in
                    )
                )
                binding.newsViewSwitcher.showPrevious()
                page = 1
            }
        }
    }
    private fun configNewsAdapter(){
        val adapter = NewsFragmentNewsListAdapter(requireContext(),news,this)
        val recyclerView = binding.newsListRecyclerView
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

}