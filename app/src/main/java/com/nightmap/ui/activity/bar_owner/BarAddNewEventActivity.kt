package com.nightmap.ui.activity.bar_owner

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.updateMargins
import com.github.ybq.android.spinkit.SpinKitView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.nightmap.R
import com.nightmap.ui.activity.UsersMapsActivity
import com.nightmap.utility.Preferences
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class BarAddNewEventActivity : AppCompatActivity(), View.OnClickListener {
    private var age_switch: SwitchCompat? = null
    private var add_event: Button? = null
    private var age_layout: LinearLayout? = null
    private var circleLay: RelativeLayout? = null
    private var upload_pictures: TextView? = null
    private var titleText: EditText? = null
    private var dateText: TextView? = null
    private var timeText: TextView? = null
    private var locationText: EditText? = null
    private var description: EditText? = null
    private var minAgeText: EditText? = null
    private var maxAgeText: EditText? = null
    private var upload_bar: LinearLayout? = null
    private var back_button: ImageView? = null
    private var spin_kit: SpinKitView? = null

    private var uriList: ArrayList<Uri>? = null
    private var imageURI: Uri? = null
    private var db: FirebaseFirestore? = null
    private var pref: Preferences? = null
    private var mainCalendar: Calendar? = null
    private var ageLimitCheck: Boolean = false
    private var latlng: LatLng? = null
    private var address: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_add_event)
        init()
    }

    private fun init() {
        db = Firebase.firestore
        pref = Preferences(this)
        mainCalendar = Calendar.getInstance()
        uriList = ArrayList()

        age_switch = findViewById(R.id.age_switch)
        add_event = findViewById(R.id.add_event)
        age_layout = findViewById(R.id.age_layout)

        circleLay = findViewById(R.id.circle)
        upload_pictures = findViewById(R.id.upload_pictures)
        titleText = findViewById(R.id.title)
        dateText = findViewById(R.id.dateText)
        timeText = findViewById(R.id.timeText)
        locationText = findViewById(R.id.locationText)
        description = findViewById(R.id.description)
        minAgeText = findViewById(R.id.minAgeText)
        maxAgeText = findViewById(R.id.maxAgeText)
        upload_bar = findViewById(R.id.upload_bar)
        back_button = findViewById(R.id.back_button)
        spin_kit = findViewById(R.id.spin_kit)

        latlng = LatLng(0.0, 0.0)

        age_switch!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            ageLimitCheck = isChecked
            if (isChecked) {
                age_layout!!.visibility = View.VISIBLE
            } else {
                age_layout!!.visibility = View.GONE

            }
        })

        add_event!!.setOnClickListener(this)
        dateText!!.setOnClickListener(this)
        timeText!!.setOnClickListener(this)
        locationText!!.setOnClickListener(this)
        circleLay!!.setOnClickListener(this)
        back_button!!.setOnClickListener(this)
    }

    private fun addImage(data: Uri?) {
        upload_bar!!.removeAllViewsInLayout()
        upload_bar!!.weightSum = uriList!!.size.toFloat()

        var index = 0
        for (x in 0 until uriList!!.size) {
            index = x
            var params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f
            )
            if (x != uriList!!.size - 1) {
                params.updateMargins(0, 0, 10, 0)
            }


            val newlayout: LinearLayout = LinearLayout(this@BarAddNewEventActivity)
            newlayout.tag = index
            val f = File(getRealPathFromURI(uriList!![x]))
            val d: Drawable = Drawable.createFromPath(f.absolutePath)!!
            newlayout!!.layoutParams = params

            newlayout!!.background = d
            newlayout.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    Log.i("Anas", "${v!!.tag} tag of pressed image")
                    deleteImage(v!!.tag as Int)
                }

            })

            upload_bar!!.addView(newlayout)
        }

    }

    private fun deleteImage(position: Int) {
        uriList!!.removeAt(position)
        upload_bar!!.removeAllViewsInLayout()
        upload_bar!!.weightSum = uriList!!.size.toFloat()
        Log.e(
            "Anas",
            "${uriList!!.size} size of uri list......${uriList!!.size.toFloat() + 1} weight sum ki value"
        )


        var index = 0
        for (x in 0 until uriList!!.size) {
            index = x
            var params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f
            )
            if (x != uriList!!.size - 1) {
                params.updateMargins(0, 0, 10, 0)
            }

            val newlayout: LinearLayout = LinearLayout(this@BarAddNewEventActivity)
            newlayout.tag = index
            val f = File(getRealPathFromURI(uriList!![x]))
            val d: Drawable = Drawable.createFromPath(f.absolutePath)!!
            newlayout!!.layoutParams = params

            newlayout!!.background = d
            newlayout.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    Log.i("Anas", "${v!!.tag} tag of pressed image")
                    deleteImage(v!!.tag as Int)
                }

            })

            upload_bar!!.addView(newlayout)
        }
    }

    private fun getRealPathFromURI(contentURI: Uri): String? {
        val cursor: Cursor? = contentResolver.query(contentURI, null, null, null, null)
        return if (cursor == null) { // Source is Dropbox or other similar local file path
            contentURI.path
        } else {
            cursor.moveToFirst()
            val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            cursor.getString(idx)
        }
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
            uriList!!.add(data?.data!!)

            Log.d("Anas", "${uriList!!.size} size of uri list")
            if (uriList!!.size == 0) {
                var params = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
                )

                val newlayout: RelativeLayout = RelativeLayout(this@BarAddNewEventActivity)

                val f = File(getRealPathFromURI(data?.data!!))
                val d: Drawable = Drawable.createFromPath(f.absolutePath)!!
                newlayout!!.layoutParams = params
                newlayout!!.background = d

                upload_bar!!.addView(newlayout)
            } else if (uriList!!.size > 0) {
                addImage(data?.data)

            }

        } else if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            latlng = data!!.getParcelableExtra("latlng")
            address = data!!.getStringExtra("addressLine")
            locationText!!.setText(address)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.add_event -> {
                if (uriList!!.size != 0) {
                    spin_kit!!.visibility = View.VISIBLE
                    val description: String = description!!.text.toString()
                    val time: Timestamp = Timestamp(mainCalendar!!.time)
                    val title: String = titleText!!.text.toString()
                    val geoPoint: GeoPoint = GeoPoint(latlng!!.latitude, latlng!!.longitude)
                    var minAge = 0
                    var maxAge = 0
                    if (ageLimitCheck) {
                        minAge = (minAgeText!!.text.toString()).toInt()
                        maxAge = (maxAgeText!!.text.toString()).toInt()
                    }
                    var usersGoing: ArrayList<String> = ArrayList<String>()
                    val hashMap =
                        hashMapOf(
                            "addedOn" to FieldValue.serverTimestamp(),
                            "ageLimit" to ageLimitCheck,
                            "barId" to pref!!.getBarID(),
                            "description" to description,
                            "isDeleted" to false,
                            "location" to geoPoint,
                            "eventLocation" to locationText!!.text.toString(),
                            "maxAge" to maxAge,
                            "minAge" to minAge,
                            "eventTime" to time,
                            "title" to title,
                            "usersGoing" to usersGoing
                        )
                    db!!.collection("Events").add(hashMap).addOnSuccessListener { document ->
                        val eventId = document.id
                        val updatingEventId = hashMapOf("eventId" to eventId)
                        db!!.collection("Events").document(eventId)
                            .update(updatingEventId as Map<String, Any>)

                        var mStorageRef: StorageReference = FirebaseStorage.getInstance().reference
                        var stoRef: StorageReference
                        val file: java.util.ArrayList<String> =
                            arrayListOf<String>()
                        for (x in 0 until uriList!!.size) {
                            stoRef = mStorageRef.child("Events/$eventId/image$x.png")
                            val uploadTask = stoRef.putFile(uriList!![x])
                            uploadTask.addOnSuccessListener { taskSnapshot ->

                                taskSnapshot!!.storage.downloadUrl.addOnCompleteListener(
                                    OnCompleteListener { task ->

                                        file.add(task.result.toString())
                                        val newHashMap = hashMapOf("imagesUrl" to file)
                                        db!!.collection("Events").document(eventId)
                                            .update(newHashMap as Map<String, Any>)
                                            .addOnSuccessListener {
                                                if (x == uriList!!.size - 1) {
                                                    finish()
                                                    spin_kit!!.visibility = View.GONE
                                                }
                                            }
                                        Log.d("Anas", "Event is uploaded")

                                    }
                                )
                            }
                        }

                    }
                } else {
                    val builder = AlertDialog.Builder(this@BarAddNewEventActivity)
                    builder.setTitle("Error")
                    builder.setMessage("Please Upload atleast 1 Image.")
                    builder.setPositiveButton("Okay") { dialog, which ->
                        dialog.dismiss()
                    }
                    builder.show()
                }

            }

            R.id.circle -> {
                if (uriList!!.size < 5) {
                    checkPermissionForImage()
                } else {
                    val builder = AlertDialog.Builder(this@BarAddNewEventActivity)
                    builder.setTitle("Error")
                    builder.setMessage("You can only upload maximum of 5 images.")
                    builder.setPositiveButton("Okay") { dialog, which ->
                        dialog.dismiss()

                    }
                    builder.show()
                }
            }
            R.id.dateText -> {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(
                    this@BarAddNewEventActivity,
                    DatePickerDialog.OnDateSetListener { view, newYear, newMonth, dayOfMonth ->

                        mainCalendar!!.set(Calendar.YEAR, newYear)
                        mainCalendar!!.set(Calendar.MONTH, newMonth)
                        mainCalendar!!.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        dateText!!.text = "${newMonth + 1}/$dayOfMonth/$newYear"
                    },
                    year,
                    month,
                    day
                )
                dpd.show()
            }
            R.id.timeText -> {
                val cal = Calendar.getInstance()
                val timeSetListener =
                    TimePickerDialog.OnTimeSetListener { view, hourOfDay, min ->
                        cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        cal.set(Calendar.MINUTE, min)
                        val hour = DateFormat.format("h", cal.time).toString()
                        val minute = DateFormat.format("mm", cal.time).toString()
                        val am_pm = DateFormat.format("a", cal.time).toString()
                        mainCalendar!!.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        mainCalendar!!.set(Calendar.MINUTE, min)

                        val time: Timestamp = Timestamp(mainCalendar!!.time)
                        timeText!!.text = "$hour $am_pm"
                    }


                TimePickerDialog(
                    this,
                    timeSetListener,
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    false
                ).show()
            }
            R.id.locationText -> {
                var i: Intent = Intent(this, UsersMapsActivity::class.java)
                startActivityForResult(i, 1)
            }

            R.id.back_button -> {
                onBackPressed()
            }
        }
    }
}