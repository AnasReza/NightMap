package com.nightmap.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.nightmap.ui.fragment.admin.AdminAllCustomerFragment
import com.nightmap.ui.fragment.admin.AdminPendingCustomerFragment
import com.nightmap.ui.fragment.admin.AdminRejectedCustomerFragment
import com.nightmap.ui.fragment.user.FriendsRequestFragment
import com.nightmap.ui.fragment.user.MyFriendsListFragment
import com.nightmap.ui.fragment.user.QuickAddFriendListFragment

class AdminTabAdapter (fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> AdminAllCustomerFragment()

            1 -> AdminPendingCustomerFragment()
            else -> {
                return AdminRejectedCustomerFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "All"
            1 -> "Pending"
            else -> {
                return "Rejected"
            }
        }
    }

}