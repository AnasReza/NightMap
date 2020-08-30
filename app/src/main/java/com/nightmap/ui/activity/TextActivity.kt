package com.nightmap.ui.activity

import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.nightmap.R

class TextActivity : AppCompatActivity() {
    private var headline: TextView? = null
    private var back_button:ImageView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text)
        init()
    }

    private fun init() {
        headline = findViewById(R.id.headLine)
        back_button=findViewById(R.id.back_button)

        var str=intent.getStringExtra("headline")
        headline!!.text=str

        back_button!!.setOnClickListener { onBackPressed() }
    }
}