package com.nightmap.ui.activity.admin

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.utility.Preferences
import kotlin.math.roundToInt

class AdminChangeEmailActivity : AppCompatActivity(), View.OnClickListener {
    private var form_layout: LinearLayout? = null
    private var success_layout: RelativeLayout? = null
    private var change_email: Button? = null
    private var headline: TextView? = null
    private var back_arrow: ImageView? = null
    private var center_layout: RelativeLayout? = null
    private var current_email: EditText? = null
    private var new_email: EditText? = null
    private var tick: ImageView? = null
    private var pref: Preferences? = null
    private var db: FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_change_email)
        init()
    }

    private fun init() {
        pref = Preferences(this)
        db = Firebase.firestore

        form_layout = findViewById(R.id.form_layout)
        success_layout = findViewById(R.id.success_layout)
        change_email = findViewById(R.id.change_email)
        headline = findViewById(R.id.headLine)
        back_arrow = findViewById(R.id.back_arrow)
        center_layout = findViewById(R.id.center_layout)
        tick = findViewById(R.id.tick)
        current_email = findViewById(R.id.current_email)
        new_email = findViewById(R.id.new_email)

        change_email!!.setOnClickListener(this)
        back_arrow!!.setOnClickListener(this)

        resizeLayout()
    }

    private fun resizeLayout() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels

        center_layout!!.layoutParams.width = (width * 0.4).roundToInt()
        center_layout!!.layoutParams.height = (width * 0.4).roundToInt()
        tick!!.layoutParams.width = (width * 0.2).roundToInt()
        tick!!.layoutParams.height = (width * 0.21).roundToInt()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.change_email -> {

                val currentEmail = current_email!!.text.toString()
                val newEmail = new_email!!.text.toString()
                val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
                if (currentEmail.isNotEmpty() && newEmail.isNotEmpty()) {
                    if (currentEmail != newEmail) {
                        if (currentEmail == user!!.email) {
                            var credential: AuthCredential = EmailAuthProvider.getCredential(
                                user!!.email!!,
                                pref!!.getAdminPassword()!!
                            )
                            user!!.reauthenticate(credential).addOnCompleteListener {
                                user!!.updateEmail(newEmail)
                                    .addOnSuccessListener {
                                        success_layout!!.visibility = View.VISIBLE
                                        form_layout!!.visibility = View.GONE
                                        headline!!.visibility = View.GONE
                                        val hashMap = hashMapOf("email" to newEmail)
                                        db!!.collection("Admin").document(pref!!.getAdminID()!!)
                                            .update(
                                                hashMap as Map<String, Any>
                                            )
                                    }
                            }


                        } else {
                            Toast.makeText(
                                this,
                                "Your Current Email is not equal with the Registered Email",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Your Current Email and New Email is same",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    Toast.makeText(
                        this,
                        "Current Email or New Email Field is Empty",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Log.d("Anas", "${user!!.email} current email")
            }
            R.id.back_arrow -> {
                onBackPressed()
            }
        }
    }
}