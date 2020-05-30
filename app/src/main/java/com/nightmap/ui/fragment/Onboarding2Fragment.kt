package com.nightmap.ui.fragment

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.nightmap.R
import kotlin.math.roundToInt

class Onboarding2Fragment: Fragment() {
    private var centerImage: ImageView? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_second_onboard, container, false)
        init(view)
        return view
    }

    private fun init(view: View?) {
        val displayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        centerImage=view!!.findViewById(R.id.centerImage)
        centerImage!!.layoutParams.width= (width*0.25).roundToInt()
        centerImage!!.layoutParams.height= (width*0.4).roundToInt()

    }
}