package com.nightmap.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.nightmap.ui.fragment.bar_owner.DiscountFragment
import com.nightmap.ui.fragment.bar_owner.FreeDrinkFragment
import com.nightmap.ui.fragment.user.FriendsRequestFragment
import com.nightmap.ui.fragment.user.MyFriendsListFragment
import com.nightmap.ui.fragment.user.QuickAddFriendListFragment

class BarTabAdapter (fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return return when (position) {
            0 -> {
                DiscountFragment()
            }
            else -> {
               FreeDrinkFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Discount"
            else -> {
                return "Free Drink"
            }
        }
    }

}