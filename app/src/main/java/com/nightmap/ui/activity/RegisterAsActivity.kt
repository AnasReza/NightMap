package com.nightmap.ui.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.nightmap.R
import com.nightmap.ui.activity.admin.AdminLoginActivity
import com.nightmap.ui.activity.bar_owner.BarOwnerRegisterActivity
import com.nightmap.ui.activity.user.UserLogin
import com.nightmap.ui.activity.user.UserRegisterActivity

class RegisterAsActivity : AppCompatActivity() {

    private var user_register: Button? = null
    private var bar_owner_register: Button? = null
    private var logo: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registeras)
        init()
    }

    private fun init() {
        user_register = findViewById(R.id.user_register)
        bar_owner_register = findViewById(R.id.bar_owner_register)
        logo = findViewById(R.id.logo)

        user_register!!.setOnClickListener {
            startActivity(Intent(this, UserRegisterActivity::class.java))
        }
        bar_owner_register!!.setOnClickListener {
            startActivity(Intent(this, BarOwnerRegisterActivity::class.java))
        }

        logo!!.setOnLongClickListener {
            startActivity(Intent(this@RegisterAsActivity,AdminLoginActivity::class.java))
            true
        }
    }


}