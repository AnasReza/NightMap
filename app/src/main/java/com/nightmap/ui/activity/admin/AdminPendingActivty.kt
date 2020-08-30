package com.nightmap.ui.activity.admin

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.adapter.ImageSliderAdapter
import com.nightmap.utility.Preferences
import kotlinx.android.synthetic.main.fragment_bar_home_details.*
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class AdminPendingActivty : AppCompatActivity(), View.OnClickListener {
    private var bar_name: TextView? = null
    private var description: TextView? = null
    private var spinKitView:SpinKitView?=null
    private var location: TextView? = null
    private var discount: TextView? = null
    private var free_drink: TextView? = null
    private var viewPager: ViewPager2? = null
    private var imageLayout: RelativeLayout? = null
    private var accept_layout: LinearLayout? = null
    private var reject_layout: LinearLayout? = null
    private var back_arrow: ImageView? = null
    private var db: FirebaseFirestore? = null
    private var pref: Preferences? = null
    private var barId: String = ""
    private var task: SomeTask? = null
    private var pagerAdapter: ImageSliderAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_pending)
        init()
    }

    private fun init() {
        db = Firebase.firestore
        pref = Preferences(this)
        barId = intent.getStringExtra("id")

        bar_name = findViewById(R.id.bar_name)
        description = findViewById(R.id.description)
        location = findViewById(R.id.location)
        discount = findViewById(R.id.discount)
        free_drink = findViewById(R.id.free_drink)
        imageLayout = findViewById(R.id.imageLayout)
        accept_layout = findViewById(R.id.accept_layout)
        reject_layout = findViewById(R.id.reject_layout)
        back_arrow = findViewById(R.id.back_arrow)
        viewPager = findViewById(R.id.pager)
        spinKitView=findViewById(R.id.spin_kit)

        db!!.collection("Bars").document(barId).get().addOnSuccessListener { documentSnapshot ->
            val urls: ArrayList<String> = documentSnapshot.get("imagesURL") as ArrayList<String>
            bar_name!!.text = documentSnapshot.get("title").toString()
            description!!.text = documentSnapshot.get("description").toString()
            location!!.text = documentSnapshot.get("barLocation").toString()
            discount!!.text = "${documentSnapshot.get("discount").toString()}%"
            free_drink!!.text = documentSnapshot.get("freeDrinkAfter").toString()

            task = SomeTask(urls[0])
            //task!!.execute()
            pagerAdapter = ImageSliderAdapter(this, urls, barId, "BarImages",spinKitView!!)
            pager!!.adapter = pagerAdapter
            accept_layout!!.setOnClickListener(this)
            reject_layout!!.setOnClickListener(this)
        }

        back_arrow!!.setOnClickListener(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (task == null) {
            if (task!!.status == AsyncTask.Status.RUNNING) {
                task!!.cancel(true)
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
            var dr: Drawable = BitmapDrawable(this@AdminPendingActivty!!.resources, result)

            imageLayout!!.background = dr
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.accept_layout -> {
                val hashMap = hashMapOf("status" to "approved",
                    "addedBy" to pref!!.getAdminID())
                db!!.collection("Bars").document(barId).update(hashMap as Map<String, Any>)
                    .addOnSuccessListener {
//                        val notiHash =
//                            hashMapOf("addedOn" to FieldValue.serverTimestamp(),
//                                "barId" to barId,
//                                "body" to "Congratulations! your bar is approved now you can manage it now.",
//                                "title" to "Bar Approved",
//                                "type" to "Approved",
//                                "uid" to "")
//                        db!!.collection("Notifications").add(notiHash).addOnSuccessListener {
//
//                        }
                        Toast.makeText(
                            this,
                            "${bar_name!!.text.toString()} Has Been Approved",
                            Toast.LENGTH_SHORT
                        ).show()
                        onBackPressed()
                    }
            }
            R.id.reject_layout -> {
                val hashMap = hashMapOf("status" to "rejected",
                    "addedBy" to pref!!.getAdminID())
                db!!.collection("Bars").document(barId).update(hashMap as Map<String, Any>)
                    .addOnSuccessListener {
                        val notiHash =
                            hashMapOf("addedOn" to FieldValue.serverTimestamp(),
                                "barId" to barId,
                                "body" to "Congratulations! your bar is approved now you can manage it now.",
                                "title" to "Bar Rejected",
                                "type" to "Rejected",
                                "uid" to "")
                        db!!.collection("Notifications").add(notiHash).addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "${bar_name!!.text.toString()} Has Been Rejected",
                                Toast.LENGTH_SHORT
                            ).show()
                            onBackPressed()
                        }

                    }
            }
            R.id.back_arrow -> {
                onBackPressed()
            }
        }
    }
}