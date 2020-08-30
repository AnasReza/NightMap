package com.nightmap.ui.activity.user

import android.content.ContentResolver
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.ui.fragment.user.*
import com.nightmap.utility.Preferences
import kotlin.math.roundToInt

class UserHomeActivity : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private var mapLayout: RelativeLayout? = null
    private var mapImage: ImageView? = null
    private var headline: TextView? = null
    private var sync_contacts: TextView? = null
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
    private var phoneList: ArrayList<String>? = null
    private var pref: Preferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_home)
        init()

    }

    private fun init() {
        pref = Preferences(this)
        phoneList = ArrayList()
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
        sync_contacts = findViewById(R.id.sync_contact_text)

        mapLayout!!.layoutParams.width = (width * 0.19).roundToInt()
        mapLayout!!.layoutParams.height = (width * 0.19).roundToInt()
        mapImage!!.layoutParams.width = (width * 0.075).roundToInt()
        mapImage!!.layoutParams.height = (width * 0.12).roundToInt()
        innerlayout!!.layoutParams.width = (width * 0.155).roundToInt()
        innerlayout!!.layoutParams.height = (width * 0.155).roundToInt()

        switchFragment(exploreFragment!!)


        bottomNavigationView!!.setOnNavigationItemSelectedListener(this)
        mapImageView!!.setOnClickListener {
            var menu: Menu = bottomNavigationView!!.menu
            menu.findItem(R.id.navigation_home).setIcon(R.drawable.ic_noun_radar_grey)
            menu.findItem(R.id.navigation_notifications).setIcon(R.drawable.ic_bell_grey)
            menu.findItem(R.id.navigation_profile).setIcon(R.drawable.ic_profile_grey)
            menu.findItem(R.id.navigation_friends).setIcon(R.drawable.ic_friends_grey)
            switchFragment(mapFragment!!)
            sync_contacts!!.visibility = View.GONE
            headline!!.text = "MAP"
            bottomNavigationView!!.menu.findItem(R.id.empty).isChecked = true
            innerlayout!!.visibility = View.VISIBLE

        }

        sync_contacts!!.setOnClickListener(this)
    }

    private fun switchFragment(fragment: Fragment) {
        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction!!.replace(R.id.fragment_layout, fragment)
        fragmentTransaction!!.commit()
        innerlayout!!.visibility = View.GONE

    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var menu: Menu = bottomNavigationView!!.menu
        when (item.itemId) {
            R.id.navigation_home -> {
                switchFragment(exploreFragment!!)
                headline!!.text = "HOME"
                menu.findItem(R.id.navigation_home).setIcon(R.drawable.ic_noun_radar)
                menu.findItem(R.id.navigation_notifications).setIcon(R.drawable.ic_bell_grey)
                menu.findItem(R.id.navigation_profile).setIcon(R.drawable.ic_profile_grey)
                menu.findItem(R.id.navigation_friends).setIcon(R.drawable.ic_friends_grey)
                sync_contacts!!.visibility = View.GONE
                return true
            }
            R.id.navigation_notifications -> {
                switchFragment(notifyFragment!!)
                headline!!.text = "NOTIFICATIONS"
                menu.findItem(R.id.navigation_home).setIcon(R.drawable.ic_noun_radar_grey)
                menu.findItem(R.id.navigation_notifications).setIcon(R.drawable.ic_bell)
                menu.findItem(R.id.navigation_profile).setIcon(R.drawable.ic_profile_grey)
                menu.findItem(R.id.navigation_friends).setIcon(R.drawable.ic_friends_grey)
                sync_contacts!!.visibility = View.GONE
                return true
            }
            R.id.navigation_profile -> {
                switchFragment(profileFragment!!)
                headline!!.text = "PROFILE"
                menu.findItem(R.id.navigation_home).setIcon(R.drawable.ic_noun_radar_grey)
                menu.findItem(R.id.navigation_notifications).setIcon(R.drawable.ic_bell_grey)
                menu.findItem(R.id.navigation_profile).setIcon(R.drawable.ic_profile)
                menu.findItem(R.id.navigation_friends).setIcon(R.drawable.ic_friends_grey)
                sync_contacts!!.visibility = View.GONE
                return true
            }
            R.id.navigation_friends -> {
                switchFragment(friendsFragment!!)
                headline!!.text = "MY FRIENDS"
                menu.findItem(R.id.navigation_home).setIcon(R.drawable.ic_noun_radar_grey)
                menu.findItem(R.id.navigation_notifications).setIcon(R.drawable.ic_bell_grey)
                menu.findItem(R.id.navigation_profile).setIcon(R.drawable.ic_profile_grey)
                menu.findItem(R.id.navigation_friends).setIcon(R.drawable.ic_friends)
                sync_contacts!!.visibility = View.VISIBLE
                return true
            }
        }
        return true
    }

    private fun getContactList() {
        try {
            val cr: ContentResolver = contentResolver
            val cur: Cursor? = cr.query(
                ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null
            )
            if ((cur?.count ?: 0) > 0) {
                while (cur != null && cur.moveToNext()) {
                    val id: String = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID)
                    )
                    val name: String = cur.getString(
                        cur.getColumnIndex(
                            ContactsContract.Contacts.DISPLAY_NAME
                        )
                    )
                    if (cur.getInt(
                            cur.getColumnIndex(
                                ContactsContract.Contacts.HAS_PHONE_NUMBER
                            )
                        ) > 0
                    ) {
                        val pCur: Cursor? = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            arrayOf(id),
                            null
                        )
                        while (pCur!!.moveToNext()) {
                            val phoneNo: String = pCur.getString(
                                pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER
                                )
                            )
                            Log.i("Anas", "Name: $name")
                            Log.i("Anas", "Phone Number: $phoneNo")
                            if (phoneNo.contains("+")) {
                                phoneList!!.add(phoneNo)
                            }
                        }

                        pCur.close()
                    }
                }
            }
            cur?.close()
        } catch (e: Exception) {
            Toast.makeText(this@UserHomeActivity, "You dont have permission.", Toast.LENGTH_SHORT)
                .show()
        }

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.sync_contact_text -> {
                getContactList()
                if (phoneList != null) {
                    for (x in 0 until phoneList!!.size) {
                        val newDB: FirebaseFirestore = Firebase.firestore
                        val hashMap =
                            hashMapOf("uid" to pref!!.getUserID(), "phoneNumber" to phoneList!![x])
                        newDB.collection("Contacts").add(hashMap)
                    }
                }
            }
        }

    }
}
