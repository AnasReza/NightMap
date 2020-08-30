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
import com.nightmap.adapter.DiscountListAdapter
import com.nightmap.utility.Preferences

class DiscountFragment : Fragment() {
    private var friendsList: RecyclerView? = null
    private var mLayoutManager: LinearLayoutManager? = null
    private var adapter: DiscountListAdapter? = null
    private var db: FirebaseFirestore? = null
    private var pref: Preferences? = null
    private var list: ArrayList<QueryDocumentSnapshot>? = null
    private var check: Boolean = true
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_bar_discount, container, false)
        init(view)
        return view
    }

    private fun init(view: View?) {
        db = Firebase.firestore
        pref = Preferences(activity!!)
        list = ArrayList()

        friendsList = view!!.findViewById(R.id.discount_list)
        mLayoutManager = LinearLayoutManager(activity!!.applicationContext)


    }

    override fun onPause() {
        super.onPause()
        check = false
        Log.e("Anas", "ONPAUSE FRoM DISCOUNT FRAGMENT")
        adapter!!.cancelTask()
    }

    override fun onResume() {
        super.onResume()
        Log.i("Anas", "$check check in Discount Fragment")
        adapter = null
        friendsList!!.removeAllViewsInLayout()
        db!!.collection("Notifications")
            .orderBy("addedOn", Query.Direction.DESCENDING)
            .get().addOnSuccessListener { documents ->
                for (doc in documents) {
                    val barId = doc.get("barId").toString()
                    val status = doc.get("type").toString()

                    if (barId == pref!!.getBarID() && status == "Discount") {
                        Log.e("Anas","$barId == ${pref!!.getBarID()}")
                       // Log.e("Anas", "${doc.get("uid").toString()} uid of notifications")
                        list!!.add(doc)
                    }
                }
                adapter = DiscountListAdapter(activity!!, list!!)

                friendsList!!.layoutManager = mLayoutManager
                friendsList!!.adapter = adapter
            }
    }
}