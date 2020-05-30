package com.nightmap.ui.activity.user

import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.utility.Preferences
import java.text.DecimalFormat
import java.util.*

class UserDetailsActivity : AppCompatActivity(), View.OnClickListener {
    private var view_location: TextView? = null
    private var image1: ImageView? = null
    private var image: ImageView? = null
    private var profile_heading: TextView? = null
    private var back_arrow: ImageView? = null
    private var profile_name: TextView? = null
    private var description: TextView? = null
    private var friend_count: TextView? = null
    private var messageText: TextView? = null

    private var bottomLayout: LinearLayout? = null
    private var newView: View? = null
    private var inflator: LayoutInflater? = null
    private var db: FirebaseFirestore? = null
    private var pref: Preferences? = null
    private var uid: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)
        init()
    }


    private fun init() {
        uid = intent.getStringExtra("uid")
        db = Firebase.firestore
        pref = Preferences(this)
        inflator = LayoutInflater.from(this)

        view_location = findViewById(R.id.view_location)
        image1 = findViewById(R.id.image1)
        bottomLayout = findViewById(R.id.bottomLayout)
        profile_heading = findViewById(R.id.profile_heading)
        back_arrow = findViewById(R.id.back_arrow)
        profile_name = findViewById(R.id.profile_name)
        description = findViewById(R.id.description)
        friend_count = findViewById(R.id.friend_count)
        messageText = findViewById(R.id.messageText)

        db!!.collection("User").document(uid).get().addOnSuccessListener { documentSnapshot ->
            val profileURl = documentSnapshot.get("profileImage").toString()
            val fullName = documentSnapshot.get("fullName").toString()
            val nameSpilited = fullName.split("\\s".toRegex())
            profile_heading!!.text = "${nameSpilited[0].toUpperCase()}'S PROFILE"
            profile_name!!.text = fullName
            description!!.text = documentSnapshot.get("about").toString()
            friend_count!!.text = "${documentSnapshot.get("friendCount")} Friends"
            if (profileURl != "") {
                Glide.with(this).asBitmap().load(profileURl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(image1!!)
            } else {
                Glide.with(this).asBitmap().load(R.drawable.user_placeholder)
                    .apply(RequestOptions.circleCropTransform())
                    .into(image1!!)
            }
        }
        db!!.collection("Rating").get().addOnSuccessListener { documents ->

            for (doc in documents) {
                val uidRate = doc.get("uid").toString()
                if (uid == uidRate) {
                    addBottomLayout(doc)
                }
            }
            if (bottomLayout!!.childCount==0){
                messageText!!.visibility=View.VISIBLE
            }
        }

        view_location!!.setOnClickListener(this)
        back_arrow!!.setOnClickListener(this)
    }

    private fun addBottomLayout(doc: QueryDocumentSnapshot) {
        var newView = inflator!!.inflate(R.layout.item_user_details_rate, null)
        val imageUrl:String= doc.get("imagesURl").toString()
        val timeStamp: Timestamp = doc.get("reviewDate") as Timestamp
        val date: Date = timeStamp.toDate()
        val day: String = DateFormat.format("dd", date).toString()
        val month: String = DateFormat.format("MM", date).toString()
        val year: String = DateFormat.format("yy", date).toString()
        val hour: String = DateFormat.format("hh", date).toString()
        val minute: String = DateFormat.format("mm", date).toString()

        val image: ImageView = newView!!.findViewById(R.id.image1)
        val name: TextView = newView.findViewById(R.id.userName)
        val rateMessage: TextView = newView.findViewById(R.id.userReview)
        val dateText: TextView = newView.findViewById(R.id.date)
        val atmosNumber: TextView = newView.findViewById(R.id.atmosNumber)
        val musicNumber: TextView = newView.findViewById(R.id.musicNumber)

        name.text = doc.get("userName").toString()
        rateMessage.text = doc.get("review").toString()
        dateText.text="On $day/$month/$year at $hour:$minute"
        atmosNumber.text = DecimalFormat("#.#").format(doc.get("atmosphereRating"))
        musicNumber.text = DecimalFormat("#.#").format(doc.get("musicRating"))

if(imageUrl!=""){
    Glide.with(this).asBitmap().load(imageUrl)
        .apply(RequestOptions.circleCropTransform())
        .into(image!!)
}else{
    Glide.with(this).asBitmap().load(R.drawable.user_placeholder)
        .apply(RequestOptions.circleCropTransform())
        .into(image!!)
}

        bottomLayout!!.addView(newView)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.view_location -> {
                startActivity(Intent(this, UsersMapsActivity::class.java))
            }
            R.id.back_arrow -> {
                onBackPressed()
            }
        }
    }
}