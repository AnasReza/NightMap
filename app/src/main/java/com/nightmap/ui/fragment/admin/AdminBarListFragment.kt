package com.nightmap.ui.fragment.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TabHost
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.nightmap.R
import com.nightmap.adapter.AdminTabAdapter
import com.nightmap.adapter.UserTabAdapter

class AdminBarListFragment : Fragment() {
    private var tabs_main:TabLayout?=null
    private var viewPager_main: ViewPager?=null
    private var fragmentAdapter: AdminTabAdapter?=null
    private var check:Boolean=true
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_admin_bar_list, container, false)
        init(view)
        return view
    }

    private fun init(view: View?) {

        fragmentAdapter=AdminTabAdapter(childFragmentManager)
        viewPager_main=view!!.findViewById(R.id.viewpager_main)
        tabs_main=view!!.findViewById(R.id.tabs_main)

        viewPager_main!!.adapter=fragmentAdapter
        tabs_main!!.setupWithViewPager(viewPager_main)

    }
}