package com.nightmap.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nightmap.R

class DetailsRateAdapter(val context: Context) : RecyclerView.Adapter<DetailsRateAdapter.DetailsRateHolder>() {
    private var detailsRateHolder: DetailsRateHolder? = null

    class DetailsRateHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image1: ImageView = view.findViewById(R.id.image1)
//        var messageText: TextView = view.findViewById(R.id.messageText)
//        var date: TextView = view.findViewById(R.id.date)
//        var tapText: TextView = view.findViewById(R.id.tapText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsRateHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_details_rate, parent, false)
        detailsRateHolder = DetailsRateHolder(itemView)
        return detailsRateHolder as DetailsRateHolder
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun onBindViewHolder(holder: DetailsRateHolder, position: Int) {
        Glide.with(context).asBitmap().load(R.drawable.club)
            .apply(RequestOptions.circleCropTransform())
            .into(holder.image1)
    }
}