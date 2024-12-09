package com.ensinativapackage.ensinativa.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ViewSwitcher
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ensinativapackage.ensinativa.databinding.FragmentNewsBinding
import com.ensinativapackage.ensinativa.model.News
import com.ensinativapackage.ensinativa.viewmodel.adapters.NewsFragmentNewsListAdapter

private lateinit var binding: FragmentNewsBinding

class NewsFragment : Fragment() {
    private lateinit var news: List<News>
    lateinit var viewSwitcher: ViewSwitcher
    var page = 1

    private fun configNews() {
        news = listOf(
            News(
                "Ensinativa - Launch Notes",
                "Notes of Ensinativa launch on Android",
                "file:///android_res/raw/news01.html"
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewsBinding.inflate(inflater, container, false)
        viewSwitcher = binding.newsViewSwitcher
        configNews()
        configNewsAdapter()
        configSwitcher()
        return binding.root
    }

    fun configNewsFromAdapter(news: News) {
        binding.newsWebView.settings.javaScriptEnabled = true
        binding.newsWebView.loadUrl(news.path)
    }

    private fun configSwitcher() {
        binding.newsViewSwitcher.inAnimation = AnimationUtils.loadAnimation(
            requireContext(),
            android.R.anim.slide_in_left
        )
        binding.newsViewSwitcher.outAnimation = AnimationUtils.loadAnimation(
            requireContext(),
            android.R.anim.slide_out_right
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