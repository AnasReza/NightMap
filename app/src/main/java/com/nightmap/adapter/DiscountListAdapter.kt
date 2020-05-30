package com.nightmap.adapter

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nightmap.R
import com.nightmap.ui.activity.user.UserDetailsActivity

class DiscountListAdapter (var activity: FragmentActivity) :
    RecyclerView.Adapter<DiscountListAdapter.DiscountListHolder>() {
    private var notifyHolder: DiscountListHolder? = null

    class DiscountListHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image1: ImageView = view.findViewById(R.id.image1)
        var messageText: TextView = view.findViewById(R.id.messageText)
        var date: TextView = view.findViewById(R.id.date)
        var item: LinearLayout = view.findViewById(R.id.item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscountListHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friends_list, parent, false)
        notifyHolder = DiscountListHolder(itemView)
        return notifyHolder as DiscountListHolder
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun onBindViewHolder(holder: DiscountListHolder, position: Int) {
        Glide.with(activity).asBitmap().load(R.drawable.club)
            .apply(RequestOptions.circleCropTransform())
            .into(holder.image1)
        when (position) {
            0 -> {
                holder.date!!.text="Timer: 6 mins left"
            }
            1 -> {
                holder.date!!.text="Timer: 1 mins left"
            }
            2 -> {
                holder.date!!.text="Timer Expired"
                holder.date!!.setTextColor(Color.RED)
            }
        }

    }
}