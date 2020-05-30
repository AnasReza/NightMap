package com.nightmap.ui.fragment.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nightmap.R
import com.nightmap.adapter.FriendsListAdapter

class AdminUserFragment : Fragment() {
    private var listView:RecyclerView?=null
    private var mLayoutManager: LinearLayoutManager? = null
    private var adapter: FriendsListAdapter?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_admin_users, container, false)
        init(view)
        return view
    }

    private fun init(view: View?) {
       // adapter=FriendsListAdapter(activity!!, list)
        listView=view!!.findViewById(R.id.users_list)

        mLayoutManager = LinearLayoutManager(activity!!.applicationContext)
        listView!!.layoutManager = mLayoutManager
        listView!!.adapter = adapter

    }
}