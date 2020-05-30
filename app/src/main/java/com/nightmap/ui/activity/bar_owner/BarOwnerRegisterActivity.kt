package com.nightmap.ui.activity.bar_owner

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.nightmap.R
import com.nightmap.ui.activity.OTPActivity
import com.nightmap.utility.Preferences
import java.util.ArrayList

class BarOwnerRegisterActivity : AppCompatActivity(), View.OnClickListener {
    private var barRegister: Button? = null
    private var bar_image: ImageView? = null
    private var upload_bar: ImageView? = null
    private var full_name_text: TextView? = null
    private var phone_number_text: TextView? = null
    private var bar_name_text: TextView? = null
    private var bar_location_text: TextView? = null
    private var description_text: TextView? = null
    private var discount_text: TextView? = null
    private var free_drink_text: TextView? = null

    private var locationManager: LocationManager? = null
    private var pref: Preferences? = null
    private var db: FirebaseFirestore? = null
    private var id: String = ""
    private var imageURI: Uri? = null
    private var check: Boolean = true
    private var nameString: String = ""
    private var phoneString: String = ""
    private var barString: String = ""
    private var locationString: String = ""
    private var descriptionString: String = ""
    private var discountString: Int = 0
    private var freeDrinkString: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_owner_register)
        init()
    }

    private fun init() {
        barRegister = findViewById(R.id.user_register)
        bar_image = findViewById(R.id.bar_image)
        upload_bar = findViewById(R.id.upload_bar)
        full_name_text = findViewById(R.id.full_name_text)
        phone_number_text = findViewById(R.id.phone_number_text)
        bar_name_text = findViewById(R.id.bar_name_text)
        bar_location_text = findViewById(R.id.bar_location_text)
        description_text = findViewById(R.id.description_text)
        discount_text = findViewById(R.id.discount_text)
        free_drink_text = findViewById(R.id.free_drink_text)

        pref = Preferences(this)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        db = Firebase.firestore
        bar_image!!.setOnClickListener(this)
        barRegister!!.setOnClickListener(this)
    }

    private fun checkPermissionForImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                && (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            ) {
                val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                val permissionCoarse = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

                requestPermissions(
                    permission,
                    1001
                ) // GIVE AN INTEGER VALUE FOR PERMISSION_CODE_READ LIKE 1001
                requestPermissions(
                    permissionCoarse,
                    1002
                ) // GIVE AN INTEGER VALUE FOR PERMISSION_CODE_WRITE LIKE 1002
            } else {
                pickImageFromGallery()
            }
        } else {
            pickImageFromGallery()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 1000) // GIVE AN INTEGER VALUE FOR IMAGE_PICK_CODE LIKE 1000
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 1000) {
            // I'M GETTING THE URI OF THE IMAGE AS DATA AND SETTING IT TO THE IMAGEVIEW
            var params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )

            bar_image!!.layoutParams = params
            // bar_image!!.setImageURI(data?.data)
            bar_image!!.visibility = View.GONE
            upload_bar!!.setImageURI(data?.data)
            imageURI = data?.data
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.user_register -> {
                if (imageURI != null) {
                    registerInFirebase()
                } else {
                    Toast.makeText(this, "Please upload the BAR Picture", Toast.LENGTH_SHORT).show()
                }


            }
            R.id.bar_image -> {
                checkPermissionForImage()
            }
        }
    }

    private fun registerInFirebase() {
        nameString = full_name_text!!.text.toString()
        phoneString = phone_number_text!!.text.toString()
        barString = bar_name_text!!.text.toString()
        locationString = bar_location_text!!.text.toString()
        descriptionString = description_text!!.text.toString()
        discountString = (discount_text!!.text.toString()).toInt()
        freeDrinkString = (free_drink_text!!.text.toString()).toInt()

        startActivity(
            Intent(
                this@BarOwnerRegisterActivity,
                OTPActivity::class.java
            ).putExtra("category", "BarOwner")
                .putExtra("phone", phoneString)
                .putExtra("name", nameString)
                .putExtra("barName", barString)
                .putExtra("location", locationString)
                .putExtra("description", descriptionString)
                .putExtra("discount", discountString)
                .putExtra("freeDrink", freeDrinkString)
                .putExtra("image", imageURI)

        )
    }


}