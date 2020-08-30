package com.nightmap.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.nightmap.R
import com.nightmap.ui.activity.admin.AdminLoginActivity
import com.nightmap.ui.activity.bar_owner.BarLoginActivity
import com.nightmap.ui.activity.user.UserLogin
import com.nightmap.ui.activity.user.UserRegisterActivity

class RegisterAsActivity : AppCompatActivity() {

    private var user_register: Button? = null
    private var bar_owner_register: Button? = null
    private var logo: ImageView? = null
    private var back_button: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registeras)
        init()
    }

    private fun init() {
        user_register = findViewById(R.id.user_register)
        bar_owner_register = findViewById(R.id.bar_owner_register)
        logo = findViewById(R.id.logo)
        back_button = findViewById(R.id.back_button)

        user_register!!.setOnClickListener {
            startActivity(Intent(this, UserLogin::class.java))
            finish()
        }
        bar_owner_register!!.setOnClickListener {
            startActivity(Intent(this, BarLoginActivity::class.java))
            finish()
        }

        logo!!.setOnLongClickListener {
            startActivity(Intent(this@RegisterAsActivity, AdminLoginActivity::class.java))
            finish()
            true
        }
        back_button!!.setOnClickListener { onBackPressed() }
    }


}