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
import androidx.cardview.widget.CardView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.adapter.ImageSliderAdapter
import com.nightmap.ui.activity.LocationMapActivity
import kotlinx.android.synthetic.main.fragment_bar_home_details.*
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import java.util.*


class UserBarDetailsActivity : AppCompatActivity(), View.OnClickListener {
    private var leave_rating_button: Button? = null
    private var bottomLayout: LinearLayout? = null
    private var back_button: ImageView? = null
    private var crowdText:TextView?=null
    private var spinKitView: SpinKitView? = null
    private var info: ImageView? = null
    private var card: CardView? = null
    private var blockImage: ImageView? = null
    private var viewPager: ViewPager2? = null
    private var blockText: TextView? = null
    private var atmosphereNumber: TextView? = null
    private var musicNumber: TextView? = null
    private var barTitle: TextView? = null
    private var description: TextView? = null
    private var layout: LinearLayout? = null
    private var blurImage: ImageView? = null
    private var imageLayout: RelativeLayout? = null
    private var eventText: TextView? = null
    private var eventHolder: LinearLayout? = null
    private var barLocation: TextView? = null
    private var barId: String = ""
    private var userType: String = ""
    private var db: FirebaseFirestore? = null
    private var eventTask: EventTask? = null
    private var height: Int? = null
    private var width: Int? = null
    private var pagerAdapter: ImageSliderAdapter? = null
    private var userCheck: Boolean = true

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
        userType = intent.getStringExtra("userType")

        db = Firebase.firestore

        leave_rating_button = findViewById(R.id.leave_rating)
        bottomLayout = findViewById(R.id.bottomLayout)
        back_button = findViewById(R.id.back_button)
        atmosphereNumber = findViewById(R.id.atmosNumber)
        musicNumber = findViewById(R.id.musicNumber)
        barTitle = findViewById(R.id.barTitle)
        description = findViewById(R.id.description)
        eventHolder = findViewById(R.id.eventHolder)
        info = findViewById(R.id.info)
        card = findViewById(R.id.card)
        blockImage = findViewById(R.id.blockImage)
        blockText = findViewById(R.id.blockText)
        barLocation = findViewById(R.id.viewLocation)
        viewPager = findViewById(R.id.pager)
        eventText = findViewById(R.id.eventText)
        spinKitView = findViewById(R.id.spin_kit)
        crowdText=findViewById(R.id.crowdText)

        layout = findViewById(R.id.layout)
        blurImage = findViewById(R.id.imageBlur)
        imageLayout = findViewById(R.id.imageLayout)

        back_button!!.setOnClickListener(this)
        leave_rating_button!!.setOnClickListener(this)
        barLocation!!.setOnClickListener(this)

        if (userType == "admin") {
            leave_rating_button!!.visibility = View.GONE
            info!!.visibility = View.VISIBLE
            info!!.setOnClickListener(this)

        }

        db!!.collection("Events").get().addOnSuccessListener { documents ->
            var index: Int = 0
            for (doc in documents) {

                val bar = doc.get("barId").toString()
                if (barId == bar) {
                    eventText!!.visibility = View.VISIBLE
                    eventHolder!!.visibility = View.VISIBLE
                    val isDeleted: Boolean = doc.get("isDeleted") as Boolean
                    if (!isDeleted) {
                        index++
                        addEventLayout(doc)
                    }
                }
            }
            if (index == 0) {
                eventText!!.visibility = View.GONE
                eventHolder!!.visibility = View.GONE
            }
        }

        db!!.collection("Bars").document(barId).get().addOnSuccessListener { document ->

            atmosphereNumber!!.text = DecimalFormat("#.#").format(document.get("averageAtmosphere"))
            musicNumber!!.text = DecimalFormat("#.#").format(document.get("averageMusic"))
            barTitle!!.text = document.get("title").toString()
            description!!.text = document.get("description").toString()

            val crowdDouble:Double= document.get("averageCrowded") as Double
            if(crowdDouble<2.5){
                crowdText!!.text="Less Crowded"
            }else if(crowdDouble>=2.5){
                crowdText!!.text="More Crowded"
            }
            var imageUrl: ArrayList<String> = document.get("imagesURL") as ArrayList<String>
            // SomeTask(imageUrl[0]).execute()
            pagerAdapter = ImageSliderAdapter(this, imageUrl, barId, "BarImages", spinKitView!!)
            pager!!.adapter = pagerAdapter
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
        db!!.collection("Rating").orderBy("reviewDate", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { documents ->
                bottomLayout!!.removeAllViews()
                for (document in documents) {

                    val id = document.get("barId").toString()

                    if (barId == id) {

                        addBottomLayout(document)
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
        val id = document.id
        val inflator = LayoutInflater.from(this)
        val newView: View = inflator.inflate(R.layout.item_event, null)
        val mainLayout: RelativeLayout = newView.findViewById(R.id.mainLayout)
        val headline: TextView = newView.findViewById(R.id.headline)
        val spinKitView: SpinKitView = newView.findViewById(R.id.spin_kit)

        headline.text = document.get("title").toString()
        val url: ArrayList<String> = document.get("imagesUrl") as ArrayList<String>
        eventTask = EventTask(url[0], mainLayout)
        // eventTask!!.execute()
        mainLayout.setOnClickListener {

            startActivity(
                Intent(this, UserEventInfoActivity::class.java)
                    .putExtra("eventId", document.get("eventId").toString())
                    .putExtra("userType", userType)

            )

        }
        checkImage(url, mainLayout, id, "EventImages", spinKitView)
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
        var crowdImage:ImageView = newView.findViewById(R.id.crowdImage)
        var crowdText:TextView=newView.findViewById(R.id.crowdText)

        val id: String = document.id
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
        val crowdDouble:Double= document.get("crowdRating") as Double
        if(crowdDouble<1.7){
            crowdText.text="Less Crowded"
        }else if(crowdDouble>1.6&&crowdDouble<3.3){
            crowdText.text="Average Crowded"
        }else if(crowdDouble>3.2){
            crowdText.text="More Crowded"
        }


        if (imagesUrl == "") {
            Glide.with(this).asBitmap().load(R.drawable.user_placeholder)
                .apply(RequestOptions.circleCropTransform())
                .into(image)
        } else {
            userImage(imagesUrl, image, id)

        }


        bottomLayout!!.addView(newView)
    }

    private fun userImage(
        url: String,
        layout: ImageView,
        id: String
    ) {
        val myDir = File(this.filesDir, "UserImage/$id")
        if (myDir.exists()) {
            var imageFile = File(myDir, "image0.png")
            if (imageFile.exists()) {
                var b: Bitmap = BitmapFactory.decodeStream(FileInputStream(imageFile))
                Glide.with(this@UserBarDetailsActivity).asBitmap().load(b)
                    .apply(RequestOptions.circleCropTransform())
                    .into(layout)

                BackgroundUserTask(url, layout, myDir).execute()

            } else {
                BackgroundUserTask(url, layout, myDir).execute()
            }

        } else {
            myDir.mkdirs()

            BackgroundUserTask(url, layout, myDir).execute()

        }
    }

    private fun checkImage(
        list: ArrayList<String>,
        layout: RelativeLayout,
        id: String,
        imageFolder: String,
        spinKitView: SpinKitView
    ) {
        val myDir = File(this.filesDir, "$imageFolder/$id")
        if (myDir.exists()) {
            var imageFile = File(myDir, "image0.png")
            if (imageFile.exists()) {
                var b: Bitmap = BitmapFactory.decodeStream(FileInputStream(imageFile))
                var dr: Drawable = BitmapDrawable(this.resources, b)

                layout!!.background = dr
                for (x in 0 until list.size) {
                    BackgroundDownloadTask(list[x], x, layout, myDir, spinKitView).execute()
                }
            } else {
                BackgroundDownloadTask(list[0], 0, layout, myDir, spinKitView).execute()
            }

        } else {
            myDir.mkdirs()
            Log.d("Anas", "${myDir.exists()} boolean of folder")
            for (x in 0 until list.size) {

                BackgroundDownloadTask(list[x], x, layout, myDir, spinKitView).execute()
            }
        }
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
            R.id.info -> {
                info!!.visibility = View.GONE
                card!!.visibility = View.VISIBLE
            }
            R.id.viewLocation -> {
                Log.d("Anas","-$barId barid")
                startActivity(
                    Intent(this, BarLocation::class.java)
                        .putExtra("id", barId)

                )
            }
            R.id.card->{
                val hash= hashMapOf("status" to "pending")
                db!!.collection("Bars").document(barId).update(hash as Map<String, Any>).addOnSuccessListener {

                    info!!.visibility = View.VISIBLE
                    card!!.visibility = View.GONE
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

    override fun onBackPressed() {
        super.onBackPressed()
        userCheck = false

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

    inner class BackgroundDownloadTask(
        val url: String,
        val position: Int,
        val layout: RelativeLayout,
        val mainFile: File,
        val spinKitView: SpinKitView
    ) :
        AsyncTask<String, URL, Bitmap>() {

        override fun doInBackground(vararg params: String?): Bitmap {
            var length = 0
            val bitmap = getBitmapFromURL(url)
            val scaledBitmap = bitmap?.let { Bitmap.createScaledBitmap(it, 739, 415, false) }
            val bos = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.PNG, 0, bos)
            val bitmapdata = bos.toByteArray()
            val bs = ByteArrayInputStream(bitmapdata)

            val fos =
                FileOutputStream("${mainFile.absolutePath}${File.separator}image$position.png")

            while ({ length = bs.read(bitmapdata); length }() > 0) {
                fos.write(bitmapdata, 0, length)
            }
            fos.flush()
            fos.close()
            bs.close()
            return scaledBitmap!!
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            Log.e("Anas", "Downloading and saving file is complete")
            if (userCheck) {
                spinKitView.visibility = View.GONE
                var dr: Drawable = BitmapDrawable(this@UserBarDetailsActivity.resources, result)

                if (position == 0) {
                    layout!!.background = dr
                }
            }

        }
    }

    inner class BackgroundUserTask(
        val url: String,
        val layout: ImageView,
        val mainFile: File
    ) :
        AsyncTask<String, URL, Bitmap>() {

        override fun doInBackground(vararg params: String?): Bitmap {
            var length = 0
            val bitmap = getBitmapFromURL(url)
            val scaledBitmap = bitmap?.let { Bitmap.createScaledBitmap(it, 739, 415, false) }
            val bos = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.PNG, 0, bos)
            val bitmapdata = bos.toByteArray()
            val bs = ByteArrayInputStream(bitmapdata)

            val fos = FileOutputStream("${mainFile.absolutePath}${File.separator}image0.png")

            while ({ length = bs.read(bitmapdata); length }() > 0) {
                fos.write(bitmapdata, 0, length)
            }
            fos.flush()
            fos.close()
            bs.close()
            return scaledBitmap!!
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            if (userCheck) {
                Glide.with(this@UserBarDetailsActivity).asBitmap().load(result)
                    .apply(RequestOptions.circleCropTransform())
                    .into(layout)
            }

        }
    }
}