package com.nightmap.ui.fragment.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.adapter.AdminNotificationAdapter
import com.nightmap.ui.activity.admin.AdminSendNotificationActivity
import com.nightmap.utility.Preferences

class AdminNotificationFragment : Fragment(), View.OnClickListener {
    private var friendsList: RecyclerView? = null
    private var mLayoutManager: LinearLayoutManager? = null
    private var adapter: AdminNotificationAdapter? = null
    private var sendButton: Button? = null
    private var db: FirebaseFirestore? = null
    private var pref: Preferences? = null
    private var list: ArrayList<QueryDocumentSnapshot>? = ArrayList()
    private var check:Boolean=true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_admin_notification, container, false)
        init(view)
        return view
    }

    override fun onPause() {
        super.onPause()
        check=false
    }
    private fun init(view: View?) {
        db = Firebase.firestore
        pref = Preferences(activity!!)
        friendsList = view!!.findViewById(R.id.notification_list)
        sendButton = view!!.findViewById(R.id.send_notification)
        mLayoutManager = LinearLayoutManager(activity!!.applicationContext)

        if(check){
            db!!.collection("Notifications").orderBy("addedOn", Query.Direction.DESCENDING).get()
                .addOnSuccessListener { documents ->
                    for (doc in documents) {
                        Log.d("Anas","${doc.id}  id")
                        list!!.add(doc)
                    }
                    Log.e("Anas","${list!!.size} size")
                    adapter = AdminNotificationAdapter(activity!!, list!!, db!!)

                    friendsList!!.layoutManager = mLayoutManager
                    friendsList!!.adapter = adapter
                }

        }else{
            adapter=null
            adapter = AdminNotificationAdapter(activity!!, list!!, db!!)
            friendsList!!.layoutManager = mLayoutManager
            friendsList!!.adapter = adapter
            db!!.collection("Notifications").orderBy("addedOn", Query.Direction.DESCENDING).get()
                .addOnSuccessListener { documents ->
                    list!!.clear()
                    for (doc in documents) {
                        list!!.add(doc)
                    }
                }

        }


        sendButton!!.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.send_notification -> {
                activity!!.startActivity(
                    Intent(
                        activity!!,
                        AdminSendNotificationActivity::class.java
                    )
                )
            }
        }
    }
}