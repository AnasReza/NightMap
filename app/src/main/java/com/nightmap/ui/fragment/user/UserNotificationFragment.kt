package com.nightmap.ui.fragment.user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.adapter.UserNotificationAdapter
import com.nightmap.utility.Preferences

class UserNotificationFragment : Fragment() {
    private var listView: RecyclerView? = null
    private var spin_kit: SpinKitView? = null
    private var mLayoutManager: LinearLayoutManager? = null
    private var adapter: UserNotificationAdapter? = null
    private var db: FirebaseFirestore? = null
    private var pref: Preferences? = null
    private var list: ArrayList<QueryDocumentSnapshot>? = ArrayList<QueryDocumentSnapshot>()
    private var check:Boolean=true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_user_notification, container, false)
        init(view)
        return view
    }

    private fun init(view: View?) {
        db = Firebase.firestore
        pref = Preferences(activity!!.applicationContext)

        listView = view!!.findViewById(R.id.notify_list)
        spin_kit = view!!.findViewById(R.id.spin_kit)
if (check){
    db!!.collection("Notifications").orderBy("addedOn", Query.Direction.DESCENDING)
        .get()
        .addOnSuccessListener { documents ->
            for (doc in documents) {
                try {
                    Log.d("Anas", "${doc.id} doc id in notification")
                    val uid = pref!!.getUserID()
                    val uidList: ArrayList<String> = doc.get("uid") as ArrayList<String>
                    for (x in 0 until uidList.size) {
                        if (uid == uidList[x]) {
                            list!!.add(doc)
                        }
                    }
                } catch (e: ClassCastException) {

                }


            }
            adapter = UserNotificationAdapter(activity!!, list!!, db!!, spin_kit!!)
            mLayoutManager = LinearLayoutManager(activity!!.applicationContext)
            listView!!.layoutManager = mLayoutManager
            listView!!.adapter = adapter
        }
}else{
    adapter=null
    adapter = UserNotificationAdapter(activity!!, list!!, db!!, spin_kit!!)
    mLayoutManager = LinearLayoutManager(activity!!.applicationContext)
    listView!!.layoutManager = mLayoutManager
    listView!!.adapter = adapter
    db!!.collection("Notifications").orderBy("addedOn", Query.Direction.DESCENDING)
        .get()
        .addOnSuccessListener { documents ->
            list!!.clear()
            for (doc in documents) {

                try {
                    Log.d("Anas", "${doc.id} doc id in notification")
                    val uid = pref!!.getUserID()
                    val uidList: ArrayList<String> = doc.get("uid") as ArrayList<String>
                    for (x in 0 until uidList.size) {
                        if (uid == uidList[x]) {
                            list!!.add(doc)
                        }
                    }
                } catch (e: ClassCastException) {

                }
            }
        }

}

    }

    override fun onPause() {
        super.onPause()
        check=false

    }

}