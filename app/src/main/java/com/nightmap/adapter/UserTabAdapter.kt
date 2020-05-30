package com.nightmap.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.nightmap.ui.fragment.user.FriendsRequestFragment
import com.nightmap.ui.fragment.user.MyFriendsListFragment
import com.nightmap.ui.fragment.user.QuickAddFriendListFragment

class UserTabAdapter (fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
               MyFriendsListFragment()
            }
            1 -> QuickAddFriendListFragment()
            else -> {
                return FriendsRequestFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "My Friends"
            1 -> "Quick Add"
            else -> {
                return "Friend Request"
            }
        }
    }

}