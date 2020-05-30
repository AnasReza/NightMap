package com.nightmap.ui.activity.user

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.ui.activity.OTPActivity
import com.nightmap.utility.Preferences

class UserRegisterActivity : AppCompatActivity(), View.OnClickListener {
    private var userRegister: Button? = null
    private var username_edit_text: EditText? = null
    private var phone_number_edit_text: EditText? = null
    private var pref: Preferences? = null
    private var db: FirebaseFirestore? = null
    private var auth: FirebaseAuth? = null
    private var locationManager: LocationManager? = null
    private var user: String = ""
    private var phone: String = ""
    private var coordinates: String = ""
    private var check:Boolean=true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_register)
        init()
    }

    private fun init() {
        userRegister = findViewById(R.id.user_register)
        username_edit_text = findViewById(R.id.username_edit_text)
        phone_number_edit_text = findViewById(R.id.phone_number_edit_text)

        pref = Preferences(this)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        userRegister!!.setOnClickListener(this)
    }

    private fun registerInFirebase() {
        user = username_edit_text!!.text.toString()
        phone = phone_number_edit_text!!.text.toString()
        if (ActivityCompat.checkSelfPermission(
                this@UserRegisterActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@UserRegisterActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this@UserRegisterActivity, "Location not granted", Toast.LENGTH_SHORT)
                .show()
            return
        } else {
            locationManager?.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0L,
                0F,
                locationListener
            )


        }

    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location?) {
            if(check){
                check=false
                var latlng:LatLng= LatLng(location!!.latitude,location!!.longitude)


                startActivity(
                    Intent(
                        this@UserRegisterActivity,
                        OTPActivity::class.java
                    ).putExtra("category", "User").putExtra("phone", phone).putExtra("name", user)
                        .putExtra("latlng", latlng)
                )
            }

        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

        override fun onProviderEnabled(provider: String?) {
            Log.e("Anas","provider is enabled")
        }

        override fun onProviderDisabled(provider: String?) {
            Toast.makeText(this@UserRegisterActivity,"Please enable the Location",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.user_register -> {
                registerInFirebase()
            }
        }
    }
}