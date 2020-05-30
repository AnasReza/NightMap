package com.nightmap.ui.activity.admin

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nightmap.R
import com.nightmap.adapter.AdminNotificationAdapter
import com.nightmap.adapter.BarNotificationAdapter

class AdminSendNotificationActivity : AppCompatActivity(), View.OnClickListener {
    private var adapter:AdminNotificationAdapter?=null
    private var listView: RecyclerView?=null
    private var mLayoutManager: LinearLayoutManager? = null
    private var add_button: RelativeLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_send_notify)
        init()
    }

    private fun init() {
        adapter= AdminNotificationAdapter(this)
        listView=findViewById(R.id.notify_list)
        add_button=findViewById(R.id.add_button)

        mLayoutManager = LinearLayoutManager(this)
        listView!!.layoutManager = mLayoutManager
        listView!!.adapter = adapter

        add_button!!.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.add_button->{
                startActivity(Intent(this,AdminNewNotificationActivity::class.java))
            }
        }
    }
}