package com.nightmap.ui.activity.bar_owner

import android.Manifest
import android.app.Activity
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
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateMargins
import com.github.ybq.android.spinkit.SpinKitView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
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

class BarAddEditProfileActivity : AppCompatActivity(), View.OnClickListener {
    private var title: EditText? = null
    private var location: EditText? = null
    private var description: EditText? = null
    private var discount: EditText? = null
    private var freeDrink: EditText? = null
    private var imageLayout: RelativeLayout? = null
    private var save_changes: Button? = null
    private var upload_bar: LinearLayout? = null
    private var back_button:ImageView?=null
    private var spinKitView:SpinKitView?=null

    private var uriList: ArrayList<Uri>? = null
    private var imageList: ArrayList<String>? = null
    private var db: FirebaseFirestore? = null
    private var pref: Preferences? = null
    private var imageUri: ArrayList<Uri>? = null
    private var latlng: LatLng? = null
    private var address: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_add_new)
        init()
    }

    private fun init() {
        db = Firebase.firestore
        pref = Preferences(this)
        uriList = ArrayList()
        imageList = ArrayList()
        imageUri = ArrayList()

        title = findViewById(R.id.titleText)
        location = findViewById(R.id.locationText)
        description = findViewById(R.id.description)
        discount = findViewById(R.id.discountText)
        freeDrink = findViewById(R.id.freeDrink)
      //  imageLayout = findViewById(R.id.imageLayout)
        save_changes = findViewById(R.id.save_changes)
        upload_bar = findViewById(R.id.upload_bar)
        back_button =findViewById(R.id.back_button)
        spinKitView = findViewById(R.id.spin_kit)

        db!!.collection("Bars").document(pref!!.getBarID()!!).get()
            .addOnSuccessListener { documentSnapshot ->
                val urls: ArrayList<String> = documentSnapshot.get("imagesURL") as ArrayList<String>
                title!!.setText(documentSnapshot.get("title").toString())
                location!!.setText(documentSnapshot.get("barLocation").toString())
                description!!.setText(documentSnapshot.get("description").toString())
                discount!!.setText(documentSnapshot.get("discount").toString())
                freeDrink!!.setText(documentSnapshot.get("freeDrinkAfter").toString())
                imageList = urls

                    addImagefromURL(imageList)

                //SomeTask(urls[0]).execute()
            }
       // imageLayout!!.setOnClickListener(this)
        save_changes!!.setOnClickListener(this)
        back_button!!.setOnClickListener(this)
        location!!.setOnClickListener(this)
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
        upload_bar!!.weightSum = (imageList!!.size+uriList!!.size).toFloat()
        val mainFolder = File(this.filesDir, "Bars/${pref!!.getBarID()}")
        var totalFiles = mainFolder.listFiles()
        for (x in totalFiles!!.indices) {
            var params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f
            )
            if (x != imageList!!.size - 1) {
                params.updateMargins(0, 0, 10, 0)
            }
            val newlayout: LinearLayout = LinearLayout(this)
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


            val newlayout: LinearLayout = LinearLayout(this)
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

        val mainFolder = File(this.filesDir, "Bars/${pref!!.getBarID()}")
        var totalFiles = mainFolder.listFiles()
        for (x in totalFiles!!.indices) {
            var params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f
            )
            if (x != imageList!!.size - 1) {
                params.updateMargins(0, 0, 10, 0)
            }
            val newlayout: LinearLayout = LinearLayout(this)
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
        imageList!!.removeAt(position)
        upload_bar!!.removeAllViewsInLayout()
        upload_bar!!.weightSum = imageList!!.size.toFloat()

        val imageFile = File(this.filesDir, "Bars/${pref!!.getBarID()}/image$position")
        if (imageFile.exists()) {
            imageFile.delete()
        }
        val mainFolder = File(this.filesDir, "Bars/${pref!!.getBarID()}")
        var totalFiles = mainFolder.listFiles()
        for (x in totalFiles!!.indices) {
            var params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f
            )
            if (x != imageList!!.size - 1) {
                params.updateMargins(0, 0, 10, 0)
            }
            val newlayout: LinearLayout = LinearLayout(this)
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
                newlayout.tag = imageList!!.size+x+1
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

    private fun checkImage(
        url:String,
        layout: LinearLayout,
        index:Int
    ) {
        val myDir = File(this.filesDir, "BarImages/${pref!!.getBarID()}")
        if (myDir.exists()) {
            var imageFile = File(myDir, "image$index.png")
            if (imageFile.exists()) {
                var b: Bitmap = BitmapFactory.decodeStream(FileInputStream(imageFile))
                val scaledBitmap = b?.let { Bitmap.createScaledBitmap(b ,100, 100, false) }
//                val d: Drawable = Drawable.createFromPath(imageFile.absolutePath)!!
                var dr: Drawable = BitmapDrawable(this!!.resources, scaledBitmap)
                layout!!.background = dr
                layout.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        deleteImage(v!!.tag as Int)
                    }

                })

                    BackgroundDownloadTask(url, index, layout!!, myDir).execute()

            } else {
                BackgroundDownloadTask(url, index, layout!!, myDir).execute()
            }

        } else {
            myDir.mkdirs()
            BackgroundDownloadTask(url, index, layout!!, myDir).execute()
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

            imageUri!!.add(data?.data!!)
            addImage(imageUri)
        }else if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            latlng = data!!.getParcelableExtra("latlng")
            address = data.getStringExtra("addressLine")
            location!!.setText(address)
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

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.imageLayout -> {
                checkPermissionForImage()
            }
            R.id.save_changes -> {
                spinKitView!!.visibility=View.VISIBLE
                val newTitle = title!!.text.toString()
                val newLocation = location!!.text.toString()
                val newDescription = description!!.text.toString()
                val newDiscount: Int = (discount!!.text.toString()).toInt()
                val newFreeDrink: Int = (freeDrink!!.text.toString()).toInt()
                val geoPoint: GeoPoint = GeoPoint(latlng!!.latitude, latlng!!.longitude)
                val hashMap = hashMapOf(
                    "title" to newTitle,
                    "barLocation" to newLocation,
                    "description" to newDescription,
                    "discount" to newDiscount,
                    "freeDrinkAfter" to newFreeDrink,
                    "location" to geoPoint
                )
                db!!.collection("Bars").document(pref!!.getBarID()!!)
                    .update(hashMap as Map<String, Any>).addOnSuccessListener {
                        var mStorageRef: StorageReference = FirebaseStorage.getInstance().reference
                        var stoRef: StorageReference
                        if (imageUri!!.size>0) {
                            for (x in 0 until imageUri!!.size) {
                                val index = imageList!!.size + x + 1
                                stoRef = mStorageRef.child("BarImages/${pref!!.getBarID()}/image$index.png")
                                val uploadTask = stoRef.putFile(imageUri!![x])
                                uploadTask.addOnSuccessListener { taskSnapshot ->

                                    taskSnapshot!!.storage.downloadUrl.addOnCompleteListener(
                                        OnCompleteListener { task ->

                                            imageList!!.add(task.result.toString())
                                            val urlHash = hashMapOf("imagesUrl" to imageList)
                                            db!!.collection("Bars").document(pref!!.getBarID()!!)
                                                .update(urlHash as Map<String, Any>).addOnSuccessListener {
                                                    if(index==(imageUri!!.size+imageList!!.size)-1){
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
            R.id.back_button->{
                onBackPressed()
            }
            R.id.locationText->{
                var i: Intent = Intent(this, UsersMapsActivity::class.java)
                startActivityForResult(i, 1)
            }
        }
    }

    inner class SomeTask(val url: String) :
        AsyncTask<String, URL, Bitmap>() {

        override fun doInBackground(vararg params: String?): Bitmap {
            val bitmap = getBitmapFromURL(url)
            val scaledBitmap = bitmap?.let { Bitmap.createScaledBitmap(it, 739, 415, false) }
            return scaledBitmap!!
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            var dr: Drawable = BitmapDrawable(this@BarAddEditProfileActivity!!.resources, result)

            imageLayout!!.background = dr
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

            var dr: Drawable = BitmapDrawable(this@BarAddEditProfileActivity.resources, result)

            if (position == 0) {
                layout!!.background = dr
            }
        }

    }
}

