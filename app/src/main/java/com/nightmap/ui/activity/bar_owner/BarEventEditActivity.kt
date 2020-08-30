package com.nightmap.ui.activity.bar_owner

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.AsyncTask
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
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class BarEventEditActivity : AppCompatActivity(), View.OnClickListener {
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
    private var spinKitView:SpinKitView?=null

    private var uriList: ArrayList<Uri>? = null
    private var urlList: ArrayList<String>? = null
    private var imageURI: Uri? = null
    private var db: FirebaseFirestore? = null
    private var pref: Preferences? = null
    private var mainCalendar: Calendar? = null
    private var ageLimitCheck: Boolean = false
    private var latlng: LatLng? = null
    private var address: String = ""
    private var eventID: String = ""
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
        eventID = intent.getStringExtra("eventID")

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
        spinKitView=findViewById(R.id.spin_kit)

        latlng = LatLng(0.0, 0.0)

        db!!.collection("Events").document(eventID).get().addOnSuccessListener { documentSnapshot ->
            val ageCheck: Boolean = documentSnapshot.get("ageLimit") as Boolean
            val timestamp: Timestamp = documentSnapshot.get("eventTime") as Timestamp
            urlList = documentSnapshot.get("imagesUrl") as ArrayList<String>?
            val date: Date = timestamp.toDate()
            val day: String = DateFormat.format("dd", date).toString()
            val month: String = DateFormat.format("MM", date).toString()
            val year: String = DateFormat.format("yy", date).toString()
            val hour: String = DateFormat.format("h", date) as String
            val am_pm: String = DateFormat.format("a", date).toString()

            age_switch!!.isChecked = ageCheck
            titleText!!.setText(documentSnapshot.get("title").toString())
            dateText!!.text = "$day/$month/$year"
            timeText!!.text = "$hour $am_pm"
            description!!.setText(documentSnapshot.get("description").toString())
            if (ageCheck) {
                maxAgeText!!.setText(documentSnapshot.get("maxAge").toString())
                minAgeText!!.setText(documentSnapshot.get("minAge").toString())
            }

                addImagefromURL(urlList)


        }
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
    private fun addImagefromURL(url: ArrayList<String>?){
        upload_bar!!.removeAllViewsInLayout()
        upload_bar!!.weightSum = url!!.size.toFloat()

        var index = 0
        for (x in 0 until url!!.size) {
            index = x
            var params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f
            )
            if (x != url!!.size - 1) {
                params.updateMargins(0, 0, 10, 0)
            }


            val newlayout = LinearLayout(this)
            newlayout.tag = x
            newlayout!!.layoutParams = params
            checkImage(url[x],newlayout,x)

            upload_bar!!.addView(newlayout)
        }
    }
    private fun addImage(data: ArrayList<Uri>?) {
        upload_bar!!.removeAllViewsInLayout()
        upload_bar!!.weightSum = (urlList!!.size+uriList!!.size).toFloat()
        val mainFolder = File(this.filesDir, "EventImages/$eventID")
        var totalFiles = mainFolder.listFiles()
        for (x in totalFiles!!.indices) {
            var params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f
            )
            if (x != urlList!!.size - 1) {
                params.updateMargins(0, 0, 10, 0)
            }
            val newlayout: LinearLayout = LinearLayout(this@BarEventEditActivity)
            newlayout.tag = x


                var totalFiles = mainFolder.listFiles()

                val f = totalFiles[x]
                val d: Drawable = Drawable.createFromPath(f.absolutePath)!!
                newlayout!!.layoutParams = params

                newlayout!!.background = d



            newlayout.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    Log.i("Anas", "${v!!.tag} tag of pressed image")
                    deleteUrlImage(v!!.tag as Int)
                }

            })

            upload_bar!!.addView(newlayout)
        }

        for (x in 0 until data!!.size) {

            var params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f
            )
            if (x != uriList!!.size - 1) {
                params.updateMargins(0, 0, 10, 0)
            }


            val newlayout: LinearLayout = LinearLayout(this@BarEventEditActivity)
            newlayout.tag = totalFiles.size+x+1
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

        val mainFolder = File(this.filesDir, "EventImages/$eventID")
        var totalFiles = mainFolder.listFiles()
        for (x in totalFiles!!.indices) {
            var params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f
            )
            if (x != urlList!!.size - 1) {
                params.updateMargins(0, 0, 10, 0)
            }
            val newlayout: LinearLayout = LinearLayout(this@BarEventEditActivity)
            newlayout.tag = x

            if (mainFolder.exists()) {
                var totalFiles = mainFolder.listFiles()

                val f = totalFiles[x]
                val d: Drawable = Drawable.createFromPath(f.absolutePath)!!
                newlayout!!.layoutParams = params

                newlayout!!.background = d

            }

            newlayout.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    Log.i("Anas", "${v!!.tag} tag of pressed image")
                    deleteUrlImage(v!!.tag as Int)
                }

            })

            upload_bar!!.addView(newlayout)
        }

        for (x in 0 until uriList!!.size) {

            var params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f
            )
            if (x != uriList!!.size - 1) {
                params.updateMargins(0, 0, 10, 0)
            }

            val newlayout: LinearLayout = LinearLayout(this)
            newlayout.tag = totalFiles!!.size+x
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
    private fun deleteUrlImage(position: Int) {
        urlList!!.removeAt(position)
        upload_bar!!.removeAllViewsInLayout()
        upload_bar!!.weightSum = urlList!!.size.toFloat()

        val imageFile = File(this.filesDir, "EventImages/$eventID/image$position")
        if (imageFile.exists()) {
            imageFile.delete()
        }
        val mainFolder = File(this.filesDir, "EventImages/$eventID")
        var totalFiles = mainFolder.listFiles()
        for (x in totalFiles!!.indices) {
            var params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f
            )
            if (x != urlList!!.size - 1) {
                params.updateMargins(0, 0, 10, 0)
            }
            val newlayout: LinearLayout = LinearLayout(this@BarEventEditActivity)
            newlayout.tag = x

            if (mainFolder.exists()) {
                var totalFiles = mainFolder.listFiles()

                val f = totalFiles[x]
                val d: Drawable = Drawable.createFromPath(f.absolutePath)!!
                newlayout!!.layoutParams = params

                newlayout!!.background = d

            }

            newlayout.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    Log.i("Anas", "${v!!.tag} tag of pressed image")
                    deleteUrlImage(v!!.tag as Int)
                }

            })

            upload_bar!!.addView(newlayout)
        }
        if (uriList!!.size != 0) {
            for (x in 0 until uriList!!.size) {

                var params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT, 1f
                )
                if (x != uriList!!.size - 1) {
                    params.updateMargins(0, 0, 10, 0)
                }

                val newlayout: LinearLayout = LinearLayout(this)
                newlayout.tag = urlList!!.size+x+1
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
    }

    private fun checkImage(
        url:String,
        layout: LinearLayout,
        index:Int
    ) {
        val myDir = File(this.filesDir, "EventImages/$eventID")
        if (myDir.exists()) {
            var imageFile = File(myDir, "image$index.png")
            if (imageFile.exists()) {

                val dr: Drawable = Drawable.createFromPath(imageFile.absolutePath)!!

                layout!!.background = dr
                layout.setOnClickListener { v -> deleteUrlImage(v!!.tag as Int) }

                BackgroundDownloadTask(url, index, layout!!, myDir).execute()

            } else {
                BackgroundDownloadTask(url, index, layout!!, myDir).execute()
            }

        } else {
            myDir.mkdirs()
            BackgroundDownloadTask(url, index, layout!!, myDir).execute()
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

                addImage(uriList)



        } else if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            latlng = data!!.getParcelableExtra("latlng")
            address = data!!.getStringExtra("addressLine")
            locationText!!.setText(address)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.add_event -> {
                spinKitView!!.visibility=View.VISIBLE
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
                        "eventLocation" to locationText!!.text.toString(),
                        "isDeleted" to false,
                        "location" to geoPoint,
                        "maxAge" to maxAge,
                        "minAge" to minAge,
                        "eventTime" to time,
                        "title" to title
                    )
                db!!.collection("Events").document(eventID).update(hashMap)
                    .addOnSuccessListener { document ->

                        var mStorageRef: StorageReference = FirebaseStorage.getInstance().reference
                        var stoRef: StorageReference
                        if (uriList!!.size != 0) {
                            for (x in 0 until uriList!!.size) {
                                val index = urlList!!.size + x + 1
                                stoRef = mStorageRef.child("EventImages/$eventID/image$index.png")
                                val uploadTask = stoRef.putFile(uriList!![x])
                                uploadTask.addOnSuccessListener { taskSnapshot ->

                                    taskSnapshot!!.storage.downloadUrl.addOnCompleteListener(
                                        OnCompleteListener { task ->

                                            urlList!!.add(task.result.toString())
                                            val urlHash = hashMapOf("imagesUrl" to urlList)
                                            db!!.collection("Events").document(eventID)
                                                .update(urlHash as Map<String, Any>).addOnSuccessListener {
                                                    if(index== (urlList!!.size+uriList!!.size)-1){
                                                        spinKitView!!.visibility=View.GONE
                                                        finish()

                                                    }
                                                }
                                        }
                                    )
                                }

                            }
                        }else{
                            spinKitView!!.visibility=View.GONE
                            finish()
                        }

                    }


            }

            R.id.circle -> {
                if ((uriList!!.size + urlList!!.size) < 5) {
                    checkPermissionForImage()
                } else {
                    val builder = AlertDialog.Builder(this@BarEventEditActivity)
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
                    this@BarEventEditActivity,
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

    inner class BackgroundDownloadTask(
        val url: String,
        val position: Int,
        val layout: LinearLayout,
        val mainFile: File
    ) :
        AsyncTask<String, URL, Bitmap>() {

        override fun doInBackground(vararg params: String?): Bitmap {

            var length = 0
            val bitmap = getBitmapFromURL(url)
            val scaledBitmap = bitmap?.let { Bitmap.createScaledBitmap(it, 739, 415, false) }
            val bos = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.PNG, 0, bos)
            val bitmapdata = bos.toByteArray()
            val bs = ByteArrayInputStream(bitmapdata)

            val fos =
                FileOutputStream("${mainFile.absolutePath}${File.separator}image$position.png")

            while ({ length = bs.read(bitmapdata); length }() > 0) {
                fos.write(bitmapdata, 0, length)
            }
            fos.flush()
            fos.close()
            bs.close()
            return scaledBitmap!!
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            Log.e("Anas", "Downloading and saving file is complete")

            var dr: Drawable = BitmapDrawable(this@BarEventEditActivity.resources, result)

            if (position == 0) {
                layout!!.background = dr
            }
        }

    }
}