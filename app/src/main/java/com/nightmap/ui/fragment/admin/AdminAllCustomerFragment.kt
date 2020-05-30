package com.nightmap.ui.fragment.admin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.nightmap.R
import com.nightmap.ui.activity.admin.AdminEventInfoActivity

class AdminAllCustomerFragment: Fragment(), View.OnClickListener {
    private var card: CardView?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_admin_customer, container, false)
        init(view)
        return view
    }

    private fun init(view: View?) {
        card=view!!.findViewById(R.id.event1)
        card!!.setOnClickListener (this)

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.event1->{
                activity!!.startActivity(Intent(activity!!, AdminEventInfoActivity::class.java))
            }
        }
    }
}