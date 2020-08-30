package com.nightmap.ui.fragment.user

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.maps.android.ui.IconGenerator
import com.nightmap.R
import com.nightmap.ui.activity.user.LeaveRatingActivity
import com.nightmap.utility.Preferences
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat


class UserMapFragment : Fragment(), OnMapReadyCallback,
    View.OnClickListener {

    private var mapFragment: SupportMapFragment? = null
    private lateinit var mMap: GoogleMap

    private var leave_rating: Button? = null
    private var discountedText: TextView? = null
    private var ratedText: TextView? = null
    private var db: FirebaseFirestore? = null
    private var pref: Preferences? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_user_map, container, false)
        init(view)
        return view
    }

    private fun init(view: View?) {
        db = Firebase.firestore
        pref = Preferences(activity!!)

        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap?) {

        mMap = map!!

        db!!.collection("Bars").get().addOnSuccessListener { documents ->
            for (doc in documents) {
                val status=doc.get("status").toString()
                if(status=="approved"){
                    val mMap2: GoogleMap = map!!
                    val urls: ArrayList<String> = doc.get("imagesURL") as ArrayList<String>
                    val geopoint: GeoPoint = doc.get("location") as GeoPoint

                    var latlng: LatLng = LatLng(geopoint.latitude, geopoint.longitude)

                    val cd =
                        ColorDrawable(ContextCompat.getColor(activity!!, R.color.custom_thumb_color))
                    Glide.with(activity!!).asBitmap().load(urls[0])
                        .override(120, 120)
                        .apply(RequestOptions().fitCenter())
                        .listener(object : RequestListener<Bitmap> {


                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Bitmap>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                e!!.printStackTrace()
                                return true
                            }

                            override fun onResourceReady(
                                resource: Bitmap?,
                                model: Any?,
                                target: Target<Bitmap>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                val iconFactory = IconGenerator(activity!!)
                                var icon2: BitmapDescriptor =
                                    BitmapDescriptorFactory.fromBitmap(getCircular(resource!!))

                                var bitmap = getCircular(resource!!)
                                var textImage =
                                    createDrawableFromView(
                                        activity!!,
                                        urls[0],
                                        doc.get("title").toString(),
                                        doc.getLong("discount")!!,
                                        doc.getLong("todayReviews")!!
                                    )
                                var scaledBitmap =
                                    Bitmap.createScaledBitmap(textImage!!, 200, 120, true)

                                Log.d("Anas","${bitmap!!.width}==width ${bitmap!!.height}==height")
                                var config:Bitmap.Config=Bitmap.Config.ARGB_8888
                                var bmpOverlay=Bitmap.createBitmap(200,200,config)
                                var canvas:Canvas= Canvas(bmpOverlay)

                                canvas.drawBitmap(bitmap, 60f,0f,null)
                                canvas.drawBitmap(scaledBitmap, 0F,51F,null)

                                var markerOptions: MarkerOptions =
                                    MarkerOptions().position(latlng).title(doc.id)
                                        .icon(BitmapDescriptorFactory.fromBitmap(bmpOverlay))

                                //mMap.addMarker(MarkerOptions().icon(icon2).position(latlng2))
                                mMap.addMarker(markerOptions)

                                mMap2.setOnMarkerClickListener { p0 ->
                                    markerClicked(p0!!.title.toString())
                                    true
                                }

                                return true
                            }

                        })
                        .placeholder(cd)
                        .preload()
                }
            }

//            val cameraPosition =
//                CameraPosition.Builder().target(latlng).zoom(0f).build()
//            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }

    }

    private fun createDrawableFromView(
        context: Context,
        url: String,
        title: String,
        discount: Long,
        todayRating: Long
    ): Bitmap? {
        val displayMetrics = DisplayMetrics()
        val view: View = activity!!.layoutInflater.inflate(R.layout.example, null)
        // val img: ImageView = view.findViewById(R.id.marker_image)
        val titleText: TextView = view.findViewById(R.id.marker_title)
        val ratedText: TextView = view.findViewById(R.id.marker_rated)
        val discountText: TextView = view.findViewById(R.id.marker_discount)
        titleText.text = title
        discountText.text = "$discount"
        ratedText.text = "$todayRating"


        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
        view.layout(
           0,
            0,
            displayMetrics.widthPixels,
            displayMetrics.heightPixels
        )
        view.buildDrawingCache()
//        Log.d("Anas", "${view.measuredWidth}==width    ${view.measuredHeight}==height")
        val bmp =
            Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        view.draw(canvas)

        return bmp
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
    private fun isTablet(context: Context): Boolean {
        return context.resources.configuration.screenLayout and
                Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }
    private fun markerClicked(id: String) {
        val displayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        val density=displayMetrics.densityDpi
        var dialog: Dialog = Dialog(activity!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_bar)

        val leave_rating: Button = dialog.findViewById(R.id.leave_rating)
        val discountedText: TextView = dialog.findViewById(R.id.discountedText)
        val ratedText: TextView = dialog.findViewById(R.id.ratedText)
        val barName: TextView = dialog.findViewById(R.id.barName)
        val musicNumber: TextView = dialog.findViewById(R.id.musicNumber)
        val atmosNumber: TextView = dialog.findViewById(R.id.atmosNumber)
        val description: TextView = dialog.findViewById(R.id.description)
        val imageLayout: ImageView = dialog.findViewById(R.id.imageLayout)
        val atmosText: TextView=dialog.findViewById(R.id.atmosText)
        val musicText: TextView= dialog.findViewById(R.id.musicText)
        val genderText: TextView= dialog.findViewById(R.id.genderText)
        val crowdText: TextView= dialog.findViewById(R.id.crowdText)

        db!!.collection("Bars").document(id).get().addOnSuccessListener { doc ->
            Log.e("Anas", "${doc!!.id} dialog")
            val discount = (doc!!.get("discount").toString())
            val rateTonight = (doc!!.get("todayReviews").toString())
            val urlList: ArrayList<String> = doc.get("imagesURL") as ArrayList<String>

            barName!!.text = doc.get("title").toString()
            atmosNumber!!.text = DecimalFormat("#.#").format(doc!!.get("averageAtmosphere"))
            musicNumber!!.text = DecimalFormat("#.#").format(doc!!.get("averageMusic"))
            description!!.text = doc!!.get("description").toString()
            Glide.with(this).asBitmap().load(urlList[0])
                .apply(RequestOptions.circleCropTransform())
                .into(imageLayout)

            if(density==120||density<320){
                if (isTablet(activity!!)) {
                    crowdText!!.textSize = (width * 0.33).toFloat()
                    atmosText!!.textSize = (width * 0.33).toFloat()
                    musicText!!.textSize = ((width * 0.33).toFloat())
                    genderText!!.textSize = (width * 0.33).toFloat()
                } else {
                    crowdText!!.textSize = (width * 0.033).toFloat()
                    atmosText!!.textSize = (width * 0.033).toFloat()
                    musicText!!.textSize = (width * 0.033).toFloat()
                    genderText!!.textSize = (width * 0.033).toFloat()
                }
            }
            else if (density == 320 || density < 420) {
                if (isTablet(activity!!)) {
                    crowdText!!.textSize = (width * 0.0125).toFloat()
                    atmosText!!.textSize = (width * 0.0125).toFloat()
                    musicText!!.textSize = ((width * 0.0125).toFloat())
                    genderText!!.textSize = (width * 0.0125).toFloat()
                } else {
                    crowdText!!.textSize = (width * 0.0125).toFloat()
                    atmosText!!.textSize = (width * 0.0125).toFloat()
                    musicText!!.textSize = (width * 0.0125).toFloat()
                    genderText!!.textSize = (width * 0.0125).toFloat()
                }
            }
            else if (density == 420 || density < 560) {
                if (isTablet(activity!!)) {
                    crowdText!!.textSize = (width * 0.01).toFloat()
                    atmosText!!.textSize = (width * 0.01).toFloat()
                    musicText!!.textSize = ((width * 0.01).toFloat())
                    genderText!!.textSize = (width * 0.01).toFloat()
                } else {
                    crowdText!!.textSize = (width * 0.01).toFloat()
                    atmosText!!.textSize = (width * 0.01).toFloat()
                    musicText!!.textSize = (width * 0.01).toFloat()
                    genderText!!.textSize = (width * 0.01).toFloat()
                }
            }
            else if (density == 560 || density < 640) {
                if (isTablet(activity!!)) {
                    crowdText!!.textSize = (width * 0.0078).toFloat()
                    atmosText!!.textSize = (width * 0.0078).toFloat()
                    musicText!!.textSize = ((width * 0.0078).toFloat())
                    genderText!!.textSize = (width * 0.0078).toFloat()
                } else {
                    crowdText!!.textSize = (width * 0.0078).toFloat()
                    atmosText!!.textSize = (width * 0.0078).toFloat()
                    musicText!!.textSize = (width * 0.0078).toFloat()
                    genderText!!.textSize = (width * 0.0078).toFloat()
                }
            }
            else if (density >= 640) {
                if (isTablet(activity!!)) {

                    crowdText!!.textSize = (width * 0.001).toFloat()
                    atmosText!!.textSize = (width * 0.001).toFloat()
                    musicText!!.textSize = ((width * 0.001).toFloat())
                    genderText!!.textSize = (width * 0.001).toFloat()
                } else {
                    crowdText!!.textSize = (width * 0.007).toFloat()
                    atmosText!!.textSize = (width * 0.007).toFloat()
                    musicText!!.textSize = (width * 0.007).toFloat()
                    genderText!!.textSize = (width * 0.007).toFloat()
                }
            }

            dialog.show()

            leave_rating!!.setOnClickListener {
                activity!!.startActivity(
                    Intent(
                        activity!!,
                        LeaveRatingActivity::class.java
                    ).putExtra("barId", doc!!.id)
                )
            }
            var WordtoSpan: Spannable = SpannableString("$discount% Discount For Each Rating")
            WordtoSpan.setSpan(
                ForegroundColorSpan(Color.WHITE), 0, discount.length + 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            discountedText!!.text = WordtoSpan

            WordtoSpan = SpannableString("$rateTonight RATED TONIGHT")
            WordtoSpan.setSpan(
                ForegroundColorSpan(Color.WHITE), 0, rateTonight.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            ratedText!!.text = WordtoSpan
        }

    }

    fun getCircular(bm: Bitmap): Bitmap? {
        var w = bm.width
        var h = bm.height
        val radius = if (w < h) w else h
        w = radius
        h = radius
        val bmOut = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmOut)
        val paint = Paint()
        paint.setAntiAlias(true)
        paint.setColor(-0xbdbdbe)
        val rect = Rect(0, 0, w, h)
        val rectF = RectF(rect)
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle(
            rectF.left + rectF.width() / 2,
            rectF.top + rectF.height() / 2,
            (radius / 2).toFloat(),
            paint
        )
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        canvas.drawBitmap(bm, rect, rect, paint)
        return bmOut
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.leave_rating -> {

            }
        }
    }

    inner class EventTask(val url: String, val layout: ImageView) :
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