package com.ensinativapackage.ensinativa.viewmodel.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.ensinativapackage.ensinativa.R
import com.ensinativapackage.ensinativa.model.News
import com.ensinativapackage.ensinativa.view.NewsFragment

class NewsFragmentNewsListAdapter(private val context: Context, private val newsList: List<News>,private val newsFragment: NewsFragment) :
    RecyclerView.Adapter<NewsFragmentNewsListAdapter.ViewHolder>() {

    class ViewHolder(private val view: View, private val newsFragment: NewsFragment) : RecyclerView.ViewHolder(view) {
        fun bind(news: News){
            val newsTitle = view.findViewById<TextView>(R.id.fragmentNewsNewsTitle)
            val newsDescription = view.findViewById<TextView>(R.id.fragmentNewsNewsDescription)
            val newsLayout : ConstraintLayout = view.findViewById(R.id.fragmentNewsNewsLayout)
            newsTitle.text = news.title
            newsDescription.text = news.description
            newsLayout.setOnClickListener{
                newsFragment.configNewsFromAdapter(news)
                newsFragment.page = 2
                newsFragment.viewSwitcher.showNext()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.frament_news_news_recycler_view, parent, false)
        return ViewHolder(view,newsFragment)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(newsList[position])
    }

    override fun getItemCount() = newsList.size
}



