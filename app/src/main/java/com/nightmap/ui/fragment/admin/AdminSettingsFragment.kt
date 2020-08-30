package com.nightmap.ui.fragment.admin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.nightmap.R
import com.nightmap.ui.activity.RegisterAsActivity
import com.nightmap.ui.activity.TextActivity
import com.nightmap.ui.activity.admin.AdminChangeEmailActivity
import com.nightmap.ui.activity.admin.AdminChangePasswordActivity
import com.nightmap.utility.Preferences

class AdminSettingsFragment : Fragment(), View.OnClickListener {
    private var change_email: TextView? = null
    private var change_password: TextView? = null
    private var log_out: TextView? = null
    private var privacy: TextView? = null
    private var terms: TextView? = null
    private var pref: Preferences? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_admin_settings, container, false)
        init(view)
        return view
    }

    private fun init(view: View?) {
        pref = Preferences(activity!!)
        change_email = view!!.findViewById(R.id.change_email)
        change_password = view!!.findViewById(R.id.change_password)
        log_out = view!!.findViewById(R.id.log_out)
        terms = view!!.findViewById(R.id.terms_condition)
        privacy = view!!.findViewById(R.id.privacy_policy)

        change_email!!.setOnClickListener(this)
        change_password!!.setOnClickListener(this)
        log_out!!.setOnClickListener(this)
        terms!!.setOnClickListener(this)
        privacy!!.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.change_email -> {
                activity!!.startActivity(Intent(activity!!, AdminChangeEmailActivity::class.java))
            }
            R.id.change_password -> {
                activity!!.startActivity(
                    Intent(
                        activity!!,
                        AdminChangePasswordActivity::class.java
                    )
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
            R.id.privacy_policy -> {
                activity!!.startActivity(
                    Intent(
                        activity!!,
                        TextActivity::class.java
                    ).putExtra("headline", "PRIVACY POLICY")
                )
            }
            R.id.log_out -> {
                var auth = FirebaseAuth.getInstance()
                val user = auth.currentUser
                var credential: AuthCredential = EmailAuthProvider.getCredential(
                    user!!.email!!,
                    pref!!.getAdminPassword()!!
                )
                user.reauthenticate(credential).addOnSuccessListener {
                    user.delete().addOnSuccessListener {  auth.signOut()

                        pref!!.setLogin(false)
                        pref!!.setUserType("")

                        pref!!.setAdminID("")
                        pref!!.setAdminPassword("")

                        activity!!.startActivity(Intent(activity, RegisterAsActivity::class.java))
                        activity!!.finishAffinity() }


                }


            }
        }
    }
}