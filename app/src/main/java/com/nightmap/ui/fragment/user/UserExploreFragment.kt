package com.nightmap.ui.fragment.user

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.adapter.SeeRatedAdapter
import com.nightmap.ui.activity.SearchBarActivity
import com.nightmap.ui.activity.user.UserBarDetailsActivity
import com.nightmap.ui.activity.user.UserEventInfoActivity
import com.nightmap.ui.activity.user.UserSeeRatedActivity
import com.nightmap.utility.Preferences
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import kotlin.math.roundToInt

class UserExploreFragment : Fragment(), View.OnClickListener {
    private var ratedLayout: LinearLayout? = null
    private var eventLayout: LinearLayout? = null
    private var barLayout: RecyclerView? = null
    private var search_bar: EditText? = null

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
    private var adapter: SeeRatedAdapter? = null
    private var mLayout: GridLayoutManager? = null
    private var documentList: ArrayList<QueryDocumentSnapshot> = ArrayList()
    private var headline: TextView? = null
    private var ratedcheck: Boolean? = true
    private var eventcheck: Boolean? = true
    private var barcheck: Boolean? = true
    private var db: FirebaseFirestore? = null
    private var pref: Preferences? = null
    private var rateTask: BackgroundTask? = null
    private var eventTask: BackgroundTask? = null
    private var barTask: BackgroundTask? = null
    private var check:Boolean=true
    private var ratingList:ArrayList<QueryDocumentSnapshot> = ArrayList()
    private var eventList:ArrayList<QueryDocumentSnapshot> = ArrayList()
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
        check=false
        if (rateTask != null) {
            if (rateTask!!.status == AsyncTask.Status.RUNNING) {
                rateTask!!.cancel(true)
            }
        }
        if (eventTask != null) {
            if (eventTask!!.status == AsyncTask.Status.RUNNING) {
                eventTask!!.cancel(true)
            }
        }
        if (barTask != null) {
            if (barTask!!.status == AsyncTask.Status.RUNNING) {
                barTask!!.cancel(true)
            }
        }

    }

    var width: Int = 0
    var height: Int = 0
    var density: Int = 0
    private fun init(view: View?) {
        db = Firebase.firestore
        pref = Preferences(activity!!)
        mLayout = GridLayoutManager(activity!!, 2)
        val displayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        height = displayMetrics.heightPixels
        width = displayMetrics.widthPixels
        density = displayMetrics.densityDpi

        ratedLayout = view!!.findViewById(R.id.ratedLayout)
        eventLayout = view!!.findViewById(R.id.eventLayout)
        barLayout = view!!.findViewById(R.id.barLayout)
        see_all = view.findViewById(R.id.see_all)
        search_bar = view.findViewById(R.id.search_bar)

        if(check){
            db!!.collection("Bars").orderBy("totalReviews", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    var index = 0
                    for (document in documents) {
                        val status = document.get("status").toString()
                        if (activity != null && status == "approved") {
                            index++
                            ratedTonightScreenSize(width, height, density, document, index)
                            ratingList!!.add(document)
                            if (index > 4) {
                                break
                            }
                        }
                    }
                }
            db!!.collection("Bars").limit(4).get().addOnSuccessListener { documents ->
                var index = 0
                for (document in documents) {
                    val status = document.get("status").toString()
                    if (status == "approved") {
                        documentList!!.add(document)
                    }

                }
                if (activity != null) {
                    index++
                    adapter = SeeRatedAdapter(activity!!, documentList, false)
                    barLayout!!.layoutManager = mLayout
                    barLayout!!.adapter = adapter
                    // barScreenSize(width, height, document, index)
                }
            }

            db!!.collection("Events").limit(2).get().addOnSuccessListener { documents ->

                for (doc in documents) {
                    val isDeleted = doc.get("isDeleted") as Boolean

                    if (activity != null) {
                        if (!isDeleted) {

                            eventScreenSize(width, height, doc)
                            eventList!!.add(doc)

                        }

                    }
                }
            }
        }
        else{
            for(x in 0 until ratingList!!.size){
                ratedTonightScreenSize(width, height, density, ratingList[x], x)
            }

            for(x in 0 until eventList!!.size){
                eventScreenSize(width, height, eventList[x])
            }

            adapter=null
            adapter = SeeRatedAdapter(activity!!, documentList, false)
            barLayout!!.layoutManager = mLayout
            barLayout!!.adapter = adapter
        }

        see_all!!.setOnClickListener(this)
        search_bar!!.setOnClickListener(this)
    }

    private fun eventScreenSize(
        width: Int,
        height: Int,
        doc: QueryDocumentSnapshot
    ) {
        val id = doc.id
        val inflator = LayoutInflater.from(activity)
        var newView = inflator!!.inflate(R.layout.item_explore, null)
        var cardLayout: CardView = newView.findViewById(R.id.cardLayout)
        var mainLayout: RelativeLayout = newView.findViewById(R.id.mainLayout)
        var spin_kit: SpinKitView = newView.findViewById(R.id.spin_kit)
        var headline: TextView = newView.findViewById(R.id.headline)
        headline.text = doc.get("title").toString()
        spin_kit.visibility = View.VISIBLE

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

        var imageUrl: ArrayList<String> = doc.get("imagesUrl") as ArrayList<String>
        eventTask = BackgroundTask(imageUrl[0], mainLayout)
        //eventTask!!.execute()

        cardLayout.setOnClickListener {
            activity!!.startActivity(
                Intent(activity, UserEventInfoActivity::class.java)
                    .putExtra("eventId", doc.get("eventId").toString())
                    .putExtra("url", imageUrl[0])
                    .putExtra("userType", pref!!.getUserType())
            )
        }
        checkImage(imageUrl, cardLayout, id, "EventImages", spin_kit)

        eventLayout!!.addView(newView)


    }

    private fun ratedTonightScreenSize(
        width: Int,
        height: Int,
        density: Int,
        document: QueryDocumentSnapshot,
        index: Int
    ) {

        val inflator = LayoutInflater.from(activity)
        val newView = inflator!!.inflate(R.layout.item_see_rated, null)
        val id = document.id

        val bottomLayout: LinearLayout = newView!!.findViewById(R.id.bottomLayout)
        bottomLayout.visibility = View.VISIBLE
        val spin_kit: SpinKitView = newView!!.findViewById(R.id.spin_kit)
        spin_kit.visibility = View.VISIBLE
        atmosphereLayout = newView.findViewById(R.id.atmosphereLayout)
        musicLayout = newView.findViewById(R.id.musicLayout)
        genderLayout = newView.findViewById(R.id.genderLayout)
        crowdLayout = newView.findViewById(R.id.crowdLayout)

        atmosCircleLayout = newView.findViewById(R.id.atmosCircleLay)
        musicCircleLayout = newView.findViewById(R.id.musicCircleLay)
        genderCircleLayout = newView.findViewById(R.id.genderCircleLay)
        crowdCircleLayout = newView.findViewById(R.id.crowdCircleLay)

        atmosNumber = newView.findViewById(R.id.atmosNumber)
        musicNumber = newView.findViewById(R.id.musicNumber)
        genderImage = newView.findViewById(R.id.genderImage)
        crowdImage = newView.findViewById(R.id.crowdImage)

        atmosText = newView.findViewById(R.id.atmosText)
        musicText = newView.findViewById(R.id.musicText)
        genderText = newView.findViewById(R.id.genderText)
        crowdText = newView.findViewById(R.id.crowdText)

        card = newView.findViewById(R.id.mainLayout)
        val layout: RelativeLayout = newView.findViewById(R.id.layout)
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
        genderImage!!.layoutParams.width = (width * 1.2).roundToInt()
        genderImage!!.layoutParams.height = (width * 1.2).roundToInt()


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
        }
        else if (density == 420 || density < 560) {
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
        }
        else if (density == 560 || density < 640) {
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
        }
        else if (density == 640) {
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


        var params = LinearLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        if (index < 3) {
            params.setMargins(0, 0, 20, 0)

        } else if (index == 3) {
            params.setMargins(0, 0, 10, 0)
        } else {
            params.setMargins(10, 0, 0, 0)

        }
        card!!.layoutParams = params
        card!!.layoutParams.width = (width * 0.46).roundToInt()
        newView!!.setOnClickListener {
            val id = document.get("barId").toString()
            activity!!.startActivity(
                Intent(
                    activity!!,
                    UserBarDetailsActivity::class.java
                ).putExtra("id", id).putExtra("userType", "user")
            )
        }

        atmosNumber!!.text = DecimalFormat("#.#").format(document.get("averageAtmosphere"))
        musicNumber!!.text = DecimalFormat("#.#").format(document.get("averageMusic"))
        headline!!.text = document.get("title").toString()
val crowdDouble:Double= document.get("averageCrowded") as Double
        if(crowdDouble<2.5){
            crowdText!!.text="Less Crowded"
        }else if(crowdDouble>=2.5){
            crowdText!!.text="More Crowded"
        }
        var imageUrl: ArrayList<String> = document.get("imagesURL") as ArrayList<String>
        rateTask = BackgroundTask(imageUrl[0], layout)

        checkImage(imageUrl, card, id, "BarImages", spin_kit)
        //rateTask!!.execute()
        ratedLayout!!.addView(newView)

    }

    private fun checkImage(
        list: ArrayList<String>,
        layout: CardView?,
        id: String,
        imageFolder: String,
        spin_kit: SpinKitView
    ) {
        val myDir = File(activity!!.filesDir, "$imageFolder/$id")
        if (myDir.exists()) {
            var imageFile = File(myDir, "image0.png")
            if (imageFile.exists()) {
                var b: Bitmap = BitmapFactory.decodeStream(FileInputStream(imageFile))
                var dr: Drawable = BitmapDrawable(activity!!.resources, b)
                layout!!.background = dr
                spin_kit.visibility = View.GONE
                for (x in 0 until list.size) {
                    BackgroundDownloadTask(list[x], x, layout!!, myDir, spin_kit).execute()
                }
            } else {
                BackgroundDownloadTask(
                    list[0],
                    0,
                    layout!!,
                    myDir,
                    spin_kit
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
                    layout!!,
                    myDir, spin_kit
                )
                    .execute()
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

    fun isTablet(context: Context): Boolean {
        return context.resources.configuration.screenLayout and
                Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.see_all -> {
                activity!!.startActivity(Intent(activity!!, UserSeeRatedActivity::class.java))
            }
            R.id.search_bar -> {
                activity!!.startActivity(Intent(activity!!, SearchBarActivity::class.java))
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

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    inner class BackgroundDownloadTask(
        val url: String,
        val position: Int,
        val layout: CardView,
        val mainFile: File, val spin_kit: SpinKitView
    ) :
        AsyncTask<String, URL, Bitmap>() {

        override fun doInBackground(vararg params: String?): Bitmap {
            var scaledBitmap: Bitmap? = null


            if (activity != null && isOnline(activity!!)) {
                var length = 0
                val bitmap = getBitmapFromURL(url)
                scaledBitmap = bitmap?.let { Bitmap.createScaledBitmap(it, 739, 415, false) }
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
            } else {

                    var paint = Paint()
                    paint.color = Color.WHITE
                    paint.textSize = 10F
                    val conf = Bitmap.Config.ARGB_8888 // see other conf types

                    scaledBitmap = Bitmap.createBitmap(200, 200, conf) // this creates a MUTABLE bitmap

                    val canvas = Canvas(scaledBitmap)
                    //canvas.drawColor(ContextCompat.getColor(activity!!, R.color.background_color))
                    canvas.drawText("No Internet Connection", 0F, 0F, paint)

            }

            return scaledBitmap!!
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            Log.e("Anas", "Downloading and saving file is complete")
            if (activity != null && result != null) {
                spin_kit.visibility = View.GONE
                var dr: Drawable = BitmapDrawable(activity!!.resources, result)

                if (position == 0) {
                    layout!!.background = dr
                }
            }

        }
    }

}