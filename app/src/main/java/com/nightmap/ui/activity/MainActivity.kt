package com.nightmap.ui.activity

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.ui.activity.admin.AdminHomeActivity
import com.nightmap.ui.activity.bar_owner.BarApprovalActivity
import com.nightmap.ui.activity.bar_owner.BarHomeActivity
import com.nightmap.ui.activity.user.UserEventInfoActivity
import com.nightmap.ui.activity.user.UserHomeActivity
import com.nightmap.utility.Preferences
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {
    private var cdt: CountDownTimer? = null

    private var centerImage: ImageView? = null
    private var pref: Preferences? = null
    private var locationManager: LocationManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_main)
        init()

    }

    private fun init() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        val density = displayMetrics.densityDpi

        pref = Preferences(this)

        centerImage = findViewById(R.id.centerImage)
        centerImage!!.layoutParams.width = (width * 0.65).roundToInt()
        centerImage!!.layoutParams.height = (height * 0.32).roundToInt()

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        cdt = object : CountDownTimer(2000, 500) {
            override fun onFinish() {
                var userType = pref!!.getUserType()


                if (userType == "user") {
                    startActivity(Intent(this@MainActivity, UserHomeActivity::class.java))
                } else if (userType == "bar") {
                    if (pref!!.getBarStatus() == "approved") {
                        startActivity(Intent(this@MainActivity, BarHomeActivity::class.java))
                    } else if (pref!!.getBarStatus() == "pending") {
                        startActivity(Intent(this@MainActivity, BarApprovalActivity::class.java))
                    }

                } else if (userType == "admin") {
                    startActivity(Intent(this@MainActivity, AdminHomeActivity::class.java))
                } else {
                    if (pref!!.getFirstCheck()!!) {
                        startActivity(Intent(this@MainActivity, RegisterAsActivity::class.java))
                    } else {
                        startActivity(Intent(this@MainActivity, OnboardingActivity::class.java))
                    }
                }

                finish()
            }

            override fun onTick(millisUntilFinished: Long) {
            }
        }

        cdt!!.start()
        Log.e("Anas","${pref!!.getSharingEventId()}  sharing event id from main activity")
//        var intent = Intent(this, UserEventInfoActivity::class.java)
//        intent.putExtra("eventId", pref!!.getSharingEventId())
//        intent.putExtra("userType", pref!!.getUserType())
//        Firebase.dynamicLinks.getDynamicLink(Intent())
//            .addOnSuccessListener(this) { pendingDynamicLinkData ->
//                var deepLink:Uri?=null
//                if(pendingDynamicLinkData!=null){
//                    deepLink=pendingDynamicLinkData.link
//                    Log.d("Anas","$deepLink from mainActivity")
//                    startActivity(intent)
//                }else{
//                    Log.d("Anas","URI is empty")
//                }
//            }

    }

    fun isTablet(context: Context): Boolean {
        return context.resources.configuration.screenLayout and
                Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }
}
