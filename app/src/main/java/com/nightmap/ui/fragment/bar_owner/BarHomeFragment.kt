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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.adapter.ImageSliderAdapter
import com.nightmap.ui.activity.bar_owner.BarEventInfoActivity
import com.nightmap.utility.Preferences
import java.io.*
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
    private var eventText: TextView? = null
    private var eventHolder: LinearLayout? = null
    private var viewPager: ViewPager2? = null
    private var spin_kit: SpinKitView? = null
    private var eventDocList:ArrayList<QueryDocumentSnapshot> = ArrayList()
    private var ratingDocList:ArrayList<QueryDocumentSnapshot> = ArrayList()

    private var pagerAdapter: ImageSliderAdapter? = null
    private var userTask: BackgroundUserTask? = null
    private var db: FirebaseFirestore? = null
    private var pref: Preferences? = null
    private var imageTask: SomeTask? = null
    private var eventTask: EventTask? = null
    private var height: Int = 0
    private var width: Int = 0
    private var check: Boolean = true
    private var bundle: Bundle = Bundle()
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
        Log.i("Anas", "init method")

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
        eventText = view!!.findViewById(R.id.eventText)
        viewPager = view!!.findViewById(R.id.pager)
        spin_kit = view!!.findViewById(R.id.spin_kit)
        Log.d("Anas", "${pref!!.getBarID()} bar id")
        if (check) {
            db!!.collection("Bars").document(pref!!.getBarID()!!).get()
                .addOnSuccessListener { document ->
                    val title = document.get("title").toString()
                    val todayReviews = document.get("todayReviews").toString()
                    val descriptionString = document.get("description").toString()
                    val atmosNum = DecimalFormat("#.#").format(document.get("averageAtmosphere"))
                    val musicNum = DecimalFormat("#.#").format(document.get("averageMusic"))


                    val imageList: ArrayList<String> =
                        document.get("imagesURL") as ArrayList<String>

                    barName!!.text = title
                    ratedText!!.text = "$todayReviews RATED TONIGHT"
                    description!!.text = descriptionString
                    atmosphereNumber!!.text = atmosNum

                    musicNumber!!.text = musicNum

                    //imageTask = SomeTask(imageList[0])
                    if (activity != null) {
                        pagerAdapter = ImageSliderAdapter(
                            activity!!,
                            imageList,
                            pref!!.getBarID()!!,
                            "BarImages",
                            spin_kit!!
                        )

                        viewPager!!.adapter = pagerAdapter
                    }
                    bundle!!.putString("title", title)
                    bundle!!.putString("todayReviews", todayReviews)
                    bundle!!.putString("description", descriptionString)
                    bundle!!.putString("atmosphereNumber", atmosNum)
                    bundle!!.putString("musicNumber", musicNum)
                    bundle!!.putStringArrayList("imageList", imageList)
                }

            db!!.collection("Events").get().addOnSuccessListener { documents ->
                var index: Int = 0


                for (doc in documents) {
                    val barId = doc.get("barId").toString()
                    val isDeleted: Boolean = doc.get("isDeleted") as Boolean
                    if (activity != null && pref!!.getBarID() == barId) {
                        if (!isDeleted) {
                            eventText!!.visibility = View.VISIBLE
                            eventHolder!!.visibility = View.VISIBLE
                            index++
                            Log.d("Anas", "${doc.id} event ids")
                            addEventLayout(doc)
                            eventDocList.add(doc)
                        }
                    }

                }
                if (index == 0) {
                    eventText!!.visibility = View.GONE
                    eventHolder!!.visibility = View.GONE
                }
            }
            db!!.collection("Rating").get().addOnSuccessListener { documents ->
                for (doc in documents) {

                    val barId = doc.get("barId").toString()
                    if (activity != null && pref!!.getBarID() == barId) {

                        addBottomLayout(doc)
                        ratingDocList!!.add(doc)

                    }
                }

            }
        }
        else{
            Log.i("Anas","${eventDocList.size} docsize")
            if (eventDocList.size == 0) {
                eventText!!.visibility = View.GONE
                eventHolder!!.visibility = View.GONE
            }else{
                for (x in 0 until eventDocList!!.size){
                    eventText!!.visibility = View.VISIBLE
                    eventHolder!!.visibility = View.VISIBLE
                    addEventLayout(eventDocList[x])
                }
            }
            if(ratingDocList!!.size>0){
                for(x in 0 until ratingDocList!!.size){
                    addBottomLayout(ratingDocList[x])
                }
            }
            val title = bundle!!.get("title").toString()
            val todayReviews = bundle!!.get("todayReviews").toString()
            val descriptionString = bundle!!.get("description").toString()
            val atmosNum = bundle!!.get("atmosphereNumber").toString()
            val musicNum = bundle!!.get("musicNumber").toString()
            val imageList: ArrayList<String> =
                bundle!!.get("imageList") as ArrayList<String>

            barName!!.text = title
            ratedText!!.text = "$todayReviews RATED TONIGHT"
            description!!.text = descriptionString
            atmosphereNumber!!.text = atmosNum

            musicNumber!!.text = musicNum

            //imageTask = SomeTask(imageList[0])
            if (activity != null) {
                pagerAdapter = ImageSliderAdapter(
                    activity!!,
                    imageList,
                    pref!!.getBarID()!!,
                    "BarImages",
                    spin_kit!!
                )

                viewPager!!.adapter = pagerAdapter
            }
        }

    }

    private fun addEventLayout(document: QueryDocumentSnapshot) {

        val eventID = document.id
        val inflator = LayoutInflater.from(activity)
        val newView = inflator.inflate(R.layout.item_event, null)
        val cardView: CardView = newView.findViewById(R.id.cardView)
        val mainLayout: RelativeLayout = newView.findViewById(R.id.mainLayout)
        val headline: TextView = newView.findViewById(R.id.headline)
        val spinKitView: SpinKitView = newView.findViewById(R.id.spin_kit)

        headline.text = document.get("title").toString()
        val url: ArrayList<String> = document.get("imagesUrl") as ArrayList<String>
        checkImage(url, mainLayout, eventID, "EventImages", spinKitView)

        var params = LinearLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )

        params.setMargins(0, 0, 20, 0)


        cardView.layoutParams = params
        mainLayout.setOnClickListener {
            activity!!.startActivity(
                Intent(activity, BarEventInfoActivity::class.java)
                    .putExtra("eventId", document.get("eventId").toString())

            )

        }

        mainLayout.layoutParams.width = (width!! * 0.42).toInt()
        mainLayout.layoutParams.height = 400
        eventHolder!!.addView(newView)
        Log.d("Anas", "${eventHolder!!.childCount} child count")

    }

    private fun checkImage(
        list: ArrayList<String>,
        layout: RelativeLayout,
        id: String,
        imageFolder: String,
        spinKitView: SpinKitView
    ) {
        val myDir = File(activity!!.filesDir, "$imageFolder/$id")
        if (myDir.exists()) {
            var imageFile = File(myDir, "image0.png")
            if (imageFile.exists()) {
                var b: Bitmap = BitmapFactory.decodeStream(FileInputStream(imageFile))
                var dr: Drawable = BitmapDrawable(activity!!.resources, b)

                layout!!.background = dr
                spinKitView.visibility = View.GONE
                for (x in 0 until list.size) {
                    BackgroundDownloadTask(list[x], x, layout!!, myDir, spinKitView).execute()
                }
            } else {
                BackgroundDownloadTask(
                    list[0],
                    0,
                    layout,
                    myDir, spinKitView
                )
                    .execute()
            }

        } else {
            myDir.mkdirs()
            Log.d("Anas", "${myDir.exists()} boolean of folder")
            for (x in 0 until list.size) {
                BackgroundDownloadTask(
                    list[x],
                    x,
                    layout,
                    myDir, spinKitView
                )
                    .execute()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        check = false
        Log.d("Anas", "onPause")
        if (imageTask != null) {
            if (imageTask!!.status == AsyncTask.Status.RUNNING) {
                imageTask!!.cancel(true)
            }
        }
        if (userTask != null) {
            if (userTask!!.status == AsyncTask.Status.RUNNING) {
                userTask!!.cancel(true)
            }
        }
    }

    private fun addBottomLayout(document: QueryDocumentSnapshot) {
        val inflator = LayoutInflater.from(activity!!)
        val newView = inflator.inflate(R.layout.item_user_details_rate, null)
        val id = document.id

        var image: ImageView = newView.findViewById(R.id.image1)
        var userName: TextView = newView.findViewById(R.id.userName)
        var userReview: TextView = newView.findViewById(R.id.userReview)
        var atmosNumber: TextView = newView.findViewById(R.id.atmosNumber)
        var musicNumber: TextView = newView.findViewById(R.id.musicNumber)
        var date: TextView = newView.findViewById(R.id.date)
        var crowdText: TextView = newView.findViewById(R.id.crowdText)

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
        if(crowdDouble<2.5){
            crowdText!!.text="Less Crowded"
        }else if(crowdDouble>=2.5){
            crowdText!!.text="More Crowded"
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
            val scaledBitmap = bitmap?.let { Bitmap.createScaledBitmap(it, 100, 415, false) }
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
            if (activity != null) {
                var dr: Drawable = BitmapDrawable(activity!!.resources, result)

                layout!!.background = dr
            }

        }
    }

    private fun userImage(
        url: String,
        layout: ImageView,
        id: String
    ) {
        val myDir = File(activity!!.filesDir, "UserImage/$id")
        if (myDir.exists()) {
            var imageFile = File(myDir, "image0.png")
            if (imageFile.exists()) {
                var b: Bitmap = BitmapFactory.decodeStream(FileInputStream(imageFile))
                Glide.with(activity!!).asBitmap().load(b)
                    .apply(RequestOptions.circleCropTransform())
                    .into(layout)

                userTask = BackgroundUserTask(url, layout, myDir)
                userTask!!.execute()

            } else {
                userTask = BackgroundUserTask(url, layout, myDir)
                userTask!!.execute()
            }

        } else {
            myDir.mkdirs()

            userTask = BackgroundUserTask(url, layout, myDir)
            userTask!!.execute()

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
            if (activity != null) {
                Glide.with(activity!!).asBitmap().load(result)
                    .apply(RequestOptions.circleCropTransform())
                    .into(layout)
            }
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
            val scaledBitmap = bitmap?.let { Bitmap.createScaledBitmap(it, 100, 100, false) }
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
            if (activity != null) {
                var dr: Drawable = BitmapDrawable(activity!!.resources, result)
                spinKitView.visibility = View.GONE
                if (position == 0) {
                    layout!!.background = dr
                }
            }
        }
    }
}