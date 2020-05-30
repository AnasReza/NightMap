package com.nightmap.ui.activity.bar_owner

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.nightmap.R
import com.nightmap.utility.Preferences
import com.squareup.picasso.Picasso
import java.io.IOException
import java.io.InputStream
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

    private var db: FirebaseFirestore? = null
    private var pref: Preferences? = null
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_add_new)
        init()
    }

    private fun init() {
        db = Firebase.firestore
        pref = Preferences(this)

        title = findViewById(R.id.titleText)
        location = findViewById(R.id.locationText)
        description = findViewById(R.id.description)
        discount = findViewById(R.id.discountText)
        freeDrink = findViewById(R.id.freeDrink)
        imageLayout = findViewById(R.id.imageLayout)
        save_changes = findViewById(R.id.save_changes)

        db!!.collection("Bars").document(pref!!.getBarID()!!).get()
            .addOnSuccessListener { documentSnapshot ->
                val urls: ArrayList<String> = documentSnapshot.get("imagesURL") as ArrayList<String>
                title!!.setText(documentSnapshot.get("title").toString())
                location!!.setText(documentSnapshot.get("barLocation").toString())
                description!!.setText(documentSnapshot.get("description").toString())
                discount!!.setText(documentSnapshot.get("discount").toString())
                freeDrink!!.setText(documentSnapshot.get("freeDrinkAfter").toString())

                SomeTask(urls[0]).execute()
            }
        imageLayout!!.setOnClickListener(this)
        save_changes!!.setOnClickListener(this)
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
            var inputSream: InputStream = contentResolver.openInputStream(data?.data!!)!!
            var dr: Drawable = Drawable.createFromStream(inputSream, data?.data.toString())
            imageLayout!!.background = dr
            imageUri = data?.data
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
                val newTitle = title!!.text.toString()
                val newLocation = location!!.text.toString()
                val newDescription = description!!.text.toString()
                val newDiscount: Int = (discount!!.text.toString()).toInt()
                val newFreeDrink: Int = (freeDrink!!.text.toString()).toInt()
                val hashMap = hashMapOf(
                    "title" to newTitle,
                    "barLocation" to newLocation,
                    "description" to newDescription,
                    "discount" to newDiscount,
                    "freeDrinkAfter" to newFreeDrink
                )
                db!!.collection("Bars").document(pref!!.getBarID()!!)
                    .update(hashMap as Map<String, Any>).addOnSuccessListener {
                        if(imageUri!=null){
                            var mStorageRef: StorageReference = FirebaseStorage.getInstance().reference
                            var stoRef: StorageReference
                            stoRef = mStorageRef.child("BarImages/${pref!!.getBarID()}/$newTitle.png")
                            val uploadTask = stoRef.putFile(imageUri!!)
                            uploadTask.addOnSuccessListener { taskSnapshot ->
                                taskSnapshot!!.storage.downloadUrl.addOnCompleteListener(
                                    OnCompleteListener { task ->
                                        val file: java.util.ArrayList<String> = arrayListOf<String>()
                                        file.add(task.result.toString())
                                        val newHashMap= hashMapOf("imagesURL" to file)
                                        db!!.collection("Bars").document(pref!!.getBarID()!!).update(
                                            newHashMap as Map<String, Any>
                                        ).addOnSuccessListener { Log.i("Anas","BAR UPDATED") }


                                    })
                            }

                        }

                    }
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


}