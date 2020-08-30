package com.nightmap.ui.activity.user


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.viewpager2.widget.ViewPager2
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.iosParameters
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.adapter.ImageSliderAdapter
import com.nightmap.ui.activity.LocationMapActivity
import com.nightmap.ui.activity.MainActivity
import com.nightmap.utility.Preferences
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class UserEventInfoActivity : AppCompatActivity(), View.OnClickListener {
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
    private var bottomLayout: RelativeLayout? = null
    private var viewPager: ViewPager2? = null
    private var back_button: ImageView? = null
    private var ageCard: CardView? = null
    private var spinKitView: SpinKitView? = null

    private var eventId: String = ""
    private var barName: String = ""

    private var userType: String = ""
    private var db: FirebaseFirestore? = null
    private var task: SomeTask? = null
    private var pref: Preferences? = null
    private var check: Boolean = true
    private var pagerAdapter: ImageSliderAdapter? = null
    private var goingList: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_event_info)
        init()
    }

    private fun init() {
        db = Firebase.firestore
        pref = Preferences(this)
        goingList = ArrayList()
        userType = pref!!.getUserType().toString()

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
        bottomLayout = findViewById(R.id.bottomLayout)
        viewPager = findViewById(R.id.pager)
        back_button = findViewById(R.id.back_button)
        ageCard = findViewById(R.id.ageCard)
        spinKitView = findViewById(R.id.spin_kit)

        if (userType == "admin") {
            bottomLayout!!.visibility = View.GONE
        }

        Firebase.dynamicLinks.getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link

                    Log.d("Anas", "$deepLink from mainActivity")
                    val userType = pref!!.getUserType()
                    var linkString = deepLink.toString()
                    if (userType == "admin" || userType == "bar") {
                        startActivity(Intent(this@UserEventInfoActivity, MainActivity::class.java))
                        finishAffinity()
                    }
                    else {
                        if(pref!!.getLogin()!!){
                            val linkList = linkString.split("/")

                            eventId = linkList[linkList.size - 1]
                            Log.d("Anas", "$eventId event id on link press")
                            getData()
                            goingSwitch!!.setOnCheckedChangeListener { _, isChecked ->
                                if (isChecked) {
                                    if (check) {
                                        db!!.collection("Events").document(eventId).get()
                                            .addOnSuccessListener { documentSnapshot ->
                                                check = false
                                                val goingList: ArrayList<String> =
                                                    documentSnapshot.get("usersGoing") as ArrayList<String>
                                                goingList.add(pref!!.getUserID()!!)
                                                val hashMap = hashMapOf("usersGoing" to goingList)
                                                db!!.collection("Events").document(eventId)
                                                    .update(hashMap as Map<String, Any>)

                                            }
                                        peopleGoingText!!.text =
                                            "${goingList!!.size + 1} People are going"
                                    }

                                } else {
                                    db!!.collection("Events").document(eventId).get()
                                        .addOnSuccessListener { documentSnapshot ->
                                            val goingList: ArrayList<String> =
                                                documentSnapshot.get("usersGoing") as ArrayList<String>
                                            for (x in 0 until goingList.size) {
                                                if (pref!!.getUserID() == goingList[x]) {
                                                    goingList.removeAt(x)
                                                    break
                                                }
                                            }

                                            val hashMap = hashMapOf("usersGoing" to goingList)
                                            db!!.collection("Events").document(eventId)
                                                .update(hashMap as Map<String, Any>)
                                            check = true
                                        }
                                    peopleGoingText!!.text = "${goingList!!.size} People are going"
                                }
                            }
                        }else{
                            startActivity(Intent(this@UserEventInfoActivity, MainActivity::class.java))
                            finishAffinity()
                        }

                    }
                }
                else {
                    eventId = intent.getStringExtra("eventId")
                    getData()
                    Log.d("Anas", "URI is empty")
                    goingSwitch!!.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            if (check) {
                                db!!.collection("Events").document(eventId).get()
                                    .addOnSuccessListener { documentSnapshot ->
                                        check = false
                                        val goingList: ArrayList<String> =
                                            documentSnapshot.get("usersGoing") as ArrayList<String>
                                        goingList.add(pref!!.getUserID()!!)
                                        val hashMap = hashMapOf("usersGoing" to goingList)
                                        db!!.collection("Events").document(eventId)
                                            .update(hashMap as Map<String, Any>)

                                    }
                                peopleGoingText!!.text = "${goingList!!.size + 1} People are going"
                            }

                        } else {
                            db!!.collection("Events").document(eventId).get()
                                .addOnSuccessListener { documentSnapshot ->
                                    val goingList: ArrayList<String> =
                                        documentSnapshot.get("usersGoing") as ArrayList<String>
                                    for (x in 0 until goingList.size) {
                                        if (pref!!.getUserID() == goingList[x]) {
                                            goingList.removeAt(x)
                                            break
                                        }
                                    }

                                    val hashMap = hashMapOf("usersGoing" to goingList)
                                    db!!.collection("Events").document(eventId)
                                        .update(hashMap as Map<String, Any>)
                                    check = true
                                }
                            peopleGoingText!!.text = "${goingList!!.size} People are going"
                        }
                    }
                }
            }

        view_location_layout!!.setOnClickListener(this)
        back_button!!.setOnClickListener(this)
        share_event_layout!!.setOnClickListener(this)

    }

    private fun getData() {
        db!!.collection("Events").document(eventId)
            .get().addOnSuccessListener { documentSnapshot ->
                val barId = documentSnapshot.get("barId").toString()
                db!!.collection("Bars").document(barId).get()
                    .addOnSuccessListener { documentSnapshot ->

                        barName = documentSnapshot.get("title").toString()
                    }

                val ageLimit: Boolean = documentSnapshot.get("ageLimit") as Boolean
                val t = documentSnapshot.getTimestamp("eventTime")
                val url: ArrayList<String> = documentSnapshot.get("imagesUrl") as ArrayList<String>
                goingList =
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

                for (x in 0 until goingList!!.size) {
                    if (pref!!.getUserID() == goingList!![x]) {
                        check = false
                        goingSwitch!!.isChecked = true
                    }
                }
                desciption!!.text = documentSnapshot.get("description").toString()
                event_name!!.text = documentSnapshot.get("title").toString()

                dateText!!.text = "$day/$month/$year"
                timeText!!.text = str
                if (ageLimit) {
                    ageCard!!.visibility = View.VISIBLE
                    agelimitText!!.text =
                        "${documentSnapshot.get("minAge")} to ${documentSnapshot.get("maxAge")} age"
                    peopleGoingText!!.text = "${goingList!!.size} People are going"
                }

                //task = SomeTask(imageURL)
                //task!!.execute()
                pagerAdapter = ImageSliderAdapter(this, url, eventId, "EventImages", spinKitView!!)
                viewPager!!.adapter = pagerAdapter

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
            var dr: Drawable = BitmapDrawable(this@UserEventInfoActivity.resources, result)

            upperLayout!!.background = dr
        }
    }

    private fun getDeepLink() {
        val myDir = File(filesDir, "EventImages/$eventId")
        val imageFile = File(myDir, "image0.png")
        var b: Bitmap = BitmapFactory.decodeStream(FileInputStream(imageFile))
        val path=MediaStore.Images.Media.insertImage(this@UserEventInfoActivity.contentResolver,b,"",null)
        val imageUri=Uri.parse(path)
        Firebase.dynamicLinks.shortLinkAsync {
            link = Uri.parse("https://nightmap.com/$eventId")
            domainUriPrefix = "https://nightmap.page.link/"
            androidParameters("com.Elroye.NightMap") {
                minimumVersion = 1
                fallbackUrl =
                    Uri.parse("https://play.google.com/store/apps/details?id=com.Elroye.NightMap")
            }
            iosParameters("com.Elroye.NightMap") {
                appStoreId = "1476740141"
                minimumVersion = "1.0.1"
            }

        }.addOnSuccessListener { result ->
            val shortLink = result.shortLink
            pref!!.setDeepLink("$shortLink")
            Log.d("Anas", "$shortLink")
            try {

                var shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
               // shareIntent.type = "image/*"

                shareIntent.putExtra(
                    Intent.EXTRA_SUBJECT,
                    "${event_name!!.text} is happening, would you like to come?"
                )
                var message =
                    "\nWould you like to join in the fun without having the worry to miss out on something you would love?\n\n"
                message =
                    "$message${pref!!.getDeepLink()}"
                shareIntent.putExtra(Intent.EXTRA_TEXT, message)
                shareIntent.putExtra(Intent.EXTRA_STREAM,imageUri)
                shareIntent.type="image/*"

                startActivity(Intent.createChooser(shareIntent, "Choose Any"))

            } catch (e: Exception) {
                e.toString()
            }
        }

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.view_location_layout -> {
                startActivity(
                    Intent(this, LocationMapActivity::class.java)
                        .putExtra("id", eventId)
                        .putExtra("dbType", "Events")
                        .putExtra("columnName", "imagesUrl")
                        .putExtra("heading", "Event Location")
                        .putExtra("location", "location")
                )
            }
            R.id.back_button -> {
                onBackPressed()
            }
            R.id.share_event_layout -> {
                getDeepLink()

            }
        }
    }

}