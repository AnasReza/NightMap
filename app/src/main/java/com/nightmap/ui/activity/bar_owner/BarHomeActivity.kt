package com.nightmap.ui.activity.bar_owner

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nightmap.R
import com.nightmap.ui.fragment.bar_owner.*
import com.nightmap.ui.fragment.user.*

class BarHomeActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private var headline: TextView? = null
    private var fragmentlayout: FrameLayout? = null
    private var fragmentManager: FragmentManager? = null
    private var fragmentTransaction: FragmentTransaction? = null
    private var fragmentLayout: FrameLayout? = null
    private var bottomNavigationView: BottomNavigationView? = null
    private var barHome: BarHomeFragment? = null
    private var barNotification: BarNotificationFragment? = null
    private var barEvent: BarEventsFragment? = null
    private var barSettings: BarSettingsFragment? = null
    private var barCustomer:BarCustomerFragment?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_home)
        init()

    }

    private fun init() {
        headline = findViewById(R.id.headLine)
        bottomNavigationView = findViewById(R.id.bottom_nav_view)
        fragmentLayout = findViewById(R.id.fragment_layout)
        barHome = BarHomeFragment()
        barNotification = BarNotificationFragment()
        barEvent = BarEventsFragment()
        barSettings = BarSettingsFragment()
        barCustomer= BarCustomerFragment()

        switchFragment(barHome!!)

        bottomNavigationView!!.setOnNavigationItemSelectedListener(this)
    }

    private fun switchFragment(fragment: Fragment) {
        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction!!.replace(R.id.fragment_layout, fragment)
        fragmentTransaction!!.commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var menu: Menu =bottomNavigationView!!.menu
        when (item.itemId) {
            R.id.navigation_home -> {
                switchFragment(barHome!!)
                headline!!.text = "HOME"
                menu.findItem(R.id.navigation_home).setIcon(R.drawable.ic_only_wine_glass)
                menu.findItem(R.id.navigation_customer).setIcon(R.drawable.ic_profile_grey)
                menu.findItem(R.id.navigation_events).setIcon(R.drawable.ic_calender_grey)
                menu.findItem(R.id.navigation_notifications).setIcon(R.drawable.ic_bell_grey)
                menu.findItem(R.id.navigation_settings).setIcon(R.drawable.ic_settings_grey)
                return true
            }
            R.id.navigation_notifications -> {
                switchFragment(barNotification!!)
                headline!!.text = "NOTIFICATIONS"
                menu.findItem(R.id.navigation_home).setIcon(R.drawable.ic_only_wine_glass_grey)
                menu.findItem(R.id.navigation_customer).setIcon(R.drawable.ic_profile_grey)
                menu.findItem(R.id.navigation_events).setIcon(R.drawable.ic_calender_grey)
                menu.findItem(R.id.navigation_notifications).setIcon(R.drawable.ic_bell)
                menu.findItem(R.id.navigation_settings).setIcon(R.drawable.ic_settings_grey)
                return true
            }
            R.id.navigation_events -> {
                switchFragment(barEvent!!)
                headline!!.text = "EVENTS"
                menu.findItem(R.id.navigation_home).setIcon(R.drawable.ic_only_wine_glass_grey)
                menu.findItem(R.id.navigation_customer).setIcon(R.drawable.ic_profile_grey)
                menu.findItem(R.id.navigation_events).setIcon(R.drawable.ic_calender)
                menu.findItem(R.id.navigation_notifications).setIcon(R.drawable.ic_bell_grey)
                menu.findItem(R.id.navigation_settings).setIcon(R.drawable.ic_settings_grey)
                return true
            }
            R.id.navigation_settings -> {
                switchFragment(barSettings!!)
                headline!!.text = "Settings"
                menu.findItem(R.id.navigation_home).setIcon(R.drawable.ic_only_wine_glass_grey)
                menu.findItem(R.id.navigation_customer).setIcon(R.drawable.ic_profile_grey)
                menu.findItem(R.id.navigation_events).setIcon(R.drawable.ic_calender_grey)
                menu.findItem(R.id.navigation_notifications).setIcon(R.drawable.ic_bell_grey)
                menu.findItem(R.id.navigation_settings).setIcon(R.drawable.ic_settings)
                return true
            }
            R.id.navigation_customer->{
                switchFragment(barCustomer!!)
                headline!!.text="CUSTOMERS"
                menu.findItem(R.id.navigation_home).setIcon(R.drawable.ic_only_wine_glass_grey)
                menu.findItem(R.id.navigation_customer).setIcon(R.drawable.ic_profile)
                menu.findItem(R.id.navigation_events).setIcon(R.drawable.ic_calender_grey)
                menu.findItem(R.id.navigation_notifications).setIcon(R.drawable.ic_bell_grey)
                menu.findItem(R.id.navigation_settings).setIcon(R.drawable.ic_settings_grey)
                return true
            }
        }
        return true
    }
}