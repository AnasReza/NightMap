package com.nightmap.ui.fragment.bar_owner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nightmap.R
import com.nightmap.adapter.BarNotificationAdapter

class BarNotificationFragment: Fragment() {
    private var listView: RecyclerView?=null
    private var mLayoutManager: LinearLayoutManager? = null
    private var adapter: BarNotificationAdapter?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_bar_notification, container, false)
        init(view)
        return view
    }

    private fun init(view: View?) {
        adapter=BarNotificationAdapter()
        listView=view!!.findViewById(R.id.notify_list)

        mLayoutManager = LinearLayoutManager(activity!!.applicationContext)
        listView!!.layoutManager = mLayoutManager
        listView!!.adapter = adapter
    }
}