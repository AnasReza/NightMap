package com.nightmap.ui.fragment.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nightmap.R

class AdminHomeFragment : Fragment() {
    private var bottomLayout: LinearLayout? = null

    private var newView: View? = null
    private var inflator: LayoutInflater? = null
    private var image: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_admin_home_details, container, false)
        init(view)
        return view
    }

    private fun init(view: View?) {
        bottomLayout = view!!.findViewById(R.id.bottomLayout)

        inflator = LayoutInflater.from(activity!!)
        for (x in 0..3) {
            addBottomLayout()
        }
    }

    private fun addBottomLayout() {
        newView = inflator!!.inflate(R.layout.item_friends_list, null)
        image = newView!!.findViewById(R.id.image1)
        Glide.with(activity!!).asBitmap().load(R.drawable.club)
            .apply(RequestOptions.circleCropTransform())
            .into(image!!)
        bottomLayout!!.addView(newView)

    }
}