package com.ensinativapackage.ensinativa.viewmodel.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.ensinativapackage.ensinativa.R
import com.ensinativapackage.ensinativa.model.User
import com.ensinativapackage.ensinativa.view.AddTagDialogFragment
import com.ensinativapackage.ensinativa.view.ProfileFragment
import com.ensinativapackage.ensinativa.view.RemoveTagFragment

class ProfileFragmentTagsAdapter(
    private val context: Context,
    tags: List<String>,
    private var editMode: Boolean,
    private val fragmentManager: FragmentManager,
    private val profileFragment: ProfileFragment
) : RecyclerView.Adapter<ProfileFragmentTagsAdapter.ViewHolder>() {
    private var listOfTags = tags.toMutableList()
    private var tagsLastIndex = tags.lastIndex

    init {
        if (tags.isEmpty()) {
            listOfTags.add("Add tag")
            tagsLastIndex = listOfTags.lastIndex
        } else {
            if (editMode) {
                listOfTags.add("Add tag")
                tagsLastIndex = listOfTags.lastIndex
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.fragment_profile_skills_recyclerview, parent, false)
        return ViewHolder(view, context, editMode, fragmentManager, profileFragment)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(listOfTags.isEmpty()){
            holder.bind(listOf("Add tag"))
        }else{
            val tagList = getTagListForPosition(position)
            holder.bind(tagList)
        }
    }

    override fun getItemCount(): Int {
        return if (listOfTags.isEmpty()) {
            1
        } else {
            listOfTags.size
        }
    }

    inner class ViewHolder(
        view: View,
        val context: Context,
        private var editMode: Boolean,
        private val fragmentManager: FragmentManager,
        private val profileFragment: ProfileFragment
    ) : RecyclerView.ViewHolder(view) {
        fun bind(tagList: List<String>) {
            if (listOfTags[0] == "Add tag") {
                editMode = true
            }
            for ((index, value) in tagList.withIndex()) {
                when (index) {
                    0 -> {
                        val tag1 =
                            itemView.findViewById<Button>(R.id.fragmentProfileSkillsRecyclerViewTag1)
                        tag1.text = value
                        tag1.visibility = View.VISIBLE
                        val layout =
                            itemView.findViewById<LinearLayout>(R.id.fragmentProfileSkillsLinearLayout)
                        layout.visibility = View.VISIBLE

                        val image1 =
                            itemView.findViewById<Button>(R.id.fragmentProfileSkillsRecyclerViewTag1Image)
                        if (editMode) {
                            image1.visibility = View.VISIBLE
                            if (value == "Add tag") {
                                image1.foreground =
                                    ContextCompat.getDrawable(context, R.drawable.ic_add_foreground)
                                image1.setOnClickListener {
                                    image1.startAnimation(
                                        android.view.animation.AnimationUtils.loadAnimation(
                                            context,
                                            androidx.appcompat.R.anim.abc_fade_in
                                        )
                                    )
                                    configAddTagFragment()
                                }
                            } else {
                                image1.setOnClickListener {
                                    image1.startAnimation(android.view.animation.AnimationUtils.loadAnimation(context, androidx.appcompat.R.anim.abc_fade_in))
                                    configRemoveTagFragment(value)
                                }
                            }
                        } else {
                            val paddingEndInDp = 2
                            val scale = context.resources.displayMetrics.density
                            val paddingEndInPx = (paddingEndInDp * scale + 0.5f).toInt()
                            tag1.setPadding(tag1.paddingStart, tag1.paddingTop, paddingEndInPx, tag1.paddingBottom)
                        }
                    }

                    1 -> {
                        val tag2 = itemView.findViewById<Button>(R.id.fragmentProfileSkillsRecyclerViewTag2)
                        tag2.text = value
                        tag2.visibility = View.VISIBLE
                        val image2 = itemView.findViewById<Button>(R.id.fragmentProfileSkillsRecyclerViewTag2Image)
                        if (editMode) {
                            image2.visibility = View.VISIBLE
                            if (value == "Add tag") {
                                image2.foreground = ContextCompat.getDrawable(context, R.drawable.ic_add_foreground)
                                image2.setOnClickListener {
                                    image2.startAnimation(android.view.animation.AnimationUtils.loadAnimation(context, androidx.appcompat.R.anim.abc_fade_in))
                                    configAddTagFragment()
                                }
                            } else {
                                image2.setOnClickListener {
                                    image2.startAnimation(android.view.animation.AnimationUtils.loadAnimation(context, androidx.appcompat.R.anim.abc_fade_in))
                                    configRemoveTagFragment(value)
                                }
                            }
                        } else {
                            val paddingEndInDp = 2
                            val scale = context.resources.displayMetrics.density
                            val paddingEndInPx = (paddingEndInDp * scale + 0.5f).toInt()
                            tag2.setPadding(tag2.paddingStart, tag2.paddingTop, paddingEndInPx, tag2.paddingBottom)
                        }
                    }

                    2 -> {
                        val tag3 = itemView.findViewById<Button>(R.id.fragmentProfileSkillsRecyclerViewTag3)
                        tag3.text = value
                        tag3.visibility = View.VISIBLE
                        val image3 = itemView.findViewById<Button>(R.id.fragmentProfileSkillsRecyclerViewTag3Image)
                        if (editMode) {
                            image3.visibility = View.VISIBLE
                            if (value == "Add tag") {
                                image3.foreground = ContextCompat.getDrawable(context, R.drawable.ic_add_foreground)
                                image3.setOnClickListener {
                                    image3.startAnimation(android.view.animation.AnimationUtils.loadAnimation(context, androidx.appcompat.R.anim.abc_fade_in))
                                    configAddTagFragment()
                                }
                            } else {
                                image3.setOnClickListener {
                                    image3.startAnimation(android.view.animation.AnimationUtils.loadAnimation(context, androidx.appcompat.R.anim.abc_fade_in))
                                    configRemoveTagFragment(value)
                                }
                            }
                        } else {
                            val paddingEndInDp = 2
                            val scale = context.resources.displayMetrics.density
                            val paddingEndInPx = (paddingEndInDp * scale + 0.5f).toInt()
                            tag3.setPadding(tag3.paddingStart, tag3.paddingTop, paddingEndInPx, tag3.paddingBottom)
                        }
                    }
                }
            }
        }

        private fun configRemoveTagFragment(tag: String) {
            if (fragmentManager.fragments.isEmpty()) {
                val newTags = profileFragment.user.tags.toMutableList()
                newTags.remove(tag)
                RemoveTagFragment(
                    User(
                        profileFragment.user.uid,
                        profileFragment.user.displayName,
                        profileFragment.user.email,
                        profileFragment.user.description,
                        profileFragment.user.achievements,
                        newTags.distinct(),
                        profileFragment.user.imageSrc,
                        profileFragment.user.rating
                    ), profileFragment
                ).show(fragmentManager, "CustomFragment")
            }
        }

        private fun configAddTagFragment() {
            if (fragmentManager.fragments.isEmpty()) {
                AddTagDialogFragment(profileFragment.user, profileFragment).show(
                    fragmentManager,
                    "CustomFragment"
                )
            }
        }
    }

    private fun getTagListForPosition(position: Int): List<String> {
        if (tagsLastIndex <= 3) {
            return if (position % 3 == 0) {
                listOfTags.subList(position, tagsLastIndex + 1)
            } else {
                emptyList()
            }
        } else {
            return if (position % 3 == 0) {
                if (position + 2 > tagsLastIndex) {
                    listOfTags.subList(position, tagsLastIndex + 1)
                } else {
                    listOfTags.subList(position, position + 3)
                }
            } else {
                emptyList()
            }
        }
    }

    fun refresh(tags: List<String>) {
        listOfTags.clear()
        listOfTags.addAll(tags)
        tagsLastIndex = tags.size
        notifyDataSetChanged()
    }
}
