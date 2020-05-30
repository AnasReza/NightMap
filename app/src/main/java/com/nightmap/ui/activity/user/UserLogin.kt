package com.nightmap.ui.activity.user

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide.init
import com.nightmap.R
import com.nightmap.ui.activity.RegisterAsActivity
import com.nightmap.ui.activity.admin.AdminLoginActivity

class UserLogin : AppCompatActivity(), View.OnClickListener, View.OnLongClickListener {
    private var create_new_account:LinearLayout?=null
    private var logo:ImageView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_login)
        init()
    }

    private fun init() {
        logo=findViewById(R.id.logo)
        create_new_account=findViewById(R.id.create_new_account)

        logo!!.setOnLongClickListener(this)
        create_new_account!!.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.create_new_account->{
                startActivity(Intent(this,RegisterAsActivity::class.java))
                finish()
            }
        }
    }

    override fun onLongClick(v: View?): Boolean {
        when(v!!.id){
            R.id.logo->{
                startActivity(Intent(this, AdminLoginActivity::class.java))
                finish()
            }
        }
        return true
    }
}