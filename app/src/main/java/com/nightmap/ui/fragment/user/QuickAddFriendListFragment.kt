package com.nightmap.ui.fragment.user

import android.content.ContentResolver
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.adapter.FriendQuickAddAdapter
import com.nightmap.utility.Preferences


class QuickAddFriendListFragment : Fragment() {
    private var friendsList: RecyclerView? = null
    private var spin_kit:SpinKitView?=null

    private var mLayoutManager: LinearLayoutManager? = null
    private var adapter: FriendQuickAddAdapter? = null

    private var phoneList: ArrayList<String>? = null
    private var db: FirebaseFirestore? = null
    private var pref: Preferences? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_quick_add_friends, container, false)
        init(view)
        return view
    }

    private fun init(view: View?) {
        phoneList = ArrayList<String>()
        db = Firebase.firestore
        pref = Preferences(activity!!)
        friendsList = view!!.findViewById(R.id.myfriends_list)
        spin_kit=view!!.findViewById(R.id.spin_kit)
        mLayoutManager = LinearLayoutManager(activity!!.applicationContext)

        addQuickAddList()
    }

    private fun addQuickAddList() {
        val docList: ArrayList<QueryDocumentSnapshot> = ArrayList<QueryDocumentSnapshot>()
        db!!.collection("User").document(pref!!.getUserID()!!).collection("QuickAdd").get()
            .addOnSuccessListener { querySnapshot ->
                for (doc in querySnapshot) {
                    docList.add(doc)
                }
                friendsList!!.removeAllViewsInLayout()
                adapter=null
                adapter= FriendQuickAddAdapter(activity!!,docList,spin_kit!!)
                friendsList!!.layoutManager = mLayoutManager
                friendsList!!.adapter = adapter
                if(docList.size==0){
                    spin_kit!!.visibility=View.GONE
                }

            }
    }
}