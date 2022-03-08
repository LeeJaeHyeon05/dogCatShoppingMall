package com.example.dogcatshoppingmall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dogcatshoppingmall.basket.BasketFragment
import com.example.dogcatshoppingmall.databinding.ActivityMainBinding
import com.example.dogcatshoppingmall.home.HomeFragment
import com.example.dogcatshoppingmall.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var homeFragment: HomeFragment
    private lateinit var basketFragment: BasketFragment
    private lateinit var profileFragment: ProfileFragment

    private var mBinding: ActivityMainBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnNav = binding.bottomNav
        btnNav.setOnNavigationItemSelectedListener (onBottomNavItemSelectedListener)

        homeFragment = HomeFragment.newInstance()
        supportFragmentManager.beginTransaction().add(R.id.fragments_frame, homeFragment).commit()


    }

    private val onBottomNavItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {

        when (it.itemId) {
            R.id.menu_home -> {
                homeFragment = HomeFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, homeFragment).commit()
            }
            R.id.menu_location -> {
                basketFragment = BasketFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, basketFragment).commit()
            }
            R.id.menu_profile -> {
                profileFragment = ProfileFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, profileFragment).commit()
            }
        }
        true
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }
}