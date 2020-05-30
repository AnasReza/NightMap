package com.nightmap.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.nightmap.R
import android.text.format.DateFormat
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class UserNotificationAdapter(
    val activity: FragmentActivity,
    val list: ArrayList<QueryDocumentSnapshot>,
    val db: FirebaseFirestore
) : RecyclerView.Adapter<UserNotificationAdapter.UserNotificationHolder>() {
    private var notifyHolder: UserNotificationHolder? = null

    class UserNotificationHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image1: ImageView = view.findViewById(R.id.image1)
        var messageText: TextView = view.findViewById(R.id.messageText)
        var date: TextView = view.findViewById(R.id.date)
        var tapText: TextView = view.findViewById(R.id.tapText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserNotificationHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_notifcation, parent, false)
        notifyHolder = UserNotificationHolder(itemView)
        return notifyHolder as UserNotificationHolder
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    override fun onBindViewHolder(holder: UserNotificationHolder, position: Int) {
        val time: Timestamp = list[position].get("addedOn") as Timestamp
        val barId: String = list[position].get("barId").toString()
        holder.messageText.text = list[position].get("body").toString()

        val date: Date = time.toDate()
        var day: String = DateFormat.format("dd", date).toString()
        var month: String = DateFormat.format("MM", date).toString()
        var year: String = DateFormat.format("yyyy", date).toString()
        var dateString: String = "$day/$month/$year"

        holder.date.text = dateString
        db!!.collection("Bars").document(barId).get().addOnSuccessListener { document ->
            val url:ArrayList<String> = document.get("imagesURL") as ArrayList<String>
            if(document.get("type")=="Free Drink"){
                Glide.with(activity).asBitmap().load(url[0])
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.image1)
            }

        }


    }
}