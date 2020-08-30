package com.nightmap.ui.activity.user

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.text.set
import com.github.ybq.android.spinkit.SpinKitView
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.ui.activity.OTPActivity
import com.nightmap.ui.activity.TextActivity
import com.nightmap.utility.Preferences
import java.util.concurrent.TimeUnit

class UserRegisterActivity : AppCompatActivity(), View.OnClickListener {
    private var userRegister: Button? = null
    private var username_edit_text: EditText? = null
    private var phone_number_edit_text: EditText? = null
    private var back_button: ImageView? = null
    private var textView: TextView? = null
    private var spin_kit: SpinKitView? = null

    private var pref: Preferences? = null
    private var db: FirebaseFirestore? = null
    private var auth: FirebaseAuth? = null
    private var locationManager: LocationManager? = null
    private var user: String = ""
    private var phone: String = ""
    private var check: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_register)
        init()
    }

    private fun init() {
        userRegister = findViewById(R.id.user_register)
        username_edit_text = findViewById(R.id.username_edit_text)
        phone_number_edit_text = findViewById(R.id.phone_number_edit_text)
        textView = findViewById(R.id.textView)
        back_button = findViewById(R.id.back_button)
        spin_kit = findViewById(R.id.spin_kit)

        pref = Preferences(this)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        userRegister!!.setOnClickListener(this)
        back_button!!.setOnClickListener {
            spin_kit!!.visibility = View.GONE
            onBackPressed()
        }


        val termSpan =
            SpannableString("By Registering With Us You Agree To Accept Our Terms & Conditions And Privacy Policy")
        var termClickable: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                spin_kit!!.visibility = View.GONE
                startActivity(
                    Intent(
                        this@UserRegisterActivity,
                        TextActivity::class.java
                    ).putExtra("headline", "TERMS OF USAGE")
                )
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isFakeBoldText = false
                ds.isUnderlineText = false
                ds.typeface = Typeface.DEFAULT_BOLD
                ds.color = resources.getColor(R.color.white)
            }
        }
        var policyClickable: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                spin_kit!!.visibility = View.GONE
                startActivity(
                    Intent(
                        this@UserRegisterActivity,
                        TextActivity::class.java
                    ).putExtra("headline", "PRIVACY POLICY")
                )
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isFakeBoldText = false
                ds.isUnderlineText = false
                ds.typeface = Typeface.DEFAULT_BOLD
                ds.color = resources.getColor(R.color.white)
            }
        }
        termSpan.setSpan(termClickable, 47, 65, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        termSpan[70, 84] = policyClickable
        textView!!.text = termSpan
        textView!!.movementMethod = LinkMovementMethod.getInstance()
        textView!!.highlightColor = Color.TRANSPARENT

    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

     fun registerInFirebase() {
        spin_kit!!.visibility = View.VISIBLE
        user = username_edit_text!!.text.toString()
        phone = phone_number_edit_text!!.text.toString()

             if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
                 if (ActivityCompat.checkSelfPermission(
                         this,
                         Manifest.permission.ACCESS_FINE_LOCATION
                     ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                         this,
                         Manifest.permission.ACCESS_COARSE_LOCATION
                     ) != PackageManager.PERMISSION_GRANTED
                 ) {
                     Log.d("Anas", "Location not granted")

                 } else {

                     locationManager?.requestLocationUpdates(
                         LocationManager.NETWORK_PROVIDER,
                         0L,
                         0F,
                         locationListener
                     )


                 }
             } else {

                 var list = arrayOf(
                     Manifest.permission.ACCESS_FINE_LOCATION,
                     Manifest.permission.ACCESS_COARSE_LOCATION
                 )
                 requestPermissions(list, 100)

             }



    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location?) {
            if (check) {
                spin_kit!!.visibility=View.GONE
                check = false
                var latlng: LatLng = LatLng(location!!.latitude, location.longitude)


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
            Log.e("Anas", "provider is enabled")
        }

        override fun onProviderDisabled(provider: String?) {
            spin_kit!!.visibility=View.GONE
            Log.d("Anas", "Please enable the Location")
            Toast.makeText(
                this@UserRegisterActivity,
                "Please enable the Location",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            Log.e("Anas", " Permission granted")
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e("Anas", "You have denied the permission")
            } else {
                locationManager?.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0L,
                    0F,
                    locationListener
                )
            }

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