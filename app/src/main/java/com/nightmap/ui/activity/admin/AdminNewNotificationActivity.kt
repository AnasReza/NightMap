package com.nightmap.ui.activity.admin

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.utility.Preferences

class AdminNewNotificationActivity : AppCompatActivity() {
    private var title: EditText? = null
    private var description: EditText? = null
    private var send: Button? = null
    private var back_button:ImageView?=null

    private var db: FirebaseFirestore? = null
    private var pref: Preferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_new_notification)
        init()
    }

    private fun init() {
        db = Firebase.firestore
        pref = Preferences(this)

        title = findViewById(R.id.title)
        description = findViewById(R.id.description)
        send = findViewById(R.id.send_notification)
        back_button=findViewById(R.id.back_button)

        send!!.setOnClickListener {
            val desc = description!!.text.toString()
            val titleText = title!!.text.toString()
            val hashValue = hashMapOf("description" to desc, "title" to titleText)
            db!!.collection("CustomNotifications").add(hashValue)
                .addOnSuccessListener {
                    Toast.makeText(this,"NOTIFICATION CREATED",Toast.LENGTH_SHORT).show()
                    onBackPressed() }
        }

        back_button!!.setOnClickListener {
            onBackPressed()
        }
    }
}