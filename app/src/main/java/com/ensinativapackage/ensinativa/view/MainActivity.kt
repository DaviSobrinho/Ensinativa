package com.ensinativapackage.ensinativa.view

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.ensinativapackage.ensinativa.R
import com.ensinativapackage.ensinativa.databinding.ActivityMainBinding
import com.ensinativapackage.ensinativa.firebaseauth.FirebaseAuthCommons
import com.ensinativapackage.ensinativa.firebaseauth.FirebaseAuthListener
import com.ensinativapackage.ensinativa.firebaseauth.GoogleAuthCommons
import com.ensinativapackage.ensinativa.firebaseauth.GoogleAuthListener
import com.ensinativapackage.ensinativa.viewmodel.ViewPagerAdapter
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

private lateinit var firebaseAuth: FirebaseAuth
private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity(), GoogleAuthListener, FirebaseAuthListener {
    private lateinit var mainPager: ViewPager2
    private var emailPasswordAuthLoggedIn = false
    private var googleAuthLoggedIn = false
    private lateinit var googleAuthCommons: GoogleAuthCommons
    private lateinit var firebaseAuthCommons: FirebaseAuthCommons
    private var startRequestFromRequest = false
    private var fragmentArrayList: ArrayList<Fragment> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configAppCheck()
        firebaseAuth = Firebase.auth
        googleAuthCommons = GoogleAuthCommons(this, firebaseAuth, this)
        firebaseAuthCommons = FirebaseAuthCommons(this, firebaseAuth)
        if (firebaseAuth.currentUser == null) {
            startLoginActivity()
        }
        renderView()
        configViewPager()
        configMenuButton(binding.logoutButton)
        val backButtonCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                moveTaskToBack(true)
            }
        }
        onBackPressedDispatcher.addCallback(this, backButtonCallback)
    }
    public override fun onStart() {
        super.onStart()

    }
    private fun renderView(){
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
    private fun configAppCheck(){
        FirebaseApp.initializeApp(this)
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )
    }


    private fun configMenuButton(menuButton: Button) {
        menuButton.setOnClickListener{
            val providers = firebaseAuthCommons.getProviders()
            emailPasswordAuthLoggedIn = providers.emailPasswordProvider
            googleAuthLoggedIn = providers.googleProvider
            if(firebaseAuth.currentUser != null){
                firebaseAuth.signOut()
            }
            if(googleAuthLoggedIn){
                googleAuthCommons.googleSignOut()
            }
            startLoginActivity()
        }
    }

    private fun configViewPager(){
        mainPager = binding.pager2
        fragmentArrayList.add(HomeFragment())
        fragmentArrayList.add(MessageFragment())
        fragmentArrayList.add(RequestFragment())
        fragmentArrayList.add(NewsFragment())
        fragmentArrayList.add(ProfileFragment())
        val adapterViewPager = ViewPagerAdapter(this,fragmentArrayList)
        adapterViewPager.getItemId(0)
        mainPager.adapter = adapterViewPager
        mainPager.offscreenPageLimit = 4
        mainPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            @Override
            override fun onPageSelected(position: Int) {
                when(position){
                    0 -> binding.bottomNav.selectedItemId = (R.id.home)
                    1 -> binding.bottomNav.selectedItemId = (R.id.messages)
                    2 -> binding.bottomNav.selectedItemId = (R.id.requests)
                    3 -> binding.bottomNav.selectedItemId = (R.id.news)
                    4 -> binding.bottomNav.selectedItemId = (R.id.profile)

                }
                super.onPageSelected(position)
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                // Nothing
            }

            override fun onPageScrollStateChanged(state: Int) {
                // Nothing
            }
        })
        binding.bottomNav.setOnItemSelectedListener(object: BottomNavigationView.OnNavigationItemSelectedListener{
            @Override
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when(item.itemId){
                    R.id.home -> mainPager.currentItem = 0
                    R.id.messages -> mainPager.currentItem = 1
                    R.id.requests -> mainPager.currentItem = 2
                    R.id.news -> mainPager.currentItem = 3
                    R.id.profile -> mainPager.currentItem = 4
                }
                return true
            }
        })
    }

    fun callMessageFragment(index: Int) {
        startRequestFromRequest = true
        mainPager.setCurrentItem(index, true)
    }

    override fun onResetEmailSentSuccess() {
        // Nothing
    }

    override fun onResetEmailSentFailure() {
        // Nothing
    }

    override fun onGetUserSignOn() {
        // Nothing
    }

    override fun onGetUserSignOut() {
        // Nothing
    }

    override fun onEmailPasswordSignInFailureCredentials(exception: Exception) {
        // Nothing
    }

    override fun onEmailPasswordSignInSuccess(email: String, password: String) {
        // Nothing
    }

    override fun onEmailPasswordSignInFailure() {
        // Nothing
    }

    override fun onEmailPasswordSignUpSuccess() {
        // Nothing
    }

    override fun onEmailPasswordSignUpFailure() {
        // Nothing
    }

    override fun onEmailPasswordSignUpFailureDuplicatedCredentials() {
        // Nothing
    }

    override fun onUserDataUpdatedSuccess() {
        // Nothing
    }

    override fun onUserDataUpdatedFailure() {
        // Nothing
    }


    override fun onGoogleSignInSuccess(account: GoogleSignInAccount?) {
        // Nothing
    }

    override fun onGoogleSignInFailure() {
        // Nothing
    }

    private fun startLoginActivity() {
        val customAnimation = ActivityOptions.makeCustomAnimation(
            this,
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent, customAnimation.toBundle())
        finish()
    }
}