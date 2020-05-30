package com.nightmap.ui.activity.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.nightmap.R

class UsersMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        for (x in 0..3) {
            when (x) {
                0 -> {
                    val sydney = LatLng(-34.0, 151.0)
                    var icon: BitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.marker2)
                    var markerOptions: MarkerOptions = MarkerOptions().position(sydney).icon(icon)

                    mMap.addMarker(markerOptions)
                }
                1 -> {
                    val sydney = LatLng(-33.9999, 151.13)
                    var icon: BitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.marker3)
                    var markerOptions: MarkerOptions = MarkerOptions().position(sydney).icon(icon)

                    mMap.addMarker(markerOptions)
                }
                2 -> {
                    val sydney = LatLng(-33.89, 150.95)
                    var icon: BitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.marker2)
                    var markerOptions: MarkerOptions = MarkerOptions().position(sydney).icon(icon)

                    mMap.addMarker(markerOptions)
                }
            }
        }
        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        var icon: BitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.marker3)
//        var markerOptions: MarkerOptions = MarkerOptions().position(sydney).icon(icon)
//
//        mMap.addMarker(markerOptions)

        val cameraPosition =
            CameraPosition.Builder().target(LatLng(-34.0, 151.0)).zoom(11f).build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }
}
