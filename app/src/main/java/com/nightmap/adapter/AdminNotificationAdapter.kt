package com.nightmap.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.nightmap.R

class AdminNotificationAdapter (var context: Context):
    RecyclerView.Adapter<AdminNotificationAdapter.BarNotificationHolder>() {
    private var notifyHolder: BarNotificationHolder? = null

    class BarNotificationHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView = view.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarNotificationHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bar_notifcation, parent, false)
        notifyHolder = BarNotificationHolder(itemView)
        return notifyHolder as BarNotificationHolder
    }

    override fun getItemCount(): Int {
        return 2
    }

    override fun onBindViewHolder(holder: BarNotificationHolder, position: Int) {
        holder.imageView.setBackgroundResource(R.drawable.ic_only_wine_glass)
    }
}