package com.nightmap.ui.fragment.user

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.ui.activity.user.UserBarDetailsActivity
import com.nightmap.ui.activity.user.UserEventInfoActivity
import com.nightmap.ui.activity.user.UserSeeRatedActivity
import kotlinx.android.synthetic.main.item_explore.*
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import kotlin.math.roundToInt

class UserExploreFragment : Fragment(), View.OnClickListener {
    private var ratedLayout: LinearLayout? = null
    private var eventLayout: LinearLayout? = null
    private var barLayout: LinearLayout? = null

    private var see_all: TextView? = null

    private var atmosphereLayout: LinearLayout? = null
    private var musicLayout: LinearLayout? = null
    private var genderLayout: LinearLayout? = null
    private var crowdLayout: LinearLayout? = null

    private var atmosCircleLayout: RelativeLayout? = null
    private var musicCircleLayout: RelativeLayout? = null
    private var genderCircleLayout: RelativeLayout? = null
    private var crowdCircleLayout: RelativeLayout? = null

    private var atmosNumber: TextView? = null
    private var musicNumber: TextView? = null
    private var genderImage: ImageView? = null
    private var crowdImage: ImageView? = null

    private var atmosText: TextView? = null
    private var musicText: TextView? = null
    private var genderText: TextView? = null
    private var crowdText: TextView? = null

    private var card: CardView? = null
    private var headline: TextView? = null
    private var ratedcheck: Boolean? = true
    private var eventcheck: Boolean? = true
    private var barcheck: Boolean? = true
    private var db: FirebaseFirestore? = null
    private var rateTask: BackgroundTask? = null
    private var eventTask: BackgroundTask? = null
    private var barTask: BackgroundTask? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_user_explore, container, false)
        init(view)
        return view
    }

    override fun onPause() {
        super.onPause()
        if (rateTask != null) {
            if (rateTask!!.status == AsyncTask.Status.RUNNING) {
                rateTask!!.cancel(true)
            }
        }

    }

    private fun init(view: View?) {
        db = Firebase.firestore
        val displayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        var density = displayMetrics.densityDpi

        ratedLayout = view!!.findViewById(R.id.ratedLayout)
        eventLayout = view!!.findViewById(R.id.eventLayout)
        barLayout = view!!.findViewById(R.id.barLayout)
        see_all = view.findViewById(R.id.see_all)


        db!!.collection("Bars").get().addOnSuccessListener { documents ->
            var index: Int = 0
            for (document in documents) {
                Log.e("Anas", "${document.id} document")
                index++
                if (activity != null) {

                    ratedTonightScreenSize(width, height, density, document)
                    barScreenSize(width, height, document)
                    if (index == 2) {
                        break
                    }

                }
            }
        }

        db!!.collection("Events").get().addOnSuccessListener { documents ->
            var index: Int = 0
            for (doc in documents) {
                val isDeleted = doc.get("isDeleted") as Boolean
                index++
                if (activity != null) {
                    if (!isDeleted) {

                        eventScreenSize(width, height, doc)
                        if (index == 2) {
                            break
                        }

                    }

                }
            }
        }
        see_all!!.setOnClickListener(this)
    }

    private fun barScreenSize(width: Int, height: Int, doc: QueryDocumentSnapshot) {
        val inflator = LayoutInflater.from(activity)
        var newView = inflator!!.inflate(R.layout.item_explore, null)
        var cardLayout: CardView = newView.findViewById(R.id.cardLayout)
        var mainLayout: RelativeLayout = newView.findViewById(R.id.mainLayout)
        var headline: TextView = newView.findViewById(R.id.headline)
        headline.text = doc.get("title").toString()

        barLayout!!.weightSum = 1f
        var params = LinearLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT, 0.5f
        )
        barcheck = if (barcheck!!) {
            params.setMargins(0, 0, 10, 0)
            false
        } else {
            params.setMargins(10, 0, 0, 0)
            true
        }
        cardLayout!!.layoutParams = params

        var imageUrl: ArrayList<String> = doc.get("imagesURL") as ArrayList<String>
        barTask = BackgroundTask(imageUrl[0], mainLayout)
        barTask!!.execute()

        cardLayout.setOnClickListener {
            val id = doc.get("barId").toString()
            activity!!.startActivity(
                Intent(
                    activity!!,
                    UserBarDetailsActivity::class.java
                ).putExtra("id", id)
            )
        }
        barLayout!!.addView(newView)


    }

    private fun eventScreenSize(
        width: Int,
        height: Int,
        doc: QueryDocumentSnapshot
    ) {
        val inflator = LayoutInflater.from(activity)
        var newView = inflator!!.inflate(R.layout.item_explore, null)
        var cardLayout: CardView = newView.findViewById(R.id.cardLayout)
        var mainLayout: RelativeLayout = newView.findViewById(R.id.mainLayout)
        var headline: TextView = newView.findViewById(R.id.headline)
        headline.text = doc.get("title").toString()

        eventLayout!!.weightSum = 1f
        var params = LinearLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT, 0.5f
        )
        if (eventcheck!!) {
            params.setMargins(0, 0, 10, 0)
            eventcheck = false
        } else {
            params.setMargins(10, 0, 0, 0)
            eventcheck = true
        }
        cardLayout!!.layoutParams = params

        var imageUrl: ArrayList<String> = doc.get("imageUrl") as ArrayList<String>
        eventTask = BackgroundTask(imageUrl[0], mainLayout)
        eventTask!!.execute()

        cardLayout.setOnClickListener {
            activity!!.startActivity(
                Intent(activity, UserEventInfoActivity::class.java)
                    .putExtra("eventId", doc.get("eventId").toString()).putExtra("url", imageUrl[0])
            )
        }
        eventLayout!!.addView(newView)


    }

    private fun ratedTonightScreenSize(
        width: Int,
        height: Int,
        density: Int,
        document: QueryDocumentSnapshot
    ) {

        val inflator = LayoutInflater.from(activity)
        val newView = inflator!!.inflate(R.layout.item_see_rated, null)

        atmosphereLayout = newView!!.findViewById(R.id.atmosphereLayout)
        musicLayout = newView!!.findViewById(R.id.musicLayout)
        genderLayout = newView!!.findViewById(R.id.genderLayout)
        crowdLayout = newView!!.findViewById(R.id.crowdLayout)

        atmosCircleLayout = newView!!.findViewById(R.id.atmosCircleLay)
        musicCircleLayout = newView!!.findViewById(R.id.musicCircleLay)
        genderCircleLayout = newView!!.findViewById(R.id.genderCircleLay)
        crowdCircleLayout = newView!!.findViewById(R.id.crowdCircleLay)

        atmosNumber = newView!!.findViewById(R.id.atmosNumber)
        musicNumber = newView!!.findViewById(R.id.musicNumber)
        genderImage = newView!!.findViewById(R.id.genderImage)
        crowdImage = newView!!.findViewById(R.id.crowdImage)

        atmosText = newView!!.findViewById(R.id.atmosText)
        musicText = newView!!.findViewById(R.id.musicText)
        genderText = newView!!.findViewById(R.id.genderText)
        crowdText = newView!!.findViewById(R.id.crowdText)

        card = newView!!.findViewById(R.id.mainLayout)
        val layout: RelativeLayout = newView!!.findViewById(R.id.layout)
        headline = newView.findViewById(R.id.headline)

        crowdCircleLayout!!.layoutParams.width = (width * 0.08).roundToInt()
        crowdCircleLayout!!.layoutParams.height = (width * 0.08).roundToInt()
        crowdImage!!.layoutParams.width = (width * 0.5).roundToInt()
        crowdImage!!.layoutParams.height = (width * 0.5).roundToInt()

        atmosCircleLayout!!.layoutParams.width = (width * 0.08).roundToInt()
        atmosCircleLayout!!.layoutParams.height = (width * 0.08).roundToInt()

        musicCircleLayout!!.layoutParams.width = (width * 0.08).roundToInt()
        musicCircleLayout!!.layoutParams.height = (width * 0.08).roundToInt()

        genderCircleLayout!!.layoutParams.width = (width * 0.08).roundToInt()
        genderCircleLayout!!.layoutParams.height = (width * 0.08).roundToInt()
        genderImage!!.layoutParams.width = (width * 0.5).roundToInt()
        genderImage!!.layoutParams.height = (width * 0.5).roundToInt()


        if (density == 320 || density < 420) {
            if (isTablet(activity!!)) {
                crowdText!!.textSize = (width * 0.007).toFloat()
                atmosText!!.textSize = (width * 0.007).toFloat()
                atmosNumber!!.textSize = ((width * 0.012).toFloat())
                musicNumber!!.textSize = ((width * 0.012).toFloat())
                musicText!!.textSize = ((width * 0.007).toFloat())
                genderText!!.textSize = (width * 0.007).toFloat()
            } else {
                crowdText!!.textSize = (width * 0.007).toFloat()
                atmosText!!.textSize = (width * 0.007).toFloat()
                atmosNumber!!.textSize = ((width * 0.015).toFloat())
                musicNumber!!.textSize = ((width * 0.015).toFloat())
                musicText!!.textSize = (width * 0.007).toFloat()
                genderText!!.textSize = (width * 0.007).toFloat()
            }
        } else if (density == 420 || density < 560) {
            if (isTablet(activity!!)) {
                crowdText!!.textSize = (width * 0.005).toFloat()
                atmosText!!.textSize = (width * 0.005).toFloat()
                atmosNumber!!.textSize = ((width * 0.01).toFloat())
                musicNumber!!.textSize = ((width * 0.01).toFloat())
                musicText!!.textSize = ((width * 0.005).toFloat())
                genderText!!.textSize = (width * 0.005).toFloat()
            } else {
                crowdText!!.textSize = (width * 0.004).toFloat()
                atmosText!!.textSize = (width * 0.004).toFloat()
                atmosNumber!!.textSize = ((width * 0.01).toFloat())
                musicNumber!!.textSize = ((width * 0.01).toFloat())
                musicText!!.textSize = (width * 0.004).toFloat()
                genderText!!.textSize = (width * 0.004).toFloat()
            }
        } else if (density == 560 || density < 640) {
            if (isTablet(activity!!)) {
                crowdText!!.textSize = (width * 0.003).toFloat()
                atmosText!!.textSize = (width * 0.003).toFloat()
                atmosNumber!!.textSize = ((width * 0.007).toFloat())
                musicNumber!!.textSize = ((width * 0.007).toFloat())
                musicText!!.textSize = ((width * 0.003).toFloat())
                genderText!!.textSize = (width * 0.003).toFloat()
            } else {
                crowdText!!.textSize = (width * 0.004).toFloat()
                atmosText!!.textSize = (width * 0.004).toFloat()
                atmosNumber!!.textSize = ((width * 0.007).toFloat())
                musicNumber!!.textSize = ((width * 0.007).toFloat())
                musicText!!.textSize = (width * 0.004).toFloat()
                genderText!!.textSize = (width * 0.004).toFloat()
            }
        } else if (density == 640) {
            if (isTablet(activity!!)) {

                crowdText!!.textSize = (width * 0.001).toFloat()
                atmosText!!.textSize = (width * 0.001).toFloat()
                atmosNumber!!.textSize = ((width * 0.0007).toFloat())
                musicNumber!!.textSize = ((width * 0.0007).toFloat())
                musicText!!.textSize = ((width * 0.001).toFloat())
                genderText!!.textSize = (width * 0.001).toFloat()
            } else {
                crowdText!!.textSize = (width * 0.0035).toFloat()
                atmosText!!.textSize = (width * 0.0035).toFloat()
                atmosNumber!!.textSize = ((width * 0.007).toFloat())
                musicNumber!!.textSize = ((width * 0.007).toFloat())
                musicText!!.textSize = (width * 0.0035).toFloat()
                genderText!!.textSize = (width * 0.0035).toFloat()
            }
        }

        ratedLayout!!.weightSum=1f
        var params = LinearLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.MATCH_PARENT,0.5f
        )
        if (ratedcheck!!) {
            params.setMargins(0, 0, 10, 0)
            ratedcheck = false
        } else {
            params.setMargins(10, 0, 0, 0)
            ratedcheck = true
        }
        card!!.layoutParams = params
        //card!!.layoutParams.width = (width * 0.4).roundToInt()
        newView!!.setOnClickListener {
            val id = document.get("barId").toString()
            activity!!.startActivity(
                Intent(
                    activity!!,
                    UserBarDetailsActivity::class.java
                ).putExtra("id", id)
            )
        }

        atmosNumber!!.text = DecimalFormat("#.#").format(document.get("averageAtmosphere"))
        musicNumber!!.text = DecimalFormat("#.#").format(document.get("averageMusic"))
        headline!!.text = document.get("title").toString()

        var imageUrl: ArrayList<String> = document.get("imagesURL") as ArrayList<String>
        rateTask = BackgroundTask(imageUrl[0], layout)
        rateTask!!.execute()
        ratedLayout!!.addView(newView)

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

    fun isTablet(context: Context): Boolean {
        return context.resources.configuration.screenLayout and
                Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.see_all -> {
                activity!!.startActivity(Intent(activity!!, UserSeeRatedActivity::class.java))
            }
        }
    }

    inner class BackgroundTask(val url: String, val layout: RelativeLayout) :
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

}