package com.nightmap.ui.activity.bar_owner

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.nightmap.R
import com.nightmap.utility.Preferences
import java.util.*
import kotlin.collections.ArrayList


class BarAddNewEventActivity : AppCompatActivity(), View.OnClickListener {
    private var age_switch: SwitchCompat? = null
    private var add_event: Button? = null
    private var age_layout: LinearLayout? = null
    private var imageLayout: ImageView? = null
    private var circleLay: RelativeLayout? = null
    private var upload_pictures: TextView? = null
    private var titleText: EditText? = null
    private var dateText: TextView? = null
    private var timeText: TextView? = null
    private var locationText: EditText? = null
    private var description: EditText? = null
    private var minAgeText: EditText? = null
    private var maxAgeText: EditText? = null

    private var imageURI: Uri? = null
    private var db: FirebaseFirestore? = null
    private var pref: Preferences? = null
    private var mainCalendar: Calendar? = null
    private var ageLimitCheck: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_add_event)
        init()
    }

    private fun init() {
        db = Firebase.firestore
        pref = Preferences(this)
        mainCalendar = Calendar.getInstance()

        age_switch = findViewById(R.id.age_switch)
        add_event = findViewById(R.id.add_event)
        age_layout = findViewById(R.id.age_layout)
        imageLayout = findViewById(R.id.imageView)
        circleLay = findViewById(R.id.circle)
        upload_pictures = findViewById(R.id.upload_pictures)
        titleText = findViewById(R.id.title)
        dateText = findViewById(R.id.dateText)
        timeText = findViewById(R.id.timeText)
        locationText = findViewById(R.id.locationText)
        description = findViewById(R.id.description)
        minAgeText = findViewById(R.id.minAgeText)
        maxAgeText = findViewById(R.id.maxAgeText)

        age_switch!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            ageLimitCheck = isChecked
            if (isChecked) {
                age_layout!!.visibility = View.VISIBLE
            } else {
                age_layout!!.visibility = View.GONE

            }
        })

        add_event!!.setOnClickListener(this)
        imageLayout!!.setOnClickListener(this)
        dateText!!.setOnClickListener(this)
        timeText!!.setOnClickListener(this)
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
            circleLay!!.visibility = View.GONE
            upload_pictures!!.visibility = View.GONE
            imageLayout!!.layoutParams = params
            // bar_image!!.setImageURI(data?.data)

            imageLayout!!.setImageURI(data?.data)
            imageURI = data?.data
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.add_event -> {
                if(imageURI!=null){
                    val description: String = description!!.text.toString()
                    val time: Timestamp = Timestamp(mainCalendar!!.time)
                    val title: String = titleText!!.text.toString()
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
                            "maxAge" to maxAge,
                            "minAge" to minAge,
                            "time" to time,
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
                        stoRef = mStorageRef.child("Events/$eventId/image.png")
                        val uploadTask = stoRef.putFile(imageURI!!)
                        uploadTask.addOnSuccessListener { taskSnapshot ->
                            taskSnapshot!!.storage.downloadUrl.addOnCompleteListener(
                                OnCompleteListener { task ->
                                    val file: java.util.ArrayList<String> = arrayListOf<String>()
                                    file.add(task.result.toString())
                                    val newHashMap = hashMapOf("imageUrl" to file)
                                    db!!.collection("Events").document(eventId).update(newHashMap as Map<String, Any>)


                                }
                            )
                        }
                    }
                }

            }

            R.id.imageView -> {
                checkPermissionForImage()
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
        }
    }
}