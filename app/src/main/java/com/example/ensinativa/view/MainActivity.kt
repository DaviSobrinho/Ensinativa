package com.example.ensinativa.view

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.ensinativa.R
import com.example.ensinativa.databinding.ActivityMainBinding
import com.example.ensinativa.firebaseauth.FirebaseAuthCommons
import com.example.ensinativa.firebaseauth.FirebaseAuthListener
import com.example.ensinativa.firebaseauth.GoogleAuthCommons
import com.example.ensinativa.firebaseauth.GoogleAuthListener
import com.example.ensinativa.model.Request
import com.example.ensinativa.model.RequestWithHash
import com.example.ensinativa.viewmodel.ViewPagerAdapter
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

private lateinit var firebaseAuth: FirebaseAuth
private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity(), GoogleAuthListener, FirebaseAuthListener {
    private lateinit var mainPager: ViewPager2
    private var emailPasswordAuthLoggedIn = false
    private var googleAuthLoggedIn = false
    private lateinit var googleAuthCommons: GoogleAuthCommons
    private lateinit var firebaseAuthCommons : FirebaseAuthCommons
    var startRequestFromRequest = false
    private lateinit var request: Request

    var fragmentArrayList : ArrayList<Fragment> = ArrayList<Fragment>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configAppCheck()
        FirebaseApp.initializeApp(this)
        firebaseAuth = Firebase.auth
        googleAuthCommons = GoogleAuthCommons(this, firebaseAuth, this)
        firebaseAuthCommons = FirebaseAuthCommons(this, firebaseAuth)
        if(firebaseAuth.currentUser == null){
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

        // Configurar o App Check com o DebugAppCheckProvider para ambiente de desenvolvimento
        println("Configurando debug")
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance()
        )

        println(DebugAppCheckProviderFactory.getInstance())
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
                    R.id.news -> mainPager.currentItem = 3
                    R.id.profile -> mainPager.currentItem = 4
                }
                return true
            }
        })
    }

    private fun setContextToAllFragments(adapterViewPager: ViewPagerAdapter) {
        for (i in 0 until adapterViewPager.itemCount) {
            mainPager.setCurrentItem(i, true)
        }
    }

    fun callMessageFragment(index: Int, request: RequestWithHash) {
        startRequestFromRequest = true
        mainPager.setCurrentItem(index, true)
    }

    override fun onResetEmailSentSuccess() {
        TODO("Not yet implemented")
    }

    override fun onResetEmailSentFailure() {
        TODO("Not yet implemented")
    }

    override fun onGetUserSignOn() {

    }

    override fun onGetUserSignOut() {
        TODO("Not yet implemented")
    }

    override fun onEmailPasswordSignInFailureCredentials(exception: Exception) {
        TODO("Not yet implemented")
    }

    override fun onEmailPasswordSignInSuccess(email: String, password: String) {
        TODO("Not yet implemented")
    }

    override fun onEmailPasswordSignInFailure() {
        TODO("Not yet implemented")
    }

    override fun onEmailPasswordSignUpSuccess() {
        TODO("Not yet implemented")
    }

    override fun onEmailPasswordSignUpFailure() {
        TODO("Not yet implemented")
    }

    override fun onEmailPasswordSignUpFailureDuplicatedCredentials() {
        TODO("Not yet implemented")
    }

    override fun onUserDataUpdatedSuccess() {
        TODO("Not yet implemented")
    }

    override fun onUserDataUpdatedFailure() {
        TODO("Not yet implemented")
    }


    override fun onGoogleSignInSuccess(account: GoogleSignInAccount?) {
        TODO("Not yet implemented")
    }

    override fun onGoogleSignInFailure() {
        TODO("Not yet implemented")
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