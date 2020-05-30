package com.nightmap.ui.activity.admin

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nightmap.R
import com.nightmap.ui.fragment.admin.*

class AdminHomeActivity : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener {

    private var headline: TextView? = null
    private var fragmentlayout: FrameLayout? = null
    private var fragmentManager: FragmentManager? = null
    private var fragmentTransaction: FragmentTransaction? = null
    private var fragmentLayout: FrameLayout? = null
    private var bottomNavigationView: BottomNavigationView? = null
    private var homeFragment: AdminHomeFragment? = null
    private var usersFragment:AdminUserFragment?=null
    private var barListFragment:AdminBarListFragment?=null
    private var notificationFragment:AdminNotificationFragment?=null
    private var settingsFragment:AdminSettingsFragment?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)
        init()

    }

    private fun init() {
        headline = findViewById(R.id.headLine)
        bottomNavigationView = findViewById(R.id.bottom_nav_view)
        fragmentLayout = findViewById(R.id.fragment_layout)

        homeFragment = AdminHomeFragment()
        usersFragment= AdminUserFragment()
        barListFragment= AdminBarListFragment()
        notificationFragment= AdminNotificationFragment()
        settingsFragment= AdminSettingsFragment()

        switchFragment(homeFragment!!)

        bottomNavigationView!!.setOnNavigationItemSelectedListener(this)
    }

    private fun switchFragment(fragment: Fragment) {
        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction!!.replace(R.id.fragment_layout, fragment)
        fragmentTransaction!!.commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var menu:Menu=bottomNavigationView!!.menu
        when (item.itemId) {
            R.id.navigation_home -> {
                switchFragment(homeFragment!!)
                headline!!.text = "HOME"
                menu.findItem(R.id.navigation_home).setIcon(R.drawable.ic_only_wine_glass)
                menu.findItem(R.id.navigation_users).setIcon(R.drawable.ic_profile_grey)
                menu.findItem(R.id.navigation_bar_list).setIcon(R.drawable.ic_bar_list_grey)
                menu.findItem(R.id.navigation_notifications).setIcon(R.drawable.ic_bell_grey)
                menu.findItem(R.id.navigation_settings).setIcon(R.drawable.ic_settings_grey)
                return true
            }
            R.id.navigation_users->{
                switchFragment(usersFragment!!)
                headline!!.text = "USERS"
                menu.findItem(R.id.navigation_home).setIcon(R.drawable.ic_only_wine_glass_grey)
                menu.findItem(R.id.navigation_users).setIcon(R.drawable.ic_profile)
                menu.findItem(R.id.navigation_bar_list).setIcon(R.drawable.ic_bar_list_grey)
                menu.findItem(R.id.navigation_notifications).setIcon(R.drawable.ic_bell_grey)
                menu.findItem(R.id.navigation_settings).setIcon(R.drawable.ic_settings_grey)
                return true
            }
            R.id.navigation_bar_list->{
                switchFragment(barListFragment!!)
                headline!!.text = "CUSTOMERS"
                menu.findItem(R.id.navigation_home).setIcon(R.drawable.ic_only_wine_glass_grey)
                menu.findItem(R.id.navigation_users).setIcon(R.drawable.ic_profile_grey)
                menu.findItem(R.id.navigation_bar_list).setIcon(R.drawable.ic_bar_list)
                menu.findItem(R.id.navigation_notifications).setIcon(R.drawable.ic_bell_grey)
                menu.findItem(R.id.navigation_settings).setIcon(R.drawable.ic_settings_grey)
                return true
            }
            R.id.navigation_notifications->{
                switchFragment(notificationFragment!!)
                headline!!.text = "NOTIFICATIONS"
                menu.findItem(R.id.navigation_home).setIcon(R.drawable.ic_only_wine_glass_grey)
                menu.findItem(R.id.navigation_users).setIcon(R.drawable.ic_profile_grey)
                menu.findItem(R.id.navigation_bar_list).setIcon(R.drawable.ic_bar_list_grey)
                menu.findItem(R.id.navigation_notifications).setIcon(R.drawable.ic_bell)
                menu.findItem(R.id.navigation_settings).setIcon(R.drawable.ic_settings_grey)
                return true
            }
            R.id.navigation_settings->{
                switchFragment(settingsFragment!!)
                headline!!.text = "SETTINGS"
                menu.findItem(R.id.navigation_home).setIcon(R.drawable.ic_only_wine_glass_grey)
                menu.findItem(R.id.navigation_users).setIcon(R.drawable.ic_profile_grey)
                menu.findItem(R.id.navigation_bar_list).setIcon(R.drawable.ic_bar_list_grey)
                menu.findItem(R.id.navigation_notifications).setIcon(R.drawable.ic_bell_grey)
                menu.findItem(R.id.navigation_settings).setIcon(R.drawable.ic_settings)
                return true
            }

        }
        return true
    }
}