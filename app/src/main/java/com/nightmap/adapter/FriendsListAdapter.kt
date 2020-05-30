package com.nightmap.adapter

import android.content.Intent
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.ui.activity.user.UserDetailsActivity
import com.nightmap.utility.Preferences
import java.util.*
import kotlin.collections.ArrayList


class FriendsListAdapter(
    var activity: FragmentActivity,
    var list: ArrayList<QueryDocumentSnapshot>
) :
    RecyclerView.Adapter<FriendsListAdapter.FriendsListHolder>() {
    private var notifyHolder: FriendsListHolder? = null
    private var db: FirebaseFirestore? = null
    private var pref: Preferences? = null

    class FriendsListHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image1: ImageView = view.findViewById(R.id.image1)
        var messageText: TextView = view.findViewById(R.id.messageText)
        var date: TextView = view.findViewById(R.id.date)
        var item: LinearLayout = view.findViewById(R.id.item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsListHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friends_list, parent, false)
        notifyHolder = FriendsListHolder(itemView)
        initial(itemView)
        return notifyHolder as FriendsListHolder
    }

    private fun initial(itemView: View?) {
        db = Firebase.firestore
        pref = Preferences(activity)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: FriendsListHolder, position: Int) {
        val document = list[position]
        val uid: String = document.get("uid").toString()
        val status: String = document.get("status").toString()


        if (status == "accepted") {
            db!!.collection("User").document(uid).get()
                .addOnSuccessListener { documentSnapshot ->
                    val profileURl:String=documentSnapshot.get("profileImage").toString()
                    val timestamp: Timestamp = documentSnapshot.get("addedOn") as Timestamp
                    var date: Date = timestamp.toDate()
                    val day: String = DateFormat.format("dd", date).toString()
                    val month: String = DateFormat.format("MM", date).toString()
                    val year: String = DateFormat.format("yy", date).toString()

                    holder.messageText.text = documentSnapshot.get("fullName").toString()
                    holder.date.text = "joined on $day/$month/$year"

                    if(profileURl!=""){
                        Glide.with(activity).asBitmap()
                            .load(profileURl)
                            .apply(RequestOptions.circleCropTransform())
                            .into(holder.image1)

                    }else{
                        Glide.with(activity).asBitmap()
                            .load(R.drawable.user_placeholder)
                            .apply(RequestOptions.circleCropTransform())
                            .into(holder.image1)

                    }


                    holder.item.setOnClickListener {
                        activity.startActivity(
                            Intent(
                                activity.applicationContext,
                                UserDetailsActivity::class.java
                            ).putExtra("uid",uid)
                        )
                    }
                }
        }


    }
}