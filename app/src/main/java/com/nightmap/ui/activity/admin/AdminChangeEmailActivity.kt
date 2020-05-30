package com.nightmap.ui.activity.admin

import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.nightmap.R
import org.w3c.dom.Text
import kotlin.math.roundToInt

class AdminChangeEmailActivity : AppCompatActivity(), View.OnClickListener {
    private var form_layout: LinearLayout? = null
    private var success_layout: RelativeLayout? = null
    private var change_email: Button? = null
    private var headline: TextView? = null
    private var back_arrow: ImageView? = null
    private var center_layout: RelativeLayout? = null
    private var tick: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_change_email)
        init()
    }

    private fun init() {
        form_layout = findViewById(R.id.form_layout)
        success_layout = findViewById(R.id.success_layout)
        change_email = findViewById(R.id.change_email)
        headline = findViewById(R.id.headLine)
        back_arrow = findViewById(R.id.back_arrow)
        center_layout = findViewById(R.id.center_layout)
        tick = findViewById(R.id.tick)

        change_email!!.setOnClickListener(this)
        back_arrow!!.setOnClickListener(this)

        resizeLayout()
    }

    private fun resizeLayout() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels

        center_layout!!.layoutParams.width= (width*0.4).roundToInt()
        center_layout!!.layoutParams.height= (width*0.4).roundToInt()
        tick!!.layoutParams.width= (width*0.2).roundToInt()
        tick!!.layoutParams.height= (width*0.21).roundToInt()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.change_email -> {
                success_layout!!.visibility = View.VISIBLE
                form_layout!!.visibility = View.GONE
                headline!!.visibility = View.GONE
            }
            R.id.back_arrow -> {
                onBackPressed()
            }
        }
    }
}