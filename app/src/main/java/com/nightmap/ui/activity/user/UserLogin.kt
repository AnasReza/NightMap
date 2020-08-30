package com.nightmap.ui.activity.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.nightmap.R
import com.nightmap.ui.activity.ReLoginOTP
import com.nightmap.ui.activity.RegisterAsActivity
import com.nightmap.ui.activity.admin.AdminLoginActivity
import kotlinx.android.synthetic.main.activity_otp.*

class UserLogin : AppCompatActivity(), View.OnClickListener, View.OnLongClickListener {
    private var create_new_account: LinearLayout? = null
    private var logo: ImageView? = null
    private var phone_edit_text: EditText? = null
    private var signin: Button?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_login)
        init()
    }

    private fun init() {
        logo = findViewById(R.id.logo)
        create_new_account = findViewById(R.id.create_new_account)
        phone_edit_text = findViewById(R.id.phone_number_edit_text)
        signin=findViewById(R.id.sign_in)

        create_new_account!!.setOnClickListener(this)
        signin!!.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.create_new_account -> {
                startActivity(Intent(this, UserRegisterActivity::class.java))

            }
            R.id.sign_in -> {
                val phone_number=phone_edit_text!!.text.toString()
                startActivity(
                    Intent(this, ReLoginOTP::class.java).putExtra(
                        "phone",
                        "$phone_number"
                    )
                )
                finish()
            }
        }
    }

    override fun onLongClick(v: View?): Boolean {
        when (v!!.id) {
            R.id.logo -> {
                startActivity(Intent(this, AdminLoginActivity::class.java))
                finish()
            }
        }
        return true
    }
}