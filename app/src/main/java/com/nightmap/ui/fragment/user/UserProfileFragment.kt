package com.nightmap.ui.fragment.user

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.ui.activity.ChangeNumberActivity
import com.nightmap.ui.activity.RegisterAsActivity
import com.nightmap.ui.activity.TextActivity
import com.nightmap.ui.activity.user.UserEditProfile
import com.nightmap.utility.Preferences
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class UserProfileFragment : Fragment(), View.OnClickListener {
    private var terms_condition: TextView? = null
    private var privacy_policy: TextView? = null
    private var change_number: TextView? = null
    private var edit_profile: TextView? = null
    private var show_location: SwitchCompat? = null
    private var shareApp: TextView? = null
    private var log_out: TextView? = null
    private var profileName: TextView? = null
    private var aboutText: EditText? = null
    private var pref: Preferences? = null
    private var profileImage: ImageView? = null
    private var db: FirebaseFirestore? = null
    private var auth: FirebaseAuth? = null
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
        auth = FirebaseAuth.getInstance()
        pref = Preferences(activity!!.applicationContext)
        terms_condition = view!!.findViewById(R.id.terms_condition)
        privacy_policy = view!!.findViewById(R.id.privacy_policy)
        change_number = view!!.findViewById(R.id.change_number)
        edit_profile = view!!.findViewById(R.id.edit_profile)
        log_out = view!!.findViewById(R.id.log_out)
        profileImage = view!!.findViewById(R.id.profileImage)
        profileName = view!!.findViewById(R.id.profile_name)
        aboutText = view!!.findViewById(R.id.about_text)
        show_location = view!!.findViewById(R.id.show_location)
        shareApp = view!!.findViewById(R.id.shareApp)

        terms_condition!!.setOnClickListener(this)
        privacy_policy!!.setOnClickListener(this)
        change_number!!.setOnClickListener(this)
        log_out!!.setOnClickListener(this)
        profileImage!!.setOnClickListener(this)
        shareApp!!.setOnClickListener(this)
        edit_profile!!.setOnClickListener(this)

        db!!.collection("User").document(pref!!.getUserID().toString()).get()
            .addOnSuccessListener { documentSnapshot ->
                val url: String = documentSnapshot.get("profileImage").toString()
                val about: String = documentSnapshot.get("about").toString()
                val name = documentSnapshot.get("fullName").toString()
                val showLocationCheck: Boolean = documentSnapshot.get("showLocation") as Boolean

                profileName!!.text = name
                show_location!!.isChecked = showLocationCheck
                if (url != "") {
                    var params = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT
                    )

                    profileImage!!.layoutParams = params
                    profileImage!!.background = null

                    userImage(url, profileImage!!, pref!!.getUserID()!!)
                }
                if (about != "") {
                    aboutText!!.setText(about)
                }
                show_location!!.setOnCheckedChangeListener { buttonView, isChecked ->
                    val hashMap = hashMapOf("showLocation" to isChecked)
                    db!!.collection("User").document(pref!!.getUserID()!!)
                        .update(hashMap as Map<String, Any>)
                        .addOnSuccessListener {
                            Log.e("Anas", "show location is updated")
//                            if (isChecked) {
//                                db!!.collection("User").document(pref!!.getUserID()!!)
//                                    .collection("Friends").get().addOnSuccessListener { documents ->
//
//                                        if (documents.size() != 0) {
//                                            for (doc in documents) {
//
//                                                val notiHash =
//                                                    hashMapOf(
//                                                        "addedOn" to FieldValue.serverTimestamp(),
//                                                        "barId" to "",
//                                                        "body" to "${pref!!.getUserName()} just shared its location with you.",
//                                                        "status" to "",
//                                                        "title" to "Location Shared",
//                                                        "type" to "Location Shared",
//                                                        "uid" to doc.id.toString()
//                                                    )
//                                                db!!.collection("Notifications").add(notiHash)
//                                            }
//
//                                        }
//                                        Log.d(
//                                            "Anas",
//                                            "Inside Friends collections ${documents.size()}"
//                                        )
//                                    }
//                            }
                        }
                }
            }


    }

    private fun userImage(
        url: String,
        layout: ImageView,
        id: String
    ) {
        val myDir = File(activity!!.filesDir, "UserImage/$id")
        if (myDir.exists()) {
            var imageFile = File(myDir, "image0.png")
            if (imageFile.exists()) {
                var b: Bitmap = BitmapFactory.decodeStream(FileInputStream(imageFile))
                Glide.with(activity!!).asBitmap().load(b)
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
            R.id.shareApp -> {
                try {
                    var shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.setType("text/plain")
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Night Map")
                    var message = "\nLet me recommend you this application\n\n"
                    message =
                        "$message https://play.google.com/store/apps/details?id=com.Elroye.NightMap"
                    shareIntent.putExtra(Intent.EXTRA_TEXT, message)
                    activity!!.startActivity(Intent.createChooser(shareIntent, "Choose Any"))

                } catch (e: Exception) {
                    //e.toString();
                }
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
//                val hashMap = hashMapOf("phoneNumber" to "")
//                    db!!.collection("User").document(pref!!.getUserID()!!)
//                        .update(hashMap as Map<String, Any>).addOnSuccessListener {
//                            pref!!.setUserID("")
//                            pref!!.setLogin(false)
//                            pref!!.setUserType("")
//                            auth!!.signOut()
//
//                            activity!!.startActivity(Intent(activity, RegisterAsActivity::class.java))
//                            activity!!.finishAffinity()
//                        }

                val auth = FirebaseAuth.getInstance()
                val user = auth.currentUser
                try {
                                pref!!.setUserID("")
                                pref!!.setLogin(false)
                                pref!!.setUserType("")
                                auth!!.signOut()

                                activity!!.startActivity(
                                    Intent(
                                        activity,
                                        RegisterAsActivity::class.java
                                    )
                                )
                                activity!!.finishAffinity()




                } catch (e: NullPointerException) {

                }

            }
            R.id.edit_profile -> {
                activity!!.startActivity(Intent(activity!!, UserEditProfile::class.java))
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
            if (activity != null) {
                Glide.with(activity!!).asBitmap().load(result)
                    .apply(RequestOptions.circleCropTransform())
                    .into(layout)
            }

        }
    }
}