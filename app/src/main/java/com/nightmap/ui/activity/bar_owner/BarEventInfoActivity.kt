package com.nightmap.ui.activity.bar_owner

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.adapter.ImageSliderAdapter
import kotlinx.android.synthetic.main.activity_bar_event_info.*
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class BarEventInfoActivity : AppCompatActivity(), View.OnClickListener {
    private var edit_event: Button? = null
    private var back_button: ImageView? = null
    private var event_name: TextView? = null
    private var ageLimit: TextView? = null
    private var people_going: TextView? = null
    private var description: TextView? = null
    private var dateText: TextView? = null
    private var timeText: TextView? = null
    private var imageLayout: RelativeLayout? = null
    private var viewPager: ViewPager2? = null
    private var spinKitView:SpinKitView?=null

    private var eventId: String = ""
    private var db: FirebaseFirestore? = null
    private var task: SomeTask? = null
    private var pagerAdapter: ImageSliderAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_event_info)
        init()
    }

    private fun init() {
        eventId = intent.getStringExtra("eventId")
        db = Firebase.firestore
        edit_event = findViewById(R.id.edit_event)
        back_button = findViewById(R.id.back_button)
        event_name = findViewById(R.id.event_name)
        ageLimit = findViewById(R.id.ageLimit)
        people_going = findViewById(R.id.people_going)
        description = findViewById(R.id.description)
        dateText = findViewById(R.id.dateText)
        timeText = findViewById(R.id.timeText)
        imageLayout = findViewById(R.id.imageLayout)
        viewPager = findViewById(R.id.pager)
        spinKitView=findViewById(R.id.spin_kit)

        edit_event!!.setOnClickListener(this)
        back_button!!.setOnClickListener(this)

        db!!.collection("Events").document(eventId).get().addOnSuccessListener { document ->
            val goingList: ArrayList<String> = document.get("usersGoing") as ArrayList<String>
            val url: ArrayList<String> = document.get("imagesUrl") as ArrayList<String>
            val timestamp: Timestamp = document.get("eventTime") as Timestamp
            val date: Date = timestamp.toDate()
            val day: String = DateFormat.format("dd", date) as String
            val month: String = DateFormat.format("MM", date) as String
            val year: String = DateFormat.format("yy", date) as String
            val hour: String = DateFormat.format("h", date) as String
            val am_pm: String = DateFormat.format("a", date).toString()

            event_name!!.text = document.get("title").toString()
            ageLimit!!.text = "${document.get("minAge")} to ${document.get("maxAge")} age"
            people_going!!.text = "${goingList.size} People Are Going"
            description!!.text = document.get("description").toString()
            dateText!!.text = "$day/$month/$year"
            timeText!!.text = "$hour $am_pm"

            task = SomeTask(url[0])
            pagerAdapter = ImageSliderAdapter(this, url, eventId, "EventImages",spinKitView!!)
            pager!!.adapter = pagerAdapter
            //task!!.execute()
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

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.edit_event -> {
                startActivity(
                    Intent(this, BarEventEditActivity::class.java).putExtra(
                        "eventID",
                        eventId
                    )
                )
            }
            R.id.back_button -> {
                onBackPressed()
            }
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
            var dr: Drawable = BitmapDrawable(this@BarEventInfoActivity!!.resources, result)

            imageLayout!!.background = dr
        }
    }
}