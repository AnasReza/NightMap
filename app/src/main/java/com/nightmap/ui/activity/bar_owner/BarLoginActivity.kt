package com.nightmap.ui.activity.bar_owner

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.ui.activity.user.UserHomeActivity
import com.nightmap.utility.Preferences

class BarLoginActivity : AppCompatActivity(), View.OnClickListener {
    private var email: EditText? = null
    private var password: EditText? = null
    private var login: Button? = null
    private var barText:TextView?=null
    private var spinKit:SpinKitView?=null
    private var back_button:ImageView?=null

    private var auth:FirebaseAuth?=null
    private var db:FirebaseFirestore?=null
    private var pref:Preferences?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_login)
        init()
    }

    private fun init() {
        auth= FirebaseAuth.getInstance()
        pref=Preferences(this)
        db=Firebase.firestore

        email = findViewById(R.id.email_edit_text)
        password = findViewById(R.id.password_edit_text)
        login = findViewById(R.id.login)
        barText=findViewById(R.id.bar_register)
        spinKit=findViewById(R.id.spin_kit)
        back_button=findViewById(R.id.back_button)

        login!!.setOnClickListener(this)
        barText!!.setOnClickListener(this)
        back_button!!.setOnClickListener(this)
    }
    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.login->{
                spinKit!!.visibility=View.VISIBLE
                val emailString=email!!.text.toString()
                val passwordString=password!!.text.toString()
                if(emailString.isNotEmpty() && passwordString.isNotEmpty()){
                    auth!!.signInWithEmailAndPassword(emailString,passwordString).addOnCompleteListener { task->
                        if (task.isSuccessful){
                            val user=task.result?.user
                            val id=user!!.uid
                            db!!.collection("Bars").document(id).get().addOnSuccessListener { documentSnapshot ->
                                val status=documentSnapshot.get("status").toString()
                                if (status=="pending"){
                                    FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
                                        if (!task.isSuccessful) {
                                            task.exception!!.printStackTrace()
                                            return@addOnCompleteListener
                                        }

                                        val token = task.result?.token
                                        val tokenMap = hashMapOf("token" to token)
                                        db!!.collection("Bars").document(id)
                                            .update(tokenMap as Map<String, Any>).addOnSuccessListener {
                                                pref!!.setFirstCheck(true)
                                                pref!!.setBarID(id)
                                                pref!!.setLogin(true)
                                                pref!!.setUserType("bar")
                                                pref!!.setBarPassword(passwordString)
                                                startActivity(
                                                    Intent(
                                                        this,
                                                        BarApprovalActivity::class.java
                                                    )
                                                )
                                                spinKit!!.visibility=View.GONE
                                                finish()
                                            }
                                    }

                                }else if(status=="approved"){
                                    FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
                                        if (!task.isSuccessful) {
                                            task.exception!!.printStackTrace()
                                            return@addOnCompleteListener
                                        }

                                        val token = task.result?.token
                                        val tokenMap = hashMapOf("token" to token)
                                        db!!.collection("Bars").document(id)
                                            .update(tokenMap as Map<String, Any>).addOnSuccessListener {
                                                pref!!.setFirstCheck(true)
                                                pref!!.setBarID(id)
                                                pref!!.setLogin(true)
                                                pref!!.setUserType("bar")
                                                pref!!.setBarPassword(passwordString)
                                                startActivity(
                                                    Intent(
                                                        this,
                                                        BarHomeActivity::class.java
                                                    )
                                                )
                                                spinKit!!.visibility=View.GONE
                                                finish()
                                            }
                                    }
                                }
                            }

                        }
                    }
                }
            }
            R.id.bar_register->{
                startActivity(Intent(this,BarOwnerRegisterActivity::class.java))
            }

            R.id.back_button->{
                onBackPressed()
            }
        }
    }
}