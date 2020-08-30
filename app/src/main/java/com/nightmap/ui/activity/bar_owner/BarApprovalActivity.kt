package com.nightmap.ui.activity.bar_owner

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.utility.Preferences

class BarApprovalActivity : AppCompatActivity() {
    private var db: FirebaseFirestore? = null
    private var pref: Preferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_approval)
        init()
    }

    private fun init() {
        db = Firebase.firestore
        pref = Preferences(this)

        db!!.collection("Bars").document(pref!!.getBarID()!!).get()
            .addOnSuccessListener { documentSnapshot ->
                val status = documentSnapshot.get("status").toString()
                if (status == "approved") {
                    pref!!.setBarStatus("approved")
                    startActivity(Intent(this, BarHomeActivity::class.java))
                    finish()
                }
            }
    }
}