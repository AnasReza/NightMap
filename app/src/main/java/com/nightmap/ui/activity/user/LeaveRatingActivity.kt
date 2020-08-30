package com.nightmap.ui.activity.user

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.utility.Preferences

class LeaveRatingActivity : AppCompatActivity(), View.OnClickListener {
    private var atmosRating: RatingBar? = null
    private var music_rating: RatingBar? = null
    private var atomsphere_number: TextView? = null
    private var music_number: TextView? = null
    private var gender_seekbar: SeekBar? = null
    private var crowd_seekbar: SeekBar? = null
    private var description_text: EditText? = null
    private var submit_rating: Button? = null
    private var back_button: ImageView? = null

    private var db: FirebaseFirestore? = null
    private var barId: String = ""
    private var pref: Preferences? = null
    private var genderDouble = 0.0
    private var crowdDouble = 0.0
    private var genderText: String = ""
    private var crowdText: String = ""
    private var ratedCount: Int = 0
    private var alreadyRated: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_leave_rate)
        init()
    }

    private fun init() {
        db = Firebase.firestore
        pref = Preferences(this)
        barId = intent!!.getStringExtra("barId")
        atmosRating = findViewById(R.id.atmosphere_rating)
        music_rating = findViewById(R.id.music_rating)
        gender_seekbar = findViewById(R.id.gender_seekbar)
        crowd_seekbar = findViewById(R.id.crowd_seekbar)
        atomsphere_number = findViewById(R.id.atmosphere_number)
        music_number = findViewById(R.id.music_number)
        description_text = findViewById(R.id.description_text)
        submit_rating = findViewById(R.id.submit_rating)
        back_button = findViewById(R.id.back_button)

        gender_seekbar!!.max = 50
        crowd_seekbar!!.max = 50

        db!!.collection("User").document(pref!!.getUserID()!!).get()
            .addOnSuccessListener { documentSnapshot ->
                var ratedList: ArrayList<String> =
                    documentSnapshot.get("ratedBar") as ArrayList<String>
                ratedCount = ratedList.size
                for (x in 0 until ratedList.size) {
                    if (barId == ratedList[x]) {
                        alreadyRated = true
                    }
                }
            }
        gender_seekbar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                String
                var currentProgress = progress * 0.1f
                var yourProgress: String = String.format("%.1f", currentProgress)
                genderDouble = yourProgress.toDouble()

                if (genderDouble < 1.7) {
                    genderText = "more men and less women"

                } else if (genderDouble > 1.6 && genderDouble < 3.3) {
                    genderText = "men and women are equal"

                } else if (genderDouble > 3.2) {
                    genderText = "more women and less men"
                    description_text!!.setText("The Review is Highly Crowded")
                }
                if (crowdText == "") {
                    description_text!!.setText("This bar has $genderText")
                } else {
                    description_text!!.setText("This bar has $genderText and is $crowdText")
                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
        crowd_seekbar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                var currentProgress = progress * 0.1f
                var yourProgress: String = String.format("%.1f", currentProgress)

                crowdDouble = yourProgress.toDouble()
                if (crowdDouble < 1.7) {
                    crowdText = "Less Crowded"

                } else if (crowdDouble > 1.6 && crowdDouble < 3.3) {
                    crowdText = "Moderate Crowd"

                } else if (crowdDouble > 3.2) {
                    crowdText = "Highly Crowded"

                }
                if (genderText == "") {
                    description_text!!.setText("This bar is $crowdText")
                } else {
                    description_text!!.setText("This bar has $genderText and is $crowdText")
                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
        submit_rating!!.setOnClickListener(this)
        atmosRating!!.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            atomsphere_number!!.text = rating.toString()
        }

        music_rating!!.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            music_number!!.text = rating.toString()
        }

        back_button!!.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.submit_rating -> {
                submitRating()


            }
            R.id.back_button -> {
                onBackPressed()
            }
        }
    }

    private fun submitRating() {
        if (ratedCount < 3) {
            if (!alreadyRated) {
                val atmosNumber: Double = atomsphere_number!!.text.toString().toDouble()
                val musicNumber: Double = music_number!!.text.toString().toDouble()
                var userProfileUrl: String
                var review = description_text!!.text.toString()
                db!!.collection("User").document(pref!!.getUserID()!!).get()
                    .addOnSuccessListener { document ->
                        userProfileUrl = document.get("profileImage").toString()

                        val hashMap =
                            hashMapOf(
                                "atmosphereRating" to atmosNumber,
                                "barId" to barId,
                                "crowdRating" to crowdDouble,
                                "genderRating" to genderDouble,
                                "imagesURL" to userProfileUrl,
                                "musicRating" to musicNumber,
                                "review" to review,
                                "reviewDate" to FieldValue.serverTimestamp(),
                                "uid" to pref!!.getUserID(),
                                "userName" to pref!!.getUserName()
                            )
                        db!!.collection("Rating").add(hashMap).addOnSuccessListener { document ->
                            db!!.collection("Bars").document(barId).get()
                                .addOnSuccessListener { documentSnapshot ->
                                    var atmos: Double =
                                        documentSnapshot.get("averageAtmosphere") as Double
                                    var music: Double =
                                        documentSnapshot.get("averageMusic") as Double

                                    var avgAtmos = (atmosNumber + atmos) / 2
                                    var avgMusic = (musicNumber + music) / 2

                                    val hashMap = hashMapOf(
                                        "averageAtmosphere" to avgAtmos,
                                        "averageMusic" to avgMusic
                                    )
                                    db!!.collection("Bars").document(barId)
                                        .update(hashMap as Map<String, Any>)
                                        .addOnSuccessListener { _ ->
                                            Toast.makeText(
                                                this@LeaveRatingActivity,
                                                "Your Rating has been submitted",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            onBackPressed()
                                        }
                                }

                        }

                    }
            } else {
                Toast.makeText(
                    this@LeaveRatingActivity,
                    "You have already rated this bar",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }else{
            Toast.makeText(
                this@LeaveRatingActivity,
                "Your today's rating limit is completed",
                Toast.LENGTH_SHORT
            ).show()
        }


    }
}