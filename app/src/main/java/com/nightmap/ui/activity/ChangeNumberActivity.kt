package com.nightmap.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.utility.Preferences

class ChangeNumberActivity : AppCompatActivity(), View.OnClickListener {
    private var back_button: ImageView? = null
    private var change_number: Button? = null


    private var pref: Preferences? = null
    private var db: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_change_number)
        init()
    }

    private fun init() {
        pref = Preferences(this)
        back_button = findViewById(R.id.back_button)
        change_number = findViewById(R.id.change_number)

        db = Firebase.firestore
        back_button!!.setOnClickListener(this)
        change_number!!.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.back_button -> {
                onBackPressed()
            }
            R.id.change_number -> {
                if (pref!!.getUserType() == "user") {

                    changeUser()
                }else if(pref!!.getUserType() == "bar"){
                    changeBar()
                }
            }
        }
    }

    private fun changeBar() {
        val newHashMap = hashMapOf("phoneNumber" to "123456789")
        db!!.collection("Bars").document(pref!!.getUserID()!!)
            .update(newHashMap as Map<String, Any>).addOnSuccessListener {
                Toast.makeText(this,"Your Number is changed",Toast.LENGTH_SHORT).show()
                Log.e("Anas","Your Number is changed")
            }.addOnFailureListener { exception -> exception.printStackTrace() }
    }

    private fun changeUser() {
        val newHashMap = hashMapOf("phoneNumber" to "123456789")
        db!!.collection("User").document(pref!!.getUserID()!!)
            .update(newHashMap as Map<String, Any>).addOnSuccessListener {
    Toast.makeText(this,"Your Number is changed",Toast.LENGTH_SHORT).show()
                Log.e("Anas","Your Number is changed")
        }.addOnFailureListener { exception -> exception.printStackTrace() }
    }
}