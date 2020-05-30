package com.nightmap.ui.fragment.admin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.nightmap.R
import com.nightmap.ui.activity.TextActivity
import com.nightmap.ui.activity.admin.AdminChangeEmailActivity

class AdminSettingsFragment : Fragment(), View.OnClickListener {
    private var change_email: TextView? = null
    private var privacy: TextView? = null
    private var terms: TextView? = null
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
        change_email = view!!.findViewById(R.id.change_email)
        terms = view!!.findViewById(R.id.terms_condition)
        privacy = view!!.findViewById(R.id.privacy_policy)

        change_email!!.setOnClickListener(this)
        terms!!.setOnClickListener(this)
        privacy!!.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.change_email -> {
                activity!!.startActivity(Intent(activity!!, AdminChangeEmailActivity::class.java))
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
        }
    }
}