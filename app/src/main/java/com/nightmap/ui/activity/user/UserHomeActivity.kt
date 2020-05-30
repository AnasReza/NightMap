package com.nightmap.ui.activity.user

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nightmap.R
import com.nightmap.ui.fragment.user.*
import kotlin.math.roundToInt

class UserHomeActivity : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener {
    private var mapLayout: RelativeLayout? = null
    private var mapImage: ImageView? = null
    private var headline: TextView? = null
    private var mapImageView: RelativeLayout? = null
    private var fragmentlayout: FrameLayout? = null
    private var innerlayout: RelativeLayout? = null
    private var fragmentManager: FragmentManager? = null
    private var fragmentTransaction: FragmentTransaction? = null
    private var exploreFragment: UserExploreFragment? = null
    private var notifyFragment: UserNotificationFragment? = null
    private var profileFragment: UserProfileFragment? = null
    private var friendsFragment: UserFriendFragement? = null
    private var mapFragment: UserMapFragment? = null
    private var fragmentLayout: FrameLayout? = null
    private var bottomNavigationView: BottomNavigationView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_home)
        init()

    }

    private fun init() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        exploreFragment = UserExploreFragment()
        notifyFragment = UserNotificationFragment()
        profileFragment = UserProfileFragment()
        mapFragment = UserMapFragment()
        friendsFragment = UserFriendFragement()

        mapLayout = findViewById(R.id.map_icon_layout)
        mapImage = findViewById(R.id.mapImage)
        fragmentLayout = findViewById(R.id.fragment_layout)
        bottomNavigationView = findViewById(R.id.bottom_nav_view)
        headline = findViewById(R.id.headLine)
        mapImageView = findViewById(R.id.map_icon_layout)
        innerlayout = findViewById(R.id.innerlayout)

        mapLayout!!.layoutParams.width = (width * 0.19).roundToInt()
        mapLayout!!.layoutParams.height = (width * 0.19).roundToInt()
        mapImage!!.layoutParams.width = (width * 0.075).roundToInt()
        mapImage!!.layoutParams.height = (width * 0.12).roundToInt()
        innerlayout!!.layoutParams.width= (width*0.155).roundToInt()
        innerlayout!!.layoutParams.height= (width*0.155).roundToInt()

        switchFragment(exploreFragment!!)


        bottomNavigationView!!.setOnNavigationItemSelectedListener(this)
        mapImageView!!.setOnClickListener {
            switchFragment(mapFragment!!)
            headline!!.text = "MAP"
            bottomNavigationView!!.menu.findItem(R.id.empty).isChecked = true
            innerlayout!!.visibility= View.VISIBLE

        }
    }

    private fun switchFragment(fragment: Fragment) {
        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction!!.replace(R.id.fragment_layout, fragment)
        fragmentTransaction!!.commit()
        innerlayout!!.visibility= View.GONE

    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var menu:Menu=bottomNavigationView!!.menu
        when (item.itemId) {
            R.id.navigation_home -> {
                switchFragment(exploreFragment!!)
                headline!!.text = "HOME"
                menu.findItem(R.id.navigation_home).setIcon(R.drawable.ic_noun_radar)
                menu.findItem(R.id.navigation_notifications).setIcon(R.drawable.ic_bell_grey)
                menu.findItem(R.id.navigation_profile).setIcon(R.drawable.ic_profile_grey)
                menu.findItem(R.id.navigation_friends).setIcon(R.drawable.ic_friends_grey)
                return true
            }
            R.id.navigation_notifications -> {
                switchFragment(notifyFragment!!)
                headline!!.text = "NOTIFICATIONS"
                menu.findItem(R.id.navigation_home).setIcon(R.drawable.ic_noun_radar_grey)
                menu.findItem(R.id.navigation_notifications).setIcon(R.drawable.ic_bell)
                menu.findItem(R.id.navigation_profile).setIcon(R.drawable.ic_profile_grey)
                menu.findItem(R.id.navigation_friends).setIcon(R.drawable.ic_friends_grey)
                return true
            }
            R.id.navigation_profile -> {
                switchFragment(profileFragment!!)
                headline!!.text = "PROFILE"
                menu.findItem(R.id.navigation_home).setIcon(R.drawable.ic_noun_radar_grey)
                menu.findItem(R.id.navigation_notifications).setIcon(R.drawable.ic_bell_grey)
                menu.findItem(R.id.navigation_profile).setIcon(R.drawable.ic_profile)
                menu.findItem(R.id.navigation_friends).setIcon(R.drawable.ic_friends_grey)
                return true
            }
            R.id.navigation_friends -> {
                switchFragment(friendsFragment!!)
                headline!!.text = "MY FRIENDS"
                menu.findItem(R.id.navigation_home).setIcon(R.drawable.ic_noun_radar_grey)
                menu.findItem(R.id.navigation_notifications).setIcon(R.drawable.ic_bell_grey)
                menu.findItem(R.id.navigation_profile).setIcon(R.drawable.ic_profile_grey)
                menu.findItem(R.id.navigation_friends).setIcon(R.drawable.ic_friends)
                return true
            }
        }
        return true
    }

}
