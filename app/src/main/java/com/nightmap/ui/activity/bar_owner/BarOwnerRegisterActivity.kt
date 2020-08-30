package com.nightmap.ui.activity.bar_owner

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.set
import androidx.core.view.updateMargins
import com.github.ybq.android.spinkit.SpinKitView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.nightmap.R
import com.nightmap.ui.activity.TextActivity
import com.nightmap.ui.activity.UsersMapsActivity
import com.nightmap.utility.Preferences
import java.io.File

class BarOwnerRegisterActivity : AppCompatActivity(), View.OnClickListener {
    private var barRegister: Button? = null
    private var bar_image: ImageView? = null
    private var upload_bar: LinearLayout? = null
    private var mainImageView: RelativeLayout? = null
    private var full_name_text: TextView? = null
    private var email_text: TextView? = null
    private var password_text: TextView? = null
    private var confirm_password_text: TextView? = null
    private var bar_name_text: TextView? = null
    private var bar_location_text: TextView? = null
    private var description_text: TextView? = null
    private var discount_text: TextView? = null
    private var free_drink_text: TextView? = null
    private var textView: TextView? = null
    private var back_button: ImageView? = null
    private var spin_kit: SpinKitView? = null

    private var uriList: ArrayList<Uri>? = null
    private var locationManager: LocationManager? = null
    private var pref: Preferences? = null
    private var auth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var check: Boolean = true
    private var nameString: String = ""
    private var emailString: String = ""
    private var passwordString: String = ""
    private var confirmPasswordString: String = ""
    private var barString: String = ""
    private var locationString: String = ""
    private var descriptionString: String = ""
    private var discountString: Int = 0
    private var freeDrinkString: Int = 0
    private var latlng: LatLng? = null
    private var address: String = ""

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
        email_text = findViewById(R.id.email_text)
        password_text = findViewById(R.id.password_text)
        confirm_password_text = findViewById(R.id.confirm_password_text)
        bar_name_text = findViewById(R.id.bar_name_text)
        bar_location_text = findViewById(R.id.bar_location_text)
        description_text = findViewById(R.id.description_text)
        discount_text = findViewById(R.id.discount_text)
        free_drink_text = findViewById(R.id.free_drink_text)
        mainImageView = findViewById(R.id.mainImageLayout)
        textView = findViewById(R.id.textView)
        back_button = findViewById(R.id.back_button)
        spin_kit = findViewById(R.id.spin_kit)

        pref = Preferences(this)
        auth = FirebaseAuth.getInstance()
        uriList = ArrayList()
        db = Firebase.firestore
        latlng = LatLng(0.0, 0.0)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        bar_image!!.setOnClickListener(this)
        barRegister!!.setOnClickListener(this)
        bar_location_text!!.setOnClickListener(this)
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
                        this@BarOwnerRegisterActivity,
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
                        this@BarOwnerRegisterActivity,
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

                val newlayout: RelativeLayout = RelativeLayout(this@BarOwnerRegisterActivity)

                val f = File(getRealPathFromURI(data.data!!))
                val d: Drawable = Drawable.createFromPath(f.absolutePath)!!
                newlayout.layoutParams = params
                newlayout.background = d

                upload_bar!!.addView(newlayout)
            } else if (uriList!!.size > 0) {
                addImage(data.data)

            }

        } else if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            latlng = data!!.getParcelableExtra("latlng")
            address = data.getStringExtra("addressLine")
            bar_location_text!!.text = address
        }
    }

    private fun addImage(data: Uri?) {
        upload_bar!!.removeAllViewsInLayout()
        upload_bar!!.weightSum = uriList!!.size.toFloat()

        var index = 0
        for (x in 0 until uriList!!.size) {
            Log.e("Anas", "${uriList!![x]}")
            index = x
            var params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f
            )
            if (x != uriList!!.size - 1) {
                params.updateMargins(0, 0, 10, 0)
            }

            val newlayout: LinearLayout = LinearLayout(this@BarOwnerRegisterActivity)
            newlayout.tag = index
            val f = File(getRealPathFromURI(uriList!![x]))

            var d: Drawable
            if (Drawable.createFromPath(f.absolutePath) == null) {
                Log.i("Anas", "Drawable is null")
                Log.d("Anas", "${f.absolutePath} absolute path")
                val bmOptions: BitmapFactory.Options = BitmapFactory.Options()
                bmOptions.inSampleSize = 2
                val bitmap: Bitmap = BitmapFactory.decodeFile(f.absolutePath, bmOptions)
                d = BitmapDrawable(this.resources, bitmap)
            } else {
                Log.i("Anas", "Drawable is not null")
                d = Drawable.createFromPath(f.absolutePath)!!
            }

            newlayout.layoutParams = params

            newlayout.background = d
            newlayout.setOnClickListener { v ->
                Log.i("Anas", "${v!!.tag} tag of pressed image")
                deleteImage(v.tag as Int)
            }

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

            val newlayout: LinearLayout = LinearLayout(this@BarOwnerRegisterActivity)
            newlayout.tag = index
            val f = File(getRealPathFromURI(uriList!![x]))
            val d: Drawable = Drawable.createFromPath(f.absolutePath)!!
            newlayout.layoutParams = params

            newlayout.background = d
            newlayout.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    Log.i("Anas", "${v!!.tag} tag of pressed image")
                    deleteImage(v.tag as Int)
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

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.user_register -> {
                if (uriList!!.size != 0) {
                    registerInFirebase()
                } else {
                    val builder = AlertDialog.Builder(this@BarOwnerRegisterActivity)
                    builder.setTitle("Error")
                    builder.setMessage("Please Upload atleast 1 Image.")
                    builder.setPositiveButton("Okay") { dialog, which ->
                        dialog.dismiss()

                    }
                    builder.show()
                }


            }
            R.id.bar_image -> {
                if (uriList!!.size < 5) {
                    checkPermissionForImage()
                } else {
                    val builder = AlertDialog.Builder(this@BarOwnerRegisterActivity)
                    builder.setTitle("Error")
                    builder.setMessage("You can only upload maximum of 5 images.")
                    builder.setPositiveButton("Okay") { dialog, which ->
                        dialog.dismiss()

                    }
                    builder.show()
                }

            }
            R.id.bar_location_text -> {
                var i: Intent = Intent(this, UsersMapsActivity::class.java)
                startActivityForResult(i, 1)
            }
        }
    }

    private fun registerInFirebase() {

        nameString = full_name_text!!.text.toString()
        emailString = email_text!!.text.toString()
        passwordString = password_text!!.text.toString()
        confirmPasswordString = confirm_password_text!!.text.toString()
        barString = bar_name_text!!.text.toString()
        locationString = bar_location_text!!.text.toString()
        descriptionString = description_text!!.text.toString()
        discountString = (discount_text!!.text.toString()).toInt()
        freeDrinkString = (free_drink_text!!.text.toString()).toInt()
        if (uriList!!.size != 0 && nameString.isNotEmpty()
            && emailString.isNotEmpty()
            && passwordString.isNotEmpty()
            && confirmPasswordString.isNotEmpty()
            && barString.isNotEmpty()
            && locationString.isNotEmpty()
            && descriptionString.isNotEmpty()
            && bar_location_text!!.text.isNotEmpty()
        ) {
            if (passwordString == confirmPasswordString) {
                spin_kit!!.visibility = View.VISIBLE
                auth!!.createUserWithEmailAndPassword(emailString, passwordString)
                    .addOnCompleteListener { task ->
                        try {
                            val user = task.result?.user
                            val id = user!!.uid
                            val geoPoint: GeoPoint = GeoPoint(latlng!!.latitude, latlng!!.longitude)
                            val userHash = hashMapOf(
                                "addedBy" to "",
                                "addedOn" to FieldValue.serverTimestamp(),
                                "averageAtmosphere" to 0.0,
                                "averageCrowded" to 0.0,
                                "averageGenderRatio" to 0.0,
                                "averageMusic" to 0.0,
                                "fullName" to nameString,
                                "barAdminEmail" to emailString,
                                "barId" to id,
                                "status" to "pending",
                                "title" to barString,
                                "description" to descriptionString,
                                "discount" to discountString,
                                "freeDrinkAfter" to freeDrinkString,
                                "location" to geoPoint,
                                "barLocation" to bar_location_text!!.text,
                                "todayReviews" to 0.0,
                                "totalReviews" to 0.0
                            )
                            db!!.collection("Bars").document(id).set(userHash)
                                .addOnSuccessListener { documentReference ->


                                    val newHash = hashMapOf("uid" to id, "userType" to "BarOwner")
                                    db!!.collection("Users").document(id).set(newHash)
                                        .addOnSuccessListener { documentReference ->
                                            FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
                                                if (!task.isSuccessful) {
                                                    task.exception!!.printStackTrace()
                                                    return@addOnCompleteListener
                                                }

                                                val token = task.result?.token
                                                val tokenMap =
                                                    hashMapOf("token" to token)
                                                db!!.collection("Bars")
                                                    .document(id)
                                                    .update(tokenMap as Map<String, Any>)
                                                    .addOnSuccessListener {

//                                                    val notiHash =
//                                                        hashMapOf(
//                                                            "addedOn" to FieldValue.serverTimestamp(),
//                                                            "barId" to id,
//                                                            "body" to "$barString requires your approval before going to public to people.",
//                                                            "title" to "Bar Approval",
//                                                            "type" to "Bar Approval",
//                                                            "uid" to ""
//                                                        )
//                                                    db!!.collection("Notifications")
//                                                        .add(notiHash)
//                                                        .addOnSuccessListener {
//
//                                                        }
                                                    }
                                            }


                                            var mStorageRef: StorageReference =
                                                FirebaseStorage.getInstance().reference
                                            var stoRef: StorageReference
                                            val file: ArrayList<String> = arrayListOf<String>()
                                            for (x in 0 until uriList!!.size) {
                                                stoRef =
                                                    mStorageRef.child("BarImages/$id/$barString$x.png")
                                                val uploadTask = stoRef.putFile(uriList!![x])
                                                uploadTask.addOnSuccessListener { taskSnapshot ->
                                                    taskSnapshot!!.storage.downloadUrl.addOnCompleteListener(
                                                        OnCompleteListener { task ->
                                                            Log.d(
                                                                "Anas",
                                                                "${task.result.toString()} link of image stored in storage"
                                                            )
                                                            file.add(task.result.toString())
                                                            val newHashMap = hashMapOf(
                                                                "imagesURL" to file
                                                            )
                                                            db!!.collection("Bars").document(id)
                                                                .update(newHashMap as Map<String, Any>)
                                                                .addOnSuccessListener {
                                                                    if (x == uriList!!.size - 1) {
                                                                        spin_kit!!.visibility =
                                                                            View.GONE
                                                                        pref!!.setFirstCheck(
                                                                            true
                                                                        )
                                                                        pref!!.setBarID(
                                                                            id
                                                                        )
                                                                        pref!!.setLogin(
                                                                            true
                                                                        )
                                                                        pref!!.setUserType(
                                                                            "bar"
                                                                        )
                                                                        pref!!.setBarPassword(
                                                                            passwordString
                                                                        )
                                                                        startActivity(
                                                                            Intent(
                                                                                this,
                                                                                BarApprovalActivity::class.java
                                                                            )
                                                                        )
                                                                        finish()
                                                                    }

                                                                }
                                                        })
                                                }
                                            }


                                        }
                                }
                                .addOnFailureListener { e ->
                                    Log.w("Anas", "Error adding document", e)
                                }
                        } catch (e: Exception) {
                            spin_kit!!.visibility=View.GONE
                            e.printStackTrace()
                            Toast.makeText(
                                this@BarOwnerRegisterActivity,
                                "This Email id has already been taken.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                    }

            }else{
                Toast.makeText(this, "Password does not match.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Please fill out all the forms", Toast.LENGTH_SHORT).show()
        }

    }


}