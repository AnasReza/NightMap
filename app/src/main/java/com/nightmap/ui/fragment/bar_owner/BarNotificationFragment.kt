package com.nightmap.ui.fragment.bar_owner

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.adapter.BarNotificationAdapter
import com.nightmap.utility.Preferences

class BarNotificationFragment : Fragment() {
    private var listView: RecyclerView? = null
    private var mLayoutManager: LinearLayoutManager? = null
    private var adapter: BarNotificationAdapter? = null
    private var db: FirebaseFirestore? = null
    private var pref: Preferences? = null
    private var list: ArrayList<QueryDocumentSnapshot> = ArrayList()
    private var check: Boolean = true
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_bar_notification, container, false)
        init(view)
        return view
    }

    override fun onPause() {
        super.onPause()
        check = false
        if (adapter != null) {
            adapter!!.cancelTask()
        }
    }

    private fun init(view: View?) {
        db = Firebase.firestore
        pref = Preferences(activity!!.applicationContext)
        listView = view!!.findViewById(R.id.notify_list)

        mLayoutManager = LinearLayoutManager(activity!!.applicationContext)
        if (check) {
            db!!.collection("Notifications").orderBy("addedOn", Query.Direction.DESCENDING)
                .get().addOnSuccessListener { documents ->
                    Log.e("Anas", "Query is working in Bar Notifications")
                    for (doc in documents) {
                        val barId = doc.get("barId")
                        if (barId == pref!!.getBarID()) {
                            list!!.add(doc)
                        }
                    }
                    adapter = BarNotificationAdapter(activity!!, list!!)

                    listView!!.layoutManager = mLayoutManager
                    listView!!.adapter = adapter
                }
        }else{
            adapter=null
            adapter = BarNotificationAdapter(activity!!, list!!)

            listView!!.layoutManager = mLayoutManager
            listView!!.adapter = adapter
            db!!.collection("Notifications").orderBy("addedOn", Query.Direction.DESCENDING)
                .get().addOnSuccessListener { documents ->
                    list!!.clear()
                    Log.e("Anas", "Query is working in Bar Notifications")
                    for (doc in documents) {
                        val barId = doc.get("barId")
                        if (barId == pref!!.getBarID()) {
                            list!!.add(doc)
                        }
                    }}
        }

    }
}