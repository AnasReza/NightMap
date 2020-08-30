package com.nightmap.ui.activity.user

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.nightmap.R
import com.nightmap.utility.Preferences
import kotlinx.android.synthetic.main.fragment_user_profile.*
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class UserEditProfile : AppCompatActivity(), View.OnClickListener {
    private var imageView: ImageView? = null
    private var description: EditText? = null
    private var save_button: Button? = null
    private var profile_name: TextView? = null

    private var db: FirebaseFirestore? = null
    private var pref: Preferences? = null
    private var mainURI: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_edit_profile)
        init()
    }

    private fun init() {
        db = Firebase.firestore
        pref = Preferences(this)

        imageView = findViewById(R.id.profileImage)
        description = findViewById(R.id.description_edit_text)
        save_button = findViewById(R.id.save_changes)
        profile_name = findViewById(R.id.profile_name)

        db!!.collection("User").document(pref!!.getUserID()!!).get()
            .addOnSuccessListener { documentSnapshot ->
                val url: String = documentSnapshot.get("profileImage").toString()
                val about: String = documentSnapshot.get("about").toString()
                val name = documentSnapshot.get("fullName").toString()

                profile_name!!.text = name
                if (about != "") {
                    description!!.setText(about)
                }
                if (url != "") {
                    var params = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT
                    )

                    imageView!!.layoutParams = params
                    imageView!!.background = null

                    userImage(url, imageView!!, pref!!.getUserID()!!)
                }

            }

        save_button!!.setOnClickListener(this)
        imageView!!.setOnClickListener(this)
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

    private fun userImage(
        url: String,
        layout: ImageView,
        id: String
    ) {
        val myDir = File(this.filesDir, "UserImage/$id")
        if (myDir.exists()) {
            var imageFile = File(myDir, "image0.png")
            if (imageFile.exists()) {
                var b: Bitmap = BitmapFactory.decodeStream(FileInputStream(imageFile))
                Glide.with(this).asBitmap().load(b)
                    .apply(RequestOptions.circleCropTransform())
                    .into(layout)

                BackgroundUserTask(url, layout, myDir).execute()

            } else {
                BackgroundUserTask(url, layout, myDir).execute()
            }

        } else {
            myDir.mkdirs()

            BackgroundUserTask(url, layout, myDir).execute()

        }
    }

    private fun checkPermissionForImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                && (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
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

            profileImage!!.layoutParams = params
            // bar_image!!.setImageURI(data?.data)
            // profileImage!!.visibility = View.GONE
            // profileImage!!.setImageURI(data?.data)
            profileImage!!.background = null

            Glide.with(this@UserEditProfile).asBitmap().load(data?.data)
                .apply(RequestOptions.circleCropTransform())
                .into(profileImage!!)
            mainURI = data?.data


        }
    }


    inner class BackgroundUserTask(
        val url: String,
        val layout: ImageView,
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

            val fos = FileOutputStream("${mainFile.absolutePath}${File.separator}image0.png")

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
            Glide.with(this@UserEditProfile).asBitmap().load(result)
                .apply(RequestOptions.circleCropTransform())
                .into(layout)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.profileImage -> {
                checkPermissionForImage()
            }
            R.id.save_changes -> {

                updateUser()
            }
        }
    }

    private fun updateUser() {
        val desc = description!!.text.toString()
        val updateHash = hashMapOf("about" to desc)
        db!!.collection("User").document(pref!!.getUserID()!!)
            .update(updateHash as Map<String, Any>).addOnSuccessListener {
                Toast.makeText(
                    this@UserEditProfile,
                    "Your Profile has been updated",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        if (mainURI != null) {
            var mStorageRef: StorageReference = FirebaseStorage.getInstance().reference
            var stoRef: StorageReference
            stoRef = mStorageRef.child("Images/${pref!!.getUserID()}/profile.png")
            val uploadTask = stoRef.putFile(mainURI!!)
            uploadTask.addOnSuccessListener { taskSnapshot ->
                taskSnapshot!!.storage.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->

                    val newURL = task.result.toString()
                    val newHashMap = hashMapOf("profileImage" to newURL)
                    db!!.collection("User").document(pref!!.getUserID().toString()).update(
                        newHashMap as Map<String, Any>
                    )
                    db!!.collection("Rating").get().addOnSuccessListener { documents ->
                        val uid = pref!!.getUserID()
                        for (document in documents) {
                            if (uid == document.get("uid")) {
                                val docID = document.id
                                val hashMap = hashMapOf("imagesURL" to newURL)
                                db!!.collection("Rating").document(docID)
                                    .update(hashMap as Map<String, Any>)
                            }
                        }
                    }

                })
            }
        }
    }
}