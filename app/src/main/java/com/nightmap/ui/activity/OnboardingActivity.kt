package com.nightmap.ui.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.nightmap.R
import com.nightmap.ui.fragment.Onboarding1Fragment
import com.nightmap.ui.fragment.Onboarding2Fragment
import com.nightmap.ui.fragment.Onboarding3Fragment
import kotlin.math.roundToInt

class OnboardingActivity : AppCompatActivity() {
    private var fragmentlayout: FrameLayout? = null
    private var fragmentManager: FragmentManager? = null
    private var fragmentTransaction: FragmentTransaction? = null
    private var firstOnboardFragment: Onboarding1Fragment? = null
    private var secondOnboardFragment: Onboarding2Fragment? = null
    private var thirdOnboardFragment: Onboarding3Fragment? = null

    private var right_arrow: ImageView? = null
    private var skipText: TextView? = null


    private var index: Int = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_onboarding)
        init()
    }

    private fun init() {

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels

        firstOnboardFragment = Onboarding1Fragment()
        secondOnboardFragment = Onboarding2Fragment()
        thirdOnboardFragment = Onboarding3Fragment()

        fragmentlayout = findViewById(R.id.fragment_layout)
        right_arrow = findViewById(R.id.right_arrow)
        skipText = findViewById(R.id.skipText)

        right_arrow!!.layoutParams.width = (width * 0.1).roundToInt()
        right_arrow!!.layoutParams.height = (height * 0.03).roundToInt()

        switchFragment(firstOnboardFragment!!)

        right_arrow!!.setOnClickListener {
            if (index == 2) {
                switchFragment(secondOnboardFragment!!)
                index++
            } else if (index == 3) {
                switchFragment(thirdOnboardFragment!!)
                index++
                skipText!!.visibility = View.GONE
            } else if (index > 3) {

                startActivity(Intent(this@OnboardingActivity, RegisterAsActivity::class.java))
                finish()
            }
        }
        skipText!!.setOnClickListener {
            startActivity(
                Intent(
                    this@OnboardingActivity,
                    RegisterAsActivity::class.java
                )
            )
            finish()
        }
    }

    private fun switchFragment(fragment: Fragment) {
        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction!!.replace(R.id.fragment_layout, fragment)
        fragmentTransaction!!.commit()
    }
}