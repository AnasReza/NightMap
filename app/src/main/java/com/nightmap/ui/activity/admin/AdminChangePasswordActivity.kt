package com.nightmap.ui.activity.admin

import android.os.Bundle
import android.util.DisplayMetrics
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

class AdminChangePasswordActivity : AppCompatActivity(), View.OnClickListener {
    private var form_layout: LinearLayout? = null
    private var success_layout: RelativeLayout? = null
    private var change_password: Button? = null
    private var headline: TextView? = null
    private var back_arrow: ImageView? = null
    private var center_layout: RelativeLayout? = null
    private var current_password: EditText? = null
    private var new_password: EditText? = null
    private var reenter_password: EditText? = null
    private var tick: ImageView? = null
    private var pref: Preferences? = null
    private var db: FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_change_password)
        init()
    }

    private fun init() {
        pref = Preferences(this)
        db = Firebase.firestore

        form_layout = findViewById(R.id.form_layout)
        success_layout = findViewById(R.id.success_layout)
        change_password = findViewById(R.id.change_password)
        headline = findViewById(R.id.headLine)
        back_arrow = findViewById(R.id.back_arrow)
        center_layout = findViewById(R.id.center_layout)
        tick = findViewById(R.id.tick)
        current_password = findViewById(R.id.current_password)
        new_password = findViewById(R.id.new_password)
        reenter_password = findViewById(R.id.re_enter_password)

        change_password!!.setOnClickListener(this)
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
            R.id.change_password -> {

                val currentPassword = current_password!!.text.toString()
                val newPassword = new_password!!.text.toString()
                val reEnterPassword = reenter_password!!.text.toString()
                val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
                if (currentPassword.isNotEmpty() && newPassword.isNotEmpty() && reEnterPassword.isNotEmpty()) {
                    if (currentPassword != newPassword) {
                        if (currentPassword == pref!!.getAdminPassword()) {
                            if (newPassword == reEnterPassword) {
                                var credential: AuthCredential = EmailAuthProvider.getCredential(
                                    user!!.email!!,
                                    pref!!.getAdminPassword()!!
                                )
                                user!!.reauthenticate(credential).addOnCompleteListener {
                                    user!!.updatePassword(newPassword)
                                        .addOnSuccessListener {
                                            success_layout!!.visibility = View.VISIBLE
                                            form_layout!!.visibility = View.GONE
                                            headline!!.visibility = View.GONE
//                                        val hashMap = hashMapOf("email" to newEmail)
//                                        db!!.collection("Admin").document(pref!!.getAdminID()!!)
//                                            .update(
//                                                hashMap as Map<String, Any>
//                                            )
                                        }
                                }
                            } else {
                                Toast.makeText(
                                    this,
                                    "Your New Password does not Match",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }


                        } else {
                            Toast.makeText(
                                this,
                                "Your Current Password is not equal with the Registered Password",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Your Current Password and New Password is same",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    Toast.makeText(
                        this,
                        "Current Password or New Password Field is Empty",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            R.id.back_arrow -> {
                onBackPressed()
            }
        }
    }

}