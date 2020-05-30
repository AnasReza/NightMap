package com.nightmap.ui.activity.user

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.text.format.DateFormat
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList


class UserBarDetailsActivity : AppCompatActivity(), View.OnClickListener {
    private var leave_rating_button: Button? = null
    private var bottomLayout: LinearLayout? = null
    private var back_button: ImageView? = null
    private var atmosphereNumber: TextView? = null
    private var musicNumber: TextView? = null
    private var barTitle: TextView? = null
    private var description: TextView? = null
    private var layout: LinearLayout? = null
    private var blurImage: ImageView? = null
    private var imageLayout: RelativeLayout? = null
    private var message: TextView? = null
    private var eventHolder: LinearLayout? = null
    private var barId: String = ""
    private var db: FirebaseFirestore? = null
    private var eventTask: EventTask? = null
    private var height: Int? = null
    private var width: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_bar_details)
        init()
    }

    private fun init() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        height = displayMetrics.heightPixels
        width = displayMetrics.widthPixels
        barId = intent.getStringExtra("id")

        db = Firebase.firestore

        leave_rating_button = findViewById(R.id.leave_rating)
        bottomLayout = findViewById(R.id.bottomLayout)
        back_button = findViewById(R.id.back_button)
        atmosphereNumber = findViewById(R.id.atmosNumber)
        musicNumber = findViewById(R.id.musicNumber)
        barTitle = findViewById(R.id.barTitle)
        description = findViewById(R.id.description)
        message = findViewById(R.id.message)
        eventHolder = findViewById(R.id.eventHolder)

        layout = findViewById(R.id.layout)
        blurImage = findViewById(R.id.imageBlur)
        imageLayout = findViewById(R.id.imageLayout)

        back_button!!.setOnClickListener(this)
        leave_rating_button!!.setOnClickListener(this)


        db!!.collection("Events").get().addOnSuccessListener { documents ->
            var index: Int = 0
            for (doc in documents) {

                val bar = doc.get("barId").toString()
                if (barId == bar) {
                    message!!.visibility = View.GONE
                    eventHolder!!.visibility = View.VISIBLE
                    val isDeleted: Boolean = doc.get("isDeleted") as Boolean
                    if (!isDeleted) {
                        index++
                        addEventLayout(doc)
                    }
                }
            }
            if (index == 0) {
                message!!.visibility = View.VISIBLE
                eventHolder!!.visibility = View.GONE
            }
        }

        db!!.collection("Bars").document(barId).get().addOnSuccessListener { document ->
            atmosphereNumber!!.text = DecimalFormat("#.#").format(document.get("averageAtmosphere"))
            musicNumber!!.text = DecimalFormat("#.#").format(document.get("averageMusic"))
            barTitle!!.text = document.get("title").toString()
            description!!.text = document.get("description").toString()
            var imageUrl: ArrayList<String> = document.get("imagesURL") as ArrayList<String>
            SomeTask(imageUrl[0]).execute()
        }
    }

    override fun onPause() {
        super.onPause()
        if (eventTask != null) {
            if (eventTask!!.status == AsyncTask.Status.RUNNING) {
                eventTask!!.cancel(true)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        db!!.collection("Rating").get().addOnSuccessListener { documents ->

            for (document in documents) {

                val id = document.get("barId").toString()
                Log.d("Anas", "$barId == $id  Document== ${document.id}")

                if (barId == id) {
                    Log.w("Anas", " onResume USER BAR DETAILS ACTIVITY")
                    bottomLayout!!.removeAllViews()
                    addBottomLayout(document)
                } else {
                    Log.e("Anas", "barID is not equal")
                }
            }
        }
        db!!.collection("Bars").document(barId).get().addOnSuccessListener { documentSnapshot ->
            atmosphereNumber!!.text =
                DecimalFormat("#.#").format(documentSnapshot.get("averageAtmosphere"))
            musicNumber!!.text = DecimalFormat("#.#").format(documentSnapshot.get("averageMusic"))
        }
    }

    private fun addEventLayout(document: QueryDocumentSnapshot) {
        val inflator = LayoutInflater.from(this)
        val newView: View = inflator.inflate(R.layout.item_event, null)
        val mainLayout: RelativeLayout = newView.findViewById(R.id.mainLayout)
        val headline: TextView = newView.findViewById(R.id.headline)

        headline.text = document.get("title").toString()
        val url: ArrayList<String> = document.get("imageUrl") as ArrayList<String>
        eventTask = EventTask(url[0], mainLayout)
        eventTask!!.execute()
        mainLayout.setOnClickListener {
            startActivity(
                Intent(this, UserEventInfoActivity::class.java)
                    .putExtra("eventId", document.get("eventId").toString()).putExtra("url", url[0])
            )

        }

        mainLayout.layoutParams.width = (width!! * 0.4).toInt()
        eventHolder!!.addView(newView)

    }

    private fun addBottomLayout(document: QueryDocumentSnapshot) {
        val inflator = LayoutInflater.from(this)
        val newView = inflator.inflate(R.layout.item_user_details_rate, null)

        var image: ImageView = newView.findViewById(R.id.image1)
        var userName: TextView = newView.findViewById(R.id.userName)
        var userReview: TextView = newView.findViewById(R.id.userReview)
        var atmosNumber: TextView = newView.findViewById(R.id.atmosNumber)
        var musicNumber: TextView = newView.findViewById(R.id.musicNumber)
        var date: TextView = newView.findViewById(R.id.date)

        var imagesUrl: String = document.get("imagesURL").toString()
        val timeStamp: Timestamp = document.get("reviewDate") as Timestamp
        val dateT: Date = timeStamp.toDate()
        val day: String = DateFormat.format("dd", dateT).toString()
        val month: String = DateFormat.format("MM", dateT).toString()
        val year: String = DateFormat.format("yy", dateT).toString()
        val hour: String = DateFormat.format("hh", dateT).toString()
        val min: String = DateFormat.format("mm", dateT).toString()
        val dateString = "$day/$month/$year at $hour:$min"

        atmosNumber!!.text = DecimalFormat("#.#").format(document.get("atmosphereRating"))
        musicNumber!!.text = DecimalFormat("#.#").format(document.get("musicRating"))
        userName!!.text = document.get("userName").toString()
        userReview!!.text = document.get("review").toString()
        date!!.text = dateString

        if (imagesUrl == "") {
            Glide.with(this).asBitmap().load(R.drawable.user_placeholder)
                .apply(RequestOptions.circleCropTransform())
                .into(image)
        } else {
            Glide.with(this).asBitmap().load(imagesUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(image)
        }


        bottomLayout!!.addView(newView)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.back_button -> {
                onBackPressed()
            }
            R.id.leave_rating -> {
                startActivity(
                    Intent(this, LeaveRatingActivity::class.java).putExtra(
                        "barId",
                        barId
                    )
                )
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
            var dr: Drawable = BitmapDrawable(this@UserBarDetailsActivity!!.resources, result)

            imageLayout!!.background = dr
        }
    }

    inner class EventTask(val url: String, val layout: RelativeLayout) :
        AsyncTask<String, URL, Bitmap>() {

        override fun doInBackground(vararg params: String?): Bitmap {
            val bitmap = getBitmapFromURL(url)
            val scaledBitmap = bitmap?.let { Bitmap.createScaledBitmap(it, 739, 415, false) }
            return scaledBitmap!!
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            var dr: Drawable = BitmapDrawable(this@UserBarDetailsActivity!!.resources, result)

            layout!!.background = dr
        }
    }
}