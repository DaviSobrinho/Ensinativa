package com.ensinativapackage.ensinativa.viewmodel

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity, fragmentsArrayList : ArrayList<Fragment>) :
    FragmentStateAdapter(fragmentActivity) {
    private var arrayList : ArrayList<Fragment> = fragmentsArrayList
    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun createFragment(position: Int): Fragment {
        return arrayList[position]
    }

}