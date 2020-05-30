package com.nightmap.ui.activity.bar_owner

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.nightmap.R

class BarApprovalActivity : AppCompatActivity() {
    private var cdt:CountDownTimer?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_approval)
        init()
    }

    private fun init() {
        cdt = object : CountDownTimer(2000, 500) {
            override fun onFinish() {
                startActivity(Intent(this@BarApprovalActivity, BarHomeActivity::class.java))
                finish()
            }

            override fun onTick(millisUntilFinished: Long) {
            }
        }
        cdt!!.start()
    }
}