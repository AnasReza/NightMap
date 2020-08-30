package com.nightmap.ui.fragment.bar_owner

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.adapter.FreeDrinkListAdapter
import com.nightmap.utility.Preferences

class FreeDrinkFragment : Fragment() {
    private var friendsList: RecyclerView? = null
    private var mLayoutManager: LinearLayoutManager? = null
    private var adapter: FreeDrinkListAdapter? = null
    private var db: FirebaseFirestore? = null
    private var pref: Preferences? = null
    private var list: ArrayList<QueryDocumentSnapshot>? = null
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

    override fun onResume() {
        super.onResume()
        adapter = null
        friendsList!!.removeAllViewsInLayout()
        db!!.collection("Notifications")
            .orderBy("addedOn", Query.Direction.DESCENDING)
            .get().addOnSuccessListener { documents ->
                for (doc in documents) {
                    val barId = doc.get("barId").toString()
                    val status=doc.get("type").toString()
                    if (barId == pref!!.getBarID()&& status=="Free Drink") {
                        Log.e("Anas","${doc.id} == ${pref!!.getBarID()}")
                        list!!.add(doc)
                    }
                }
               adapter=FreeDrinkListAdapter(activity!!,list!!)

                friendsList!!.layoutManager = mLayoutManager
                friendsList!!.adapter = adapter
            }
    }
}