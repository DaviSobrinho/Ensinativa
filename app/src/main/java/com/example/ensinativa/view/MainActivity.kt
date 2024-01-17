package com.example.ensinativa.view

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.ensinativa.R
import com.example.ensinativa.databinding.ActivityMainBinding
import com.example.ensinativa.viewmodel.ViewPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

private lateinit var auth: FirebaseAuth
private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var mainPager: ViewPager2
    var fragmentArrayList : ArrayList<Fragment> = ArrayList<Fragment>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }
    public override fun onStart() {
        super.onStart()
        // Initialize Firebase Auth
        FirebaseApp.initializeApp(this);
        auth = Firebase.auth
        verifyAuth()
    }
    private fun verifyAuth(){
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java).apply {
            }
            startActivity(intent)
            Toast.makeText(this, "CurrentUser != null", Toast.LENGTH_SHORT).show()
        }else{
            renderView()
        }
    }
    private fun renderView(){
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        configViewPager()
        configMenuButton(binding.menuButton)
    }

    private fun configMenuButton(menuButton: Button) {
        menuButton.setOnClickListener{
            if(auth.currentUser != null){
                auth.signOut()
            }
            val customAnimation = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_left, R.anim.slide_out_right)
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent, customAnimation.toBundle())
        }
    }

    private fun configViewPager(){
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