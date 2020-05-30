package com.nightmap.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.nightmap.R

class BarNotificationAdapter : RecyclerView.Adapter<BarNotificationAdapter.BarNotificationHolder>() {
    private var notifyHolder: BarNotificationHolder? = null

    class BarNotificationHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarNotificationHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bar_notifcation, parent, false)
        notifyHolder = BarNotificationHolder(itemView)
        return notifyHolder as BarNotificationHolder
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: BarNotificationHolder, position: Int) {

    }
}