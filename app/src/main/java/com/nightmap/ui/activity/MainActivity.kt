package com.nightmap.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.nightmap.R
import com.nightmap.ui.activity.bar_owner.BarHomeActivity
import com.nightmap.ui.activity.user.UserHomeActivity
import com.nightmap.ui.activity.user.UserLogin
import com.nightmap.utility.Preferences
import java.text.SimpleDateFormat
import java.util.*
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
                    startActivity(Intent(this@MainActivity, BarHomeActivity::class.java))
                } else {
                    if (pref!!.getFirstCheck()!!) {
                        startActivity(Intent(this@MainActivity, UserLogin::class.java))
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
    }



    fun isTablet(context: Context): Boolean {
        return context.resources.configuration.screenLayout and
                Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }
}
