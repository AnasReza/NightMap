package com.nightmap.ui.fragment.bar_owner

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.ui.activity.ChangeNumberActivity
import com.nightmap.ui.activity.TextActivity
import com.nightmap.ui.activity.bar_owner.BarAddEditProfileActivity
import com.nightmap.ui.activity.user.UserLogin
import com.nightmap.utility.Preferences

class BarSettingsFragment : Fragment(), View.OnClickListener {
    private var policy: TextView? = null
    private var terms: TextView? = null
    private var log_out: TextView? = null
    private var change_number: TextView? = null
    private var edit_profile: TextView? = null
    private var profile_image: ImageView? = null
    private var profile_name: TextView? = null
    private var description: TextView? = null
    private var pref: Preferences? = null
    private var db: FirebaseFirestore? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_bar_settings, container, false)
        init(view)
        return view
    }

    private fun init(view: View?) {
        db = Firebase.firestore
        pref = Preferences(activity!!.applicationContext)
        policy = view!!.findViewById(R.id.privacy_policy)
        terms = view!!.findViewById(R.id.terms_condition)
        change_number = view!!.findViewById(R.id.change_number)
        edit_profile = view!!.findViewById(R.id.edit_profile)
        log_out = view!!.findViewById(R.id.log_out)
        profile_image = view!!.findViewById(R.id.profileImage)
        profile_name = view!!.findViewById(R.id.profile_name)
        description = view!!.findViewById(R.id.description)

        policy!!.setOnClickListener(this)
        terms!!.setOnClickListener(this)
        change_number!!.setOnClickListener(this)
        edit_profile!!.setOnClickListener(this)
        log_out!!.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        db!!.collection("Bars").document(pref!!.getBarID()!!).get()
            .addOnSuccessListener { documentSnapshot ->
                val urls:ArrayList<String> = documentSnapshot.get("imagesURL") as ArrayList<String>
                Glide.with(activity!!).asBitmap().load(urls[0])
                    .apply(RequestOptions.circleCropTransform())
                    .into(profile_image!!)
                profile_name!!.text= documentSnapshot.get("fullName").toString()
                description!!.text= documentSnapshot.get("description").toString()
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
                activity!!.startActivity(Intent(activity!!, ChangeNumberActivity::class.java))
            }
            R.id.edit_profile -> {
                activity!!.startActivity(Intent(activity!!, BarAddEditProfileActivity::class.java))
            }
            R.id.log_out -> {
                pref!!.setUserID("")
                pref!!.setLogin(false)
                pref!!.setUserType("")
                activity!!.finishAffinity()
                activity!!.startActivity(Intent(activity, UserLogin::class.java))
                activity!!.finish()
            }
        }
    }
}