package com.example.ensinativa.viewmodel.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.ensinativa.R

class ProfileFragmentTagsAdapter(private val context: Context, tags: List<String>) : RecyclerView.Adapter<ProfileFragmentTagsAdapter.ViewHolder>() {
    private val tags = tags.toMutableList()
    private var tagsLastIndex = tags.lastIndex

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(tagList: List<String>){
            println("Chegou ate aqui1")
            for ((index, value) in tagList.withIndex()) {
                println("Value" + index)
                when (index) {
                    0 -> {
                        val tag1 =
                            itemView.findViewById<Button>(R.id.fragmentProfileSkillsRecyclerViewTag1)
                        tag1.text = value
                        tag1.visibility = View.VISIBLE
                        val layout =
                            itemView.findViewById<LinearLayout>(R.id.fragmentProfileSkillsLinearLayout)
                        layout.visibility = View.VISIBLE
                    }

                    1 -> {
                        val tag2 =
                            itemView.findViewById<Button>(R.id.fragmentProfileSkillsRecyclerViewTag2)
                        tag2.text = value
                        tag2.visibility = View.VISIBLE
                    }

                    2 -> {
                        val tag3 =
                            itemView.findViewById<Button>(R.id.fragmentProfileSkillsRecyclerViewTag3)
                        tag3.text = value
                        tag3.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.fragment_profile_skills_recyclerview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tagList = getTagListForPosition(position)
        if(tagList.isNotEmpty()){
            holder.bind(tagList)
        }
    }

    override fun getItemCount(): Int = tags.size

    fun refresh(tags: List<String>) {
        this.tags.clear()
        this.tags.addAll(tags)
        this.tagsLastIndex = tags.size
        notifyDataSetChanged()
    }

    private fun getTagListForPosition(position: Int): List<String> {
        return if(tagsLastIndex <=3){
            if(position % 3 ==0){
                tags.subList(position,tagsLastIndex + 1)
            }else{
                emptyList()
            }
        }else{
            if(position % 3 ==0){
                if (position+2 > tagsLastIndex ){
                    tags.subList(position,tagsLastIndex +1 )
                }else{
                    tags.subList(position,position+3)
                }
            }else{
                emptyList()
            }
        }
    }
}



