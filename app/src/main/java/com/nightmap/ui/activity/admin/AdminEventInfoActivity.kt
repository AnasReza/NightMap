package com.nightmap.ui.activity.admin

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nightmap.R

class AdminEventInfoActivity: AppCompatActivity() {
    private var bottomLayout: LinearLayout?=null

    private var newView:View?=null
    private var inflator: LayoutInflater?=null
    private var image: ImageView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_bar_event_info)
        init()
    }

    private fun init() {

        bottomLayout=findViewById(R.id.bottomLayout)

        inflator= LayoutInflater.from(this)
        for(x in 0..3){
            addBottomLayout()
        }
    }
    private fun addBottomLayout() {
        newView=inflator!!.inflate(R.layout.item_user_details_rate,null)
image=newView!!.findViewById(R.id.image1)
        Glide.with(this).asBitmap().load(R.drawable.club)
            .apply(RequestOptions.circleCropTransform())
            .into(image!!)
        bottomLayout!!.addView(newView)
    }
}