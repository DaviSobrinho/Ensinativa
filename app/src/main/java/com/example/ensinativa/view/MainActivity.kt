package com.example.ensinativa.view

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.ensinativa.R
import com.example.ensinativa.databinding.ActivityMainBinding
import com.example.ensinativa.viewmodel.ViewPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView


private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var mainPager: ViewPager2
    var fragmentArrayList : ArrayList<Fragment> = ArrayList<Fragment>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        mainPager = binding.pager2
        fragmentArrayList.add(HomeFragment())
        fragmentArrayList.add(MessageFragment())
        fragmentArrayList.add(RequestFragment())
        fragmentArrayList.add(MessageFragment())
        fragmentArrayList.add(ProfileFragment())

        val adapterViewPager = ViewPagerAdapter(this,fragmentArrayList)
        mainPager.adapter = adapterViewPager

        mainPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            @Override
            override fun onPageSelected(position: Int) {
                when(position){
                    0 -> binding.bottomNav.selectedItemId = (R.id.home)
                    1 -> binding.bottomNav.selectedItemId = (R.id.messages)
                    2 -> binding.bottomNav.selectedItemId = (R.id.requests)
                    3 -> binding.bottomNav.selectedItemId = (R.id.notifications)
                    4 -> binding.bottomNav.selectedItemId = (R.id.profile)

                }
                super.onPageSelected(position)
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
        binding.bottomNav.setOnItemSelectedListener(object: BottomNavigationView.OnNavigationItemSelectedListener{
            @Override
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when(item.itemId){
                    R.id.home -> mainPager.currentItem = 0
                    R.id.messages -> mainPager.currentItem = 1
                    R.id.requests -> mainPager.currentItem = 2
                    R.id.notifications -> mainPager.currentItem = 3
                    R.id.profile -> mainPager.currentItem = 4
                }
                return true
            }
        })
    }
}