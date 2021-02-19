package com.jaber.insetagramcloneapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.jaber.insetagramcloneapp.fragments.HomeFragment
import com.jaber.insetagramcloneapp.fragments.NotificationFragment
import com.jaber.insetagramcloneapp.fragments.ProfileFragment
import com.jaber.insetagramcloneapp.fragments.SearchFragment

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavView: BottomNavigationView
    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    moveToFragment(HomeFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_search -> {
                    moveToFragment(SearchFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_add_post -> {
//                    item.isChecked = false
//                    startActivity(Intent(this@MainActivity, AddPostActivity::class.java))
                    return@OnNavigationItemSelectedListener true
                }

                R.id.nav_notification -> {
                    moveToFragment(NotificationFragment())
                    return@OnNavigationItemSelectedListener true
                }

                R.id.nav_profile -> {
                    moveToFragment(ProfileFragment())
                    return@OnNavigationItemSelectedListener true
                }

            }



            false

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavView = findViewById(R.id.bottom_nav_view)

        //Default Fragment to display
        moveToFragment(HomeFragment())

        //Display fragment according to bottom nav icon click
        bottomNavView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)


    }


    private fun moveToFragment(fragment: Fragment) {
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.fragment_container, fragment)
        fragmentTrans.commit()
    }
}