package com.nightmap.adapter

import android.content.Intent
import android.text.format.DateFormat
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
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.nightmap.R
import com.nightmap.ui.activity.LocationMapActivity
import com.nightmap.ui.activity.user.UserDiscountActivity
import com.nightmap.ui.activity.user.UserFreeDrinkActivity
import java.util.*

class UserNotificationAdapter(
    val activity: FragmentActivity,
    val list: ArrayList<QueryDocumentSnapshot>,
    val db: FirebaseFirestore,
    val spinKitView: SpinKitView
) : RecyclerView.Adapter<UserNotificationAdapter.UserNotificationHolder>() {
    private var notifyHolder: UserNotificationHolder? = null

    class UserNotificationHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image1: ImageView = view.findViewById(R.id.image1)
        var messageText: TextView = view.findViewById(R.id.messageText)
        var date: TextView = view.findViewById(R.id.date)
        var mainlayout: RelativeLayout = view.findViewById(R.id.mainLayout)
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
        spinKitView.visibility=View.GONE
        var collectionName = ""
        var itemName = ""
        val mainDocument = list[position]
        val docID = mainDocument.id

        val time: Timestamp = mainDocument.get("addedOn") as Timestamp
        val barId: String = mainDocument.get("barId").toString()
        val type: String = mainDocument.get("type").toString()
        val status:String=mainDocument.get("status").toString()
        holder.messageText.text = mainDocument.get("body").toString()

        val date: Date = time.toDate()
        var day: String = DateFormat.format("dd", date).toString()
        var month: String = DateFormat.format("MM", date).toString()
        var year: String = DateFormat.format("yyyy", date).toString()
        var dateString: String = "$day/$month/$year"

        holder.date.text = dateString

        if (type == "Discount" || type == "Free Drink") {
            collectionName = "Bars"
            itemName = "imagesURL"
        }else if(type=="Friend Request" || type=="View Location"){
            collectionName = "User"
            itemName = "profileImage"
        }

        db!!.collection(collectionName).document(barId).get().addOnSuccessListener { document ->
            if(collectionName=="Bars"){
                val url: ArrayList<String> = document.get(itemName) as ArrayList<String>
                val title: String = document.get("title").toString()
                val discount: String = document.get("discount").toString()
                if (activity != null) {
                    Glide.with(activity!!).asBitmap().load(url[0])
                        .apply(RequestOptions.circleCropTransform())
                        .into(holder.image1)
                    holder.mainlayout.setOnClickListener {
                        Log.d("Anas","$status  is pressed $docID")
                        if(status=="awarded"){
                            if (type == "Free Drink") {
                                activity!!.startActivity(
                                    Intent(
                                        activity!!,
                                        UserFreeDrinkActivity::class.java
                                    ).putExtra("bar_name", title).putExtra("url", url[0])
                                        .putExtra("notificationID", docID).putExtra("addedOn",date)
                                )
                            }
                            else if (type == "Discount") {
                                activity!!.startActivity(
                                    Intent(
                                        activity!!,
                                        UserDiscountActivity::class.java
                                    ).putExtra("bar_name", title).putExtra("discount", discount)
                                        .putExtra("url", url[0]).putExtra("notificationID", docID)
                                        .putExtra("addedOn",date)
                                )
                            }
                        }else{
                            if(status=="expired"){
                                Toast.makeText(activity,"Your $type is Expired",Toast.LENGTH_SHORT).show()
                            }else if (status=="availed"){
                                Toast.makeText(activity,"You have already Availed this $type",Toast.LENGTH_SHORT).show()
                            }
                        }

                    }
                }
            }

            else if(collectionName=="User"){
                val url: String = document.get(itemName).toString()
                val title: String = document.get("title").toString()
                val name:String=document.get("fullName").toString()
                val id:String=document.id
                if (activity != null) {
                    Glide.with(activity!!).asBitmap().load(url)
                        .apply(RequestOptions.circleCropTransform())
                        .into(holder.image1)
                    holder.mainlayout.setOnClickListener {
                        if (type == "View Location") {
                            activity!!.startActivity(
                                Intent(
                                    activity!!,
                                    LocationMapActivity::class.java
                                ).putExtra("id", id)
                                    .putExtra("dbType","User")
                                    .putExtra("columnName", itemName)
                                    .putExtra("location","currentLocation")
                                    .putExtra("heading","$name Location")
                            )
                        }
                    }
                }
            }

        }


    }
}