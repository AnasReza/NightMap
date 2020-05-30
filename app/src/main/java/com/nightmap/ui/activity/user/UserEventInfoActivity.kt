package com.nightmap.ui.activity.user


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.bumptech.glide.Glide.init
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.utility.Preferences
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class UserEventInfoActivity : AppCompatActivity() {
    private var desciption: TextView? = null
    private var event_name: TextView? = null
    private var agelimitText: TextView? = null
    private var peopleGoingText: TextView? = null
    private var dateText: TextView? = null
    private var timeText: TextView? = null
    private var goingSwitch: SwitchCompat? = null
    private var view_location_layout: LinearLayout? = null
    private var share_event_layout: LinearLayout? = null
    private var upperLayout: RelativeLayout? = null

    private var eventId: String = ""
    private var imageURL: String = ""
    private var db: FirebaseFirestore? = null
    private var task: SomeTask? = null
    private var pref: Preferences? = null
    private var check:Boolean=true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_event_info)
        init()
    }

    private fun init() {
        eventId = intent.getStringExtra("eventId")
        imageURL = intent.getStringExtra("url")
        db = Firebase.firestore
        pref = Preferences(this)

        desciption = findViewById(R.id.description)
        event_name = findViewById(R.id.event_name)
        agelimitText = findViewById(R.id.agelimitText)
        peopleGoingText = findViewById(R.id.peopleGoingText)
        dateText = findViewById(R.id.dateText)
        timeText = findViewById(R.id.timeText)
        goingSwitch = findViewById(R.id.goingSwitch)
        view_location_layout = findViewById(R.id.view_location_layout)
        share_event_layout = findViewById(R.id.share_event_layout)
        upperLayout = findViewById(R.id.upperLayout)

        db!!.collection("Events").document(eventId)
            .get().addOnSuccessListener { documentSnapshot ->
                val t = documentSnapshot.getTimestamp("time")
                val goingList: ArrayList<String> =
                    documentSnapshot.get("usersGoing") as ArrayList<String>
                val date: Date = t!!.toDate()
                val day: String = DateFormat.format("dd", date).toString()
                val month: String = DateFormat.format("MM", date).toString()
                val year: String = DateFormat.format("yy", date).toString()
                val cal = Calendar.getInstance()
                cal.time = date
                val hour: String = DateFormat.format("h", date).toString()
                val am_pm: String = DateFormat.format("a", date).toString()
                val str = "$hour $am_pm"

                for (x in 0 until goingList.size){
                    if(pref!!.getUserID()==goingList[x]){
                        check=false
                        goingSwitch!!.isChecked=true
                    }
                }
                desciption!!.text = documentSnapshot.get("description").toString()
                event_name!!.text = documentSnapshot.get("title").toString()
                agelimitText!!.text =
                    "${documentSnapshot.get("minAge")} to ${documentSnapshot.get("maxAge")} age"
                dateText!!.text = "$day/$month/$year"
                timeText!!.text = str
                peopleGoingText!!.text = "${goingList.size} People are going"
                task = SomeTask(imageURL)
                task!!.execute()

            }

        goingSwitch!!.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if(check){
                    db!!.collection("Events").document(eventId).get()
                        .addOnSuccessListener { documentSnapshot ->
                            check=false
                            val goingList: ArrayList<String> =
                                documentSnapshot.get("usersGoing") as ArrayList<String>
                            goingList!!.add(pref!!.getUserID()!!)
                            val hashMap = hashMapOf("usersGoing" to goingList)
                            db!!.collection("Events").document(eventId)
                                .update(hashMap as Map<String, Any>)

                        }
                }

            } else {
                db!!.collection("Events").document(eventId).get()
                    .addOnSuccessListener { documentSnapshot ->
                        val goingList: ArrayList<String> =
                            documentSnapshot.get("usersGoing") as ArrayList<String>

                        for(x in 0 until goingList.size){
                            if(pref!!.getUserID()==goingList[x]){
                                goingList.removeAt(x)
                            }
                        }
                        val hashMap = hashMapOf("usersGoing" to goingList)
                        db!!.collection("Events").document(eventId)
                            .update(hashMap as Map<String, Any>)
 check=true
                    }
            }
        }
    }

    private fun getBitmapFromURL(s: String): Bitmap? {
        return try {
            val url = URL(s)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }

    }

    inner class SomeTask(val url: String) :
        AsyncTask<String, URL, Bitmap>() {

        override fun doInBackground(vararg params: String?): Bitmap {
            val bitmap = getBitmapFromURL(url)
            val scaledBitmap = bitmap?.let { Bitmap.createScaledBitmap(it, 739, 415, false) }
            return scaledBitmap!!
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            var dr: Drawable = BitmapDrawable(this@UserEventInfoActivity!!.resources, result)

            upperLayout!!.background = dr
        }
    }

}