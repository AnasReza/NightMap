package com.nightmap.ui.activity.admin

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.nightmap.R
import com.nightmap.ui.activity.user.UserHomeActivity
import com.nightmap.utility.Preferences

class AdminLoginActivity : AppCompatActivity(), View.OnClickListener {
    private var admin_login: Button? = null
    private var emailText: EditText? = null
    private var passwordText: EditText? = null
    private var back_button:ImageView?=null
    private var spin_kit:SpinKitView?=null

    private var auth: FirebaseAuth? = null
    private var pref:Preferences?=null
    private var db:FirebaseFirestore?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)

        init()
    }

    private fun init() {
        auth = FirebaseAuth.getInstance()
        pref=Preferences(this)
        db=Firebase.firestore

        admin_login = findViewById(R.id.admin_login)
        emailText = findViewById(R.id.email_text)
        passwordText = findViewById(R.id.password_text)
        back_button=findViewById(R.id.back_button)
        spin_kit=findViewById(R.id.spin_kit)

        admin_login!!.setOnClickListener(this)
        back_button!!.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.admin_login -> {
                loginInoAdmin()
            }
            R.id.back_button->{
                spin_kit!!.visibility=View.GONE
                onBackPressed()
            }
        }
    }

    private fun loginInoAdmin() {
        spin_kit!!.visibility=View.VISIBLE
        val email: String = emailText!!.text.toString()
        val password: String = passwordText!!.text.toString()
        auth!!.signInWithEmailAndPassword(email,password).addOnCompleteListener {task->
            if(task.isSuccessful){
               val user=task.result?.user
                val id=user!!.uid
                FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        task.exception!!.printStackTrace()
                        return@addOnCompleteListener
                    }

                    val token = task.result?.token
                    val tokenMap = hashMapOf("token" to token)
                    db!!.collection("Admin").document(id)
                        .update(tokenMap as Map<String, Any>).addOnSuccessListener {
                            spin_kit!!.visibility=View.GONE
                            pref!!.setFirstCheck(true)
                            pref!!.setAdminID(id)
                            pref!!.setAdminPassword(password)
                            pref!!.setLogin(true)
                            pref!!.setUserType("admin")
                            startActivity(Intent(this@AdminLoginActivity, AdminHomeActivity::class.java))
                            finish()
                        }
                }
            }else{

                Toast.makeText(this, "EMAIL OR PASSWORD IS INCORRECT",Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            exception.printStackTrace()

        }
    }
}