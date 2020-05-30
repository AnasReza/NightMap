package com.nightmap.ui.activity.admin

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.nightmap.R

class AdminLoginActivity : AppCompatActivity(), View.OnClickListener {
    private var admin_login: Button? = null
    private var emailText: EditText? = null
    private var passwordText: EditText? = null
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)

        init()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth!!.currentUser

    }

    private fun init() {
        auth = FirebaseAuth.getInstance()

        admin_login = findViewById(R.id.admin_login)
        emailText = findViewById(R.id.email_text)
        passwordText = findViewById(R.id.password_text)

        admin_login!!.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.admin_login -> {
                loginInoAdmin()
                //startActivity(Intent(this@AdminLoginActivity, AdminHomeActivity::class.java))
            }
        }
    }

    private fun loginInoAdmin() {
        val email: String = emailText!!.text.toString()
        val password: String = passwordText!!.text.toString()
        auth!!.signInWithEmailAndPassword(email,password).addOnCompleteListener {task->
            if(task.isSuccessful){
                Log.i("Anas","ADMIN LOGIN SUCCESSFUL")
            }else{
                Log.e("Anas","ADMIN LOGIN NOT SUCCESSFUL")
            }
        }.addOnFailureListener { exception ->
            exception.printStackTrace()
        }
    }
}