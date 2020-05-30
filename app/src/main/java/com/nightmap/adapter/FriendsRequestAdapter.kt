package com.nightmap.adapter

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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.utility.Preferences

class FriendsRequestAdapter(
    var activity: FragmentActivity,
    var list: ArrayList<QueryDocumentSnapshot>
) : RecyclerView.Adapter<FriendsRequestAdapter.FriendsRequestHolder>() {
    private var notifyHolder: FriendsRequestHolder? = null
    private var db: FirebaseFirestore? = null
    private var pref: Preferences? = null

    class FriendsRequestHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image1: ImageView = view.findViewById(R.id.image1)
        var messageText: TextView = view.findViewById(R.id.messageText)
        var tickLayout: LinearLayout = view.findViewById(R.id.tickLayout)
        var cutLayout: LinearLayout = view.findViewById(R.id.cutLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsRequestHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friends_request, parent, false)
        init(itemView)
        notifyHolder = FriendsRequestHolder(itemView)
        return notifyHolder as FriendsRequestHolder
    }

    fun init(itemView: View) {
        db = Firebase.firestore
        pref = Preferences(activity)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: FriendsRequestHolder, position: Int) {
        val document = list[position]
        val uid: String = document.get("uid").toString()
        db!!.collection("User").document(uid).get().addOnSuccessListener { documentSnapshot ->
            val url = documentSnapshot.get("profileImage").toString()
            holder.messageText.text = documentSnapshot.get("fullName").toString()
            if (url != "") {
                Glide.with(activity).asBitmap().load(url)
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.image1)
            } else {
                Glide.with(activity).asBitmap().load(R.drawable.user_placeholder)
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.image1)
            }

            holder.tickLayout.setOnClickListener {
                changeStatus("accepted", uid)

            }
            holder.cutLayout.setOnClickListener {
                changeStatus("cancel", uid)
            }
        }

    }

    private fun changeStatus(status: String, uid: String) {
        val hashMap = hashMapOf("status" to status)
        db!!.collection("User").document(pref!!.getUserID()!!).collection("FriendRequests")
            .document(uid).update(
                hashMap as Map<String, Any>
            )
    }


}