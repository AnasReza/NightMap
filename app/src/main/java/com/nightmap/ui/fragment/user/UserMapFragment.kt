package com.nightmap.ui.fragment.user

import android.app.Dialog
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
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
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.ui.activity.user.LeaveRatingActivity
import com.nightmap.utility.Preferences


class UserMapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    View.OnClickListener {

    private var mapFragment: SupportMapFragment? = null
    private lateinit var mMap: GoogleMap

    private var leave_rating: Button? = null
    private var discountedText:TextView?=null
    private var ratedText:TextView?=null
    private var db:FirebaseFirestore?=null
    private var pref:Preferences?=null
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
        db=Firebase.firestore
        pref= Preferences(activity!!)

        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap?) {

        mMap = map!!
//        for (x in 0..3) {
//            when (x) {
//                0 -> {
//                    val sydney = LatLng(-34.0, 151.0)
//                    var icon: BitmapDescriptor =
//                        BitmapDescriptorFactory.fromResource(R.drawable.marker2)
//                    var circle:CircleOptions= CircleOptions()
//                    circle.center(sydney)
//                    circle.radius(10000.0)
//                    circle.fillColor(Color.RED)
//                    circle.strokeColor(Color.BLUE)
//                    circle.strokeWidth(5.0F)
//                    circle.visible(true)
//                    var markerOptions: MarkerOptions =
//                        MarkerOptions().position(sydney).icon(icon).title("1")
//mMap.addCircle(circle)
//                    mMap.addMarker(markerOptions)
//                }
//                1 -> {
//                    val sydney = LatLng(-33.9999, 151.13)
//                    var icon: BitmapDescriptor =
//                        BitmapDescriptorFactory.fromResource(R.drawable.marker2)
//                    var markerOptions: MarkerOptions =
//                        MarkerOptions().position(sydney).icon(icon).title("1")
//
//                    mMap.addMarker(markerOptions)
//                }
//                2 -> {
//                    val sydney = LatLng(-33.89, 150.95)
//                    var icon: BitmapDescriptor =
//                        BitmapDescriptorFactory.fromResource(R.drawable.marker2)
//                    var markerOptions: MarkerOptions =
//                        MarkerOptions().position(sydney).icon(icon).title("1")
//
//                    mMap.addMarker(markerOptions)
//                }
//            }
//        }

        db!!.collection("Bars").get().addOnSuccessListener { documents->
            for (doc in documents){
                val urls:ArrayList<String> = doc.get("imagesURL") as ArrayList<String>
                val geopoint:GeoPoint= doc.get("location") as GeoPoint
                val latlng:LatLng= LatLng(geopoint.latitude,geopoint.longitude)
                val cd= ColorDrawable(ContextCompat.getColor(activity!!,R.color.custom_thumb_color))
                Glide.with(activity!!).asBitmap().load(urls[0])
                    .override(100,100)
                    .apply(RequestOptions().fitCenter())
                    .listener(object :  RequestListener<Bitmap> {
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
//                            resource!!.width=100
//                            resource!!.height=100
                            Log.e("Anas","${resource!!.width}= width   ${resource!!.height} = height")

                    var icon: BitmapDescriptor =
                        BitmapDescriptorFactory.fromBitmap(getCircular(resource))

                    var markerOptions: MarkerOptions =
                        MarkerOptions().position(sydney).icon(icon).title("1")

                    mMap.addMarker(markerOptions)

                            return true
                        }

                    })
                    .placeholder(cd)
                    .preload()

            }
            mMap.setOnMarkerClickListener(this)
//            val cameraPosition =
//                CameraPosition.Builder().target(latlng).zoom(0f).build()
//            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
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
            (radius ).toFloat(),
            paint
        )
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        canvas.drawBitmap(bm, rect, rect, paint)
        return bmOut
    }
    override fun onMarkerClick(marker: Marker?): Boolean {
        var title = marker!!.title
        if (title.contentEquals("1")) {
            Log.i("Anas", "MARKER IS PRESSED")
            var dialog: Dialog = Dialog(activity!!)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
            dialog.setContentView(R.layout.dialog_bar)

            leave_rating = dialog.findViewById(R.id.leave_rating)
            discountedText=dialog.findViewById(R.id.discountedText)
            ratedText=dialog.findViewById(R.id.ratedText)

            dialog.show()

            leave_rating!!.setOnClickListener(this)
            var WordtoSpan: Spannable = SpannableString("20% Discount For Each Rating")
            WordtoSpan.setSpan(
                ForegroundColorSpan(Color.WHITE), 0, 3,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            discountedText!!.text = WordtoSpan

            WordtoSpan = SpannableString("34 RATED TONIGHT")
            WordtoSpan.setSpan(
                ForegroundColorSpan(Color.WHITE), 0, 3,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            ratedText!!.text=WordtoSpan
        }
        return true
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.leave_rating->{
                activity!!.startActivity(Intent(activity!!,LeaveRatingActivity::class.java))
            }
        }
    }
}