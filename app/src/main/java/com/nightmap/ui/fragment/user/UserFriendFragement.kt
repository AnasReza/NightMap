package com.nightmap.ui.fragment.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.nightmap.R
import com.nightmap.adapter.UserTabAdapter

class UserFriendFragement : Fragment() {
    private var tabs_main:TabLayout?=null
    private var viewPager_main:ViewPager?=null
    private var fragmentAdapter: UserTabAdapter?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_user_friend, container, false)
        init(view)
        return view
    }

    private fun init(view: View?) {
        fragmentAdapter=UserTabAdapter(childFragmentManager)
        viewPager_main=view!!.findViewById(R.id.viewpager_main)
        tabs_main=view!!.findViewById(R.id.tabs_main)

        viewPager_main!!.adapter=fragmentAdapter
        tabs_main!!.setupWithViewPager(viewPager_main)
    }
}