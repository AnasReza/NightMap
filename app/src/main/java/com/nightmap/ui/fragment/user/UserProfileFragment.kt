package com.nightmap.ui.fragment.user

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.nightmap.R
import com.nightmap.ui.activity.ChangeNumberActivity
import com.nightmap.ui.activity.TextActivity
import com.nightmap.ui.activity.bar_owner.BarHomeActivity
import com.nightmap.ui.activity.user.UserLogin
import com.nightmap.utility.Preferences

class UserProfileFragment : Fragment(), View.OnClickListener {
    private var terms_condition: TextView? = null
    private var privacy_policy: TextView? = null
    private var change_number: TextView? = null
    private var log_out: TextView? = null
    private var profileName: TextView? = null
    private var aboutText: EditText? = null
    private var pref: Preferences? = null
    private var profileImage: ImageView? = null
    private var db: FirebaseFirestore? = null
    private var auth:FirebaseAuth?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_user_profile, container, false)
        init(view)
        return view
    }

    private fun init(view: View?) {
        db = Firebase.firestore
        auth=FirebaseAuth.getInstance()
        pref = Preferences(activity!!.applicationContext)
        terms_condition = view!!.findViewById(R.id.terms_condition)
        privacy_policy = view!!.findViewById(R.id.privacy_policy)
        change_number = view!!.findViewById(R.id.change_number)
        log_out = view!!.findViewById(R.id.log_out)
        profileImage = view!!.findViewById(R.id.profileImage)
        profileName = view!!.findViewById(R.id.profile_name)
        aboutText = view!!.findViewById(R.id.about_text)

        terms_condition!!.setOnClickListener(this)
        privacy_policy!!.setOnClickListener(this)
        change_number!!.setOnClickListener(this)
        log_out!!.setOnClickListener(this)
        profileImage!!.setOnClickListener(this)

        db!!.collection("User").document(pref!!.getUserID().toString()).get()
            .addOnSuccessListener { documentSnapshot ->
                val url: String = documentSnapshot.get("profileImage").toString()
                val about: String = documentSnapshot.get("about").toString()
                val name = documentSnapshot.get("fullName").toString()

                profileName!!.text = name
                if (url != "") {
                    var params = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT
                    )

                    profileImage!!.layoutParams = params
                    profileImage!!.background = null
                    Glide.with(activity!!).asBitmap().load(url)
                        .apply(RequestOptions.circleCropTransform())
                        .into(profileImage!!)
                }
                if (about != "") {
                    aboutText!!.setText(about)
                }
            }

        aboutText!!.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE|| event.keyCode==KeyEvent.KEYCODE_ENTER) {
                aboutText!!.clearFocus()
                Log.e("Anas","ENTER BUTTON")
                val hashMap = hashMapOf("about" to aboutText!!.text.toString())

                db!!.collection("User").document(pref!!.getUserID().toString())
                    .update(hashMap as Map<String, Any>)
            }
            true
        }
    }

    private fun checkPermissionForImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((activity!!.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                && (activity!!.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
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
            Glide.with(activity!!).asBitmap().load(data?.data)
                .apply(RequestOptions.circleCropTransform())
                .into(profileImage!!)

            var mStorageRef: StorageReference = FirebaseStorage.getInstance().reference
            var stoRef: StorageReference
            stoRef = mStorageRef.child("Images/${pref!!.getUserID()}/profile.png")
            val uploadTask = stoRef.putFile(data?.data!!)
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

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.privacy_policy -> {
                activity!!.startActivity(
                    Intent(
                        activity!!,
                        TextActivity::class.java
                    ).putExtra("headline", "PRIVACY POLICY")
                )
            }
            R.id.terms_condition -> {
                activity!!.startActivity(
                    Intent(
                        activity!!,
                        TextActivity::class.java
                    ).putExtra("headline", "TERMS OF USAGE")
                )
            }
            R.id.change_number -> {
                activity!!.startActivity(
                    Intent(
                        activity!!,
                        ChangeNumberActivity::class.java
                    )
                )
            }
            R.id.log_out -> {
                pref!!.setUserID("")
                pref!!.setLogin(false)
                pref!!.setUserType("")
                auth!!.signOut()
                activity!!.finishAffinity()
                activity!!.startActivity(Intent(activity, UserLogin::class.java))
                activity!!.finish()

            }
            R.id.profileImage -> {
                checkPermissionForImage()
            }
        }
    }
}