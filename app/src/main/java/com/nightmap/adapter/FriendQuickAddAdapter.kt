package com.nightmap.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.utility.Preferences

class FriendQuickAddAdapter(
    var activity: FragmentActivity,
    var doc: ArrayList<QueryDocumentSnapshot>
) : RecyclerView.Adapter<FriendQuickAddAdapter.FriendsQuickAddHolder>() {
    private var notifyHolder: FriendsQuickAddHolder? = null
    private var db: FirebaseFirestore? = null
    private var pref: Preferences? = null

    class FriendsQuickAddHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image1: ImageView = view.findViewById(R.id.image1)
        var messageText: TextView = view.findViewById(R.id.messageText)
        var tickLayout: RelativeLayout = view.findViewById(R.id.tickLayout)
        var cutLayout: RelativeLayout = view.findViewById(R.id.cutLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsQuickAddHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friends_request, parent, false)
        initial(itemView)
        notifyHolder = FriendsQuickAddHolder(itemView)
        return notifyHolder as FriendsQuickAddHolder
    }

    private fun initial(itemView: View?) {
        db = Firebase.firestore
        pref = Preferences(activity)
    }

    override fun getItemCount(): Int {
        return doc.size
    }

    override fun onBindViewHolder(holder: FriendsQuickAddHolder, position: Int) {
        val document = doc[position]
        db!!.collection("User").document(document.id).get()
            .addOnSuccessListener { documentSnapshot ->
                val imageURL = documentSnapshot.get("profileImage").toString()
                holder.messageText.text = documentSnapshot.get("fullName").toString()
                if (imageURL == "") {
                    Glide.with(activity).asBitmap().load(R.drawable.user_placeholder)
                        .apply(RequestOptions.circleCropTransform())
                        .into(holder.image1)
                } else {
                    Glide.with(activity).asBitmap().load(imageURL)
                        .apply(RequestOptions.circleCropTransform())
                        .into(holder.image1).onLoadFailed(
                            activity.resources.getDrawable(
                                R.drawable.user_placeholder,
                                null
                            )
                        )
                }
            }
        holder.tickLayout.setOnClickListener {
            val hashMap = hashMapOf("uid" to document.id, "status" to "pending")
            db!!.collection("User").document(pref!!.getUserID()!!).collection("FriendRequestSent")
                .document(document.id).set(hashMap).addOnSuccessListener { document ->
                    Toast.makeText(activity!!, "Friend Request Sent", Toast.LENGTH_SHORT).show()
                }

        }
    }


}