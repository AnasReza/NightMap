package com.nightmap.ui.fragment.admin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nightmap.R
import com.nightmap.adapter.UserNotificationAdapter
import com.nightmap.ui.activity.admin.AdminSendNotificationActivity

class AdminNotificationFragment: Fragment(), View.OnClickListener {
    private var friendsList: RecyclerView? = null
    private var mLayoutManager: LinearLayoutManager? = null
    private var adapter: UserNotificationAdapter? = null
    private var sendButton: Button? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_admin_notification, container, false)
        init(view)
        return view
    }

    private fun init(view: View?) {
        friendsList = view!!.findViewById(R.id.notification_list)
        sendButton=view!!.findViewById(R.id.send_notification)
        mLayoutManager = LinearLayoutManager(activity!!.applicationContext)
       // adapter= UserNotificationAdapter(activity!!, list, db)

        friendsList!!.layoutManager = mLayoutManager
        friendsList!!.adapter = adapter

        sendButton!!.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.send_notification->{
                activity!!.startActivity(Intent(activity!!,AdminSendNotificationActivity::class.java))
            }
        }
    }
}