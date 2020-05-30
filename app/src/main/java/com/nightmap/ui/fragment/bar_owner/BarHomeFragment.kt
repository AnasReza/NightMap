package com.nightmap.ui.fragment.bar_owner

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.text.format.DateFormat
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.ui.activity.user.UserEventInfoActivity
import com.nightmap.utility.Preferences
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class BarHomeFragment : Fragment() {

    private var bottomLayout: LinearLayout? = null
    private var imageLayout: RelativeLayout? = null
    private var barName: TextView? = null
    private var ratedText: TextView? = null
    private var description: TextView? = null
    private var atmosphereNumber: TextView? = null
    private var musicNumber: TextView? = null
    private var message: TextView? = null
    private var eventHolder: LinearLayout? = null

    private var newView: View? = null
    private var inflator: LayoutInflater? = null
    private var db: FirebaseFirestore? = null
    private var pref: Preferences? = null
    private var imageTask: SomeTask? = null
    private var eventTask: EventTask? = null
    private var height: Int = 0
    private var width: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_bar_home_details, container, false)
        init(view)
        return view
    }

    private fun init(view: View?) {
        val displayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        height = displayMetrics.heightPixels
        width = displayMetrics.widthPixels

        db = Firebase.firestore
        pref = Preferences(activity!!)
        bottomLayout = view!!.findViewById(R.id.bottomLayout)
        imageLayout = view!!.findViewById(R.id.imageLayout)
        barName = view!!.findViewById(R.id.barName)
        ratedText = view!!.findViewById(R.id.ratedNumber)
        description = view!!.findViewById(R.id.description)
        atmosphereNumber = view!!.findViewById(R.id.atmosNumber)
        musicNumber = view!!.findViewById(R.id.musicNumber)
        eventHolder = view!!.findViewById(R.id.eventHolder)
        message = view!!.findViewById(R.id.message)

        db!!.collection("Bars").document(pref!!.getBarID()!!).get()
            .addOnSuccessListener { document ->
                val imageList: ArrayList<String> = document.get("imagesURL") as ArrayList<String>

                barName!!.text = document.get("title").toString()
                ratedText!!.text = "${document.get("todayReview").toString()} RATED TONIGHT"
                description!!.text = document.get("description").toString()
                atmosphereNumber!!.text =
                    DecimalFormat("#.#").format(document.get("averageAtmosphere"))
                musicNumber!!.text = DecimalFormat("#.#").format(document.get("averageMusic"))

                imageTask = SomeTask(imageList[0])
                imageTask!!.execute()
            }

        db!!.collection("Events").get().addOnSuccessListener { documents ->
            var index: Int = 0
            for (doc in documents) {
                val barId = doc.get("barId").toString()
                val isDeleted: Boolean = doc.get("isDeleted") as Boolean
                if (pref!!.getBarID() == barId) {
                    if (!isDeleted) {
                        message!!.visibility = View.GONE
                        eventHolder!!.visibility = View.VISIBLE
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
        db!!.collection("Rating").get().addOnSuccessListener { documents->
            for (doc in documents){
                var index:Int=0
                val barId=doc.get("barId").toString()
                if(pref!!.getBarID()==barId){
                    index++
                    addBottomLayout(doc)
                }
            }

        }
    }

    private fun addEventLayout(document: QueryDocumentSnapshot) {
        val inflator = LayoutInflater.from(activity)
        val newView: View = inflator.inflate(R.layout.item_event, null)
        val mainLayout: RelativeLayout = newView.findViewById(R.id.mainLayout)
        val headline: TextView = newView.findViewById(R.id.headline)

        headline.text = document.get("title").toString()
        val url: ArrayList<String> = document.get("imageUrl") as ArrayList<String>
        eventTask = EventTask(url[0], mainLayout)
        eventTask!!.execute()
        mainLayout.setOnClickListener {
            activity!!.startActivity(
                Intent(activity, UserEventInfoActivity::class.java)
                    .putExtra("eventId", document.get("eventId").toString()).putExtra("url", url[0])
            )

        }

        mainLayout.layoutParams.width = (width!! * 0.4).toInt()
        eventHolder!!.addView(newView)

    }

    override fun onPause() {
        super.onPause()
        if (imageTask != null) {
            if (imageTask!!.status == AsyncTask.Status.RUNNING) {
                imageTask!!.cancel(true)
            }
        }
    }

    private fun addBottomLayout(document: QueryDocumentSnapshot) {
        val inflator = LayoutInflater.from(activity)
        val newView = inflator.inflate(R.layout.item_user_details_rate, null)

        var image: ImageView = newView.findViewById(R.id.image1)
        var userName: TextView = newView.findViewById(R.id.userName)
        var userReview: TextView = newView.findViewById(R.id.userReview)
        var atmosNumber: TextView = newView.findViewById(R.id.atmosNumber)
        var musicNumber: TextView = newView.findViewById(R.id.musicNumber)
        var date: TextView = newView.findViewById(R.id.date)

        var imagesUrl: String = document.get("imagesURL").toString()
        val timeStamp: Timestamp = document.get("reviewDate") as Timestamp
        val dateT: Date =timeStamp.toDate()
        val day:String= DateFormat.format("dd",dateT).toString()
        val month:String= DateFormat.format("MM",dateT).toString()
        val year:String= DateFormat.format("yy",dateT).toString()
        val hour:String= DateFormat.format("hh",dateT).toString()
        val min:String= DateFormat.format("mm",dateT).toString()
        val dateString="$day/$month/$year at $hour:$min"

        atmosNumber!!.text = DecimalFormat("#.#").format(document.get("atmosphereRating"))
        musicNumber!!.text = DecimalFormat("#.#").format(document.get("musicRating"))
        userName!!.text = document.get("userName").toString()
        userReview!!.text = document.get("review").toString()
        date!!.text =dateString

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
            if (activity != null) {
                var dr: Drawable = BitmapDrawable(activity!!.resources, result)

                imageLayout!!.background = dr
            }

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
            var dr: Drawable = BitmapDrawable(activity!!.resources, result)

            layout!!.background = dr
        }
    }
}