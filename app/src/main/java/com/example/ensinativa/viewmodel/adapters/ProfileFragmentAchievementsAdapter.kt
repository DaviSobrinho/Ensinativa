package com.example.ensinativa.viewmodel.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.ensinativa.R
import com.example.ensinativa.firebasestorage.FirebaseStorageCommons
import com.example.ensinativa.firebasestorage.FirebaseStorageListener
import com.example.ensinativa.model.Achievement
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference

class ProfileFragmentAchievementsAdapter(private val context: Context, achievements: List<Achievement>, private val firebaseStorageListener: FirebaseStorageListener, private val firebaseAuth: FirebaseAuth) : RecyclerView.Adapter<ProfileFragmentAchievementsAdapter.ViewHolder>() {
    private val achievements = achievements.toMutableList()
    private var achievementsLastIndex = achievements.lastIndex


    class ViewHolder(private var view: View, firebaseStorageListener: FirebaseStorageListener, private val firebaseAuth: FirebaseAuth, private var context: Context) : RecyclerView.ViewHolder(view) {
        val firebaseStorageCommons = FirebaseStorageCommons(firebaseStorageListener, firebaseAuth)
        fun loadImageIntoButton(button: Button, storageReference: StorageReference) {
            Glide.with(button.context)
                .load(storageReference)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(object : CustomTarget<Drawable>() {
                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        button.background = resource
                    }
                })
        }
        private fun showMenuNameSnackbar(view: View, achievementName : String,achievementDescprition: String) {
            val snackbar = Snackbar.make(view, "$achievementName $achievementDescprition", Snackbar.LENGTH_INDEFINITE)
            snackbar.setAction("OK") {
            }
            snackbar.show()
        }
        fun bind(achievementsList: List<Achievement>){
            for ((index) in achievementsList.withIndex()) {
                when (index) {
                    0 -> {
                        val achievement1 = itemView.findViewById<Button>(R.id.fragmentProfileAchievementsRecyclerViewBadge1)
                        achievement1.visibility = View.VISIBLE
                        val layout = itemView.findViewById<LinearLayout>(R.id.fragmentProfileAchievementsLinearLayout)
                        layout.visibility = View.VISIBLE
                        loadImageIntoButton(
                            achievement1,
                            firebaseStorageCommons.getFileReference(
                                firebaseAuth,
                                "achievements",
                                achievementsList[index].imageSrcName,
                                "png"
                            )
                        )
                        achievement1.setOnClickListener{
                            achievement1.startAnimation(AnimationUtils.loadAnimation(context,androidx.appcompat.R.anim.abc_fade_in))
                        }
                        achievement1.setOnLongClickListener {
                            achievement1.startAnimation(AnimationUtils.loadAnimation(context,androidx.appcompat.R.anim.abc_fade_in))
                            showMenuNameSnackbar(view,achievementsList[index].name,achievementsList[index].description)
                            true
                        }
                    }
                    1 -> {
                        val achievement2 =
                            itemView.findViewById<Button>(R.id.fragmentProfileAchievementsRecyclerViewBadge2)
                        loadImageIntoButton(achievement2,firebaseStorageCommons.getFileReference(firebaseAuth,"achievements",achievementsList[index].imageSrcName,"png"))
                        achievement2.visibility = View.VISIBLE
                        loadImageIntoButton(
                            achievement2,
                            firebaseStorageCommons.getFileReference(
                                firebaseAuth,
                                "achievements",
                                achievementsList[index].imageSrcName,
                                "png"
                            )
                        )
                        achievement2.setOnClickListener{
                            achievement2.startAnimation(AnimationUtils.loadAnimation(context,androidx.appcompat.R.anim.abc_fade_in))
                        }
                        achievement2.setOnLongClickListener {
                            achievement2.startAnimation(AnimationUtils.loadAnimation(context,androidx.appcompat.R.anim.abc_fade_in))
                            showMenuNameSnackbar(view,achievementsList[index].name,achievementsList[index].description)
                            true
                        }
                    }

                    2 -> {
                        val achievement3 =
                            itemView.findViewById<Button>(R.id.fragmentProfileAchievementsRecyclerViewBadge3)
                        loadImageIntoButton(achievement3,firebaseStorageCommons.getFileReference(firebaseAuth,"achievements",achievementsList[index].imageSrcName,"png"))
                        achievement3.visibility = View.VISIBLE
                        loadImageIntoButton(
                            achievement3,
                            firebaseStorageCommons.getFileReference(
                                firebaseAuth,
                                "achievements",
                                achievementsList[index].imageSrcName,
                                "png"
                            )
                        )
                        achievement3.setOnClickListener{
                            achievement3.startAnimation(AnimationUtils.loadAnimation(context,androidx.appcompat.R.anim.abc_fade_in))
                        }
                        achievement3.setOnLongClickListener {
                            achievement3.startAnimation(AnimationUtils.loadAnimation(context,androidx.appcompat.R.anim.abc_fade_in))
                            showMenuNameSnackbar(view,achievementsList[index].name,achievementsList[index].description)
                            true
                        }
                    }
                    3 -> {
                        val achievement4 =
                            itemView.findViewById<Button>(R.id.fragmentProfileAchievementsRecyclerViewBadge4)
                        loadImageIntoButton(achievement4,firebaseStorageCommons.getFileReference(firebaseAuth,"achievements",achievementsList[index].imageSrcName,"png"))
                        achievement4.visibility = View.VISIBLE
                        loadImageIntoButton(
                            achievement4,
                            firebaseStorageCommons.getFileReference(
                                firebaseAuth,
                                "achievements",
                                achievementsList[index].imageSrcName,
                                "png"
                            )
                        )
                        achievement4.setOnClickListener{
                            achievement4.startAnimation(AnimationUtils.loadAnimation(context,androidx.appcompat.R.anim.abc_fade_in))
                        }
                        achievement4.setOnLongClickListener {
                            achievement4.startAnimation(AnimationUtils.loadAnimation(context,androidx.appcompat.R.anim.abc_fade_in))
                            showMenuNameSnackbar(view,achievementsList[index].name,achievementsList[index].description)
                            true
                        }
                    }
                    4 -> {
                        val achievement5 =
                            itemView.findViewById<Button>(R.id.fragmentProfileAchievementsRecyclerViewBadge5)
                        loadImageIntoButton(achievement5,firebaseStorageCommons.getFileReference(firebaseAuth,"achievements",achievementsList[index].imageSrcName,"png"))
                        achievement5.visibility = View.VISIBLE
                        loadImageIntoButton(
                            achievement5,
                            firebaseStorageCommons.getFileReference(
                                firebaseAuth,
                                "achievements",
                                achievementsList[index].imageSrcName,
                                "png"
                            )
                        )
                        achievement5.setOnClickListener{
                            achievement5.startAnimation(AnimationUtils.loadAnimation(context,androidx.appcompat.R.anim.abc_fade_in))
                        }
                        achievement5.setOnLongClickListener {
                            achievement5.startAnimation(AnimationUtils.loadAnimation(context,androidx.appcompat.R.anim.abc_fade_in))
                            showMenuNameSnackbar(view,achievementsList[index].name,achievementsList[index].description)
                            true
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.fragment_profile_achievements_recyclerview, parent, false)
        return ViewHolder(view, firebaseStorageListener, firebaseAuth, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val achievementList = getTagListForPosition(position)
        if(achievementList.isNotEmpty()){
            holder.bind(achievementList)
        }
    }

    override fun getItemCount(): Int = achievements.size

    fun refresh(tags: List<Achievement>) {
        this.achievements.clear()
        this.achievements.addAll(tags)
        this.achievementsLastIndex = achievements.size
        notifyDataSetChanged()
    }

    private fun getTagListForPosition(position: Int): List<Achievement> {
        return if(achievementsLastIndex <=5){
            if(position % 5 ==0){
                achievements.subList(position,achievementsLastIndex + 1)
            }else{
                emptyList()
            }
        }else{
            if(position % 5 ==0){
                if (position+4 > achievementsLastIndex ){
                    achievements.subList(position,achievementsLastIndex +1 )
                }else{
                    achievements.subList(position,position+5)
                }
            }else{
                emptyList()
            }
        }
    }
}



