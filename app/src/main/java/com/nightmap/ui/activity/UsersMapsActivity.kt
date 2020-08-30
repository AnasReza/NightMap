package com.nightmap.ui.activity

import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.nightmap.R
import java.util.*
import kotlin.collections.ArrayList

class UsersMapsActivity : AppCompatActivity(), OnMapReadyCallback {
private var back_button:ImageView?=null
    private lateinit var mMap: GoogleMap
    private var latLngList: LatLng? = null
    private var str: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        init()

    }

    private fun init() {
        back_button=findViewById(R.id.back_button)
        latLngList = LatLng(0.0, 0.0)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        back_button!!.setOnClickListener { onBackPressed() }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        mMap.setOnMapClickListener { latlng ->
            Log.e("Anas", "$latlng")
            mMap.clear()
            latLngList=latlng
            mMap.addMarker(MarkerOptions().position(latlng).title("1"))

        }
        val cameraPosition =
            CameraPosition.Builder().target(LatLng(-34.0, 151.0)).zoom(11f).build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    override fun onBackPressed() {
        try{
            val geocoder: Geocoder = Geocoder(this, Locale.getDefault())
            var addresses = geocoder.getFromLocation(latLngList!!.latitude, latLngList!!.longitude, 1)

            val address: String = addresses[0]
                .getAddressLine(0)
            val i: Intent =Intent()
            i.putExtra("latlng",latLngList!!)
            i.putExtra("addressLine",address)
            setResult(Activity.RESULT_OK,i)
            finish()
        }catch (e:IndexOutOfBoundsException){
            super.onBackPressed()
        }

    }
}
