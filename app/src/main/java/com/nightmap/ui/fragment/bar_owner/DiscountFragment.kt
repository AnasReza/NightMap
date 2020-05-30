package com.nightmap.ui.fragment.bar_owner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nightmap.R
import com.nightmap.adapter.DiscountListAdapter
import com.nightmap.adapter.FriendsListAdapter

class DiscountFragment : Fragment() {
    private var friendsList: RecyclerView? = null
    private var mLayoutManager: LinearLayoutManager? = null
    private var adapter: DiscountListAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_bar_discount, container, false)
        init(view)
        return view
    }

    private fun init(view: View?) {
        friendsList = view!!.findViewById(R.id.discount_list)
        mLayoutManager = LinearLayoutManager(activity!!.applicationContext)
        adapter = DiscountListAdapter(activity!!)

        friendsList!!.layoutManager = mLayoutManager
        friendsList!!.adapter = adapter
    }
}