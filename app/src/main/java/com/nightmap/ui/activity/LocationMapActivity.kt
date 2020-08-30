package com.nightmap.ui.activity

import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.utility.Preferences

class LocationMapActivity : AppCompatActivity(), OnMapReadyCallback {
    private var headline: TextView? = null
    private lateinit var mMap: GoogleMap
    private var back_button:ImageView?=null
    private var db: FirebaseFirestore? = null
    private var pref: Preferences? = null
    private var dbType: String = ""
    private var columnName: String = ""
    private var location: String = ""
    private var ID: String = ""
    private var mainURl: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        init()


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun init() {
        db = Firebase.firestore
        pref = Preferences(this)
        back_button=findViewById(R.id.back_button)
        back_button!!.setOnClickListener { onBackPressed() }

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        ID = intent.getStringExtra("id")
        dbType = intent.getStringExtra("dbType")
        columnName = intent.getStringExtra("columnName")
        location = intent.getStringExtra("location")
        headline = findViewById(R.id.headline)
        headline!!.text = intent.getStringExtra("heading")
        Log.e("Anas", "$ID event id")

        db!!.collection(dbType).document(ID).get().addOnSuccessListener { documents ->
            try {
                val urls: ArrayList<String> = documents.get(columnName) as ArrayList<String>
                mainURl = urls[0]
            } catch (e: ClassCastException) {
                val urls = documents.get(columnName).toString()
                mainURl = urls
            }

            val geopoint: GeoPoint = documents.get(location) as GeoPoint
            val latlng: LatLng = LatLng(geopoint.latitude, geopoint.longitude)
            val cd =
                ColorDrawable(ContextCompat.getColor(this, R.color.custom_thumb_color))
            if (mainURl != "") {
                Glide.with(this).asBitmap().load(mainURl)
                    .override(100, 100)
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
                            val sydney = latlng

                            var icon: BitmapDescriptor =
                                BitmapDescriptorFactory.fromBitmap(getCircular(resource!!))

                            var markerOptions: MarkerOptions =
                                MarkerOptions().position(sydney).icon(icon)


                            mMap.addMarker(markerOptions)

                            return true
                        }

                    })
                    .placeholder(cd)
                    .preload()
            } else {
                Glide.with(this).asBitmap().load(R.drawable.user_placeholder)
                    .override(100, 100)
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
                            val sydney = latlng

                            var icon: BitmapDescriptor =
                                BitmapDescriptorFactory.fromBitmap(getCircular(resource!!))

                            var markerOptions: MarkerOptions =
                                MarkerOptions().position(sydney).icon(icon)


                            mMap.addMarker(markerOptions)

                            return true
                        }

                    })
                    .placeholder(cd)
                    .preload()
            }
            val cameraPosition =
                CameraPosition.Builder().target(latlng).zoom(11f).build()
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
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
}
