package com.nightmap.ui.fragment.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.adapter.FriendsListAdapter
import com.nightmap.utility.Preferences

class MyFriendsListFragment : Fragment() {
    private var friendsList: RecyclerView? = null
    private var messageText:TextView?=null
    private var mLayoutManager: LinearLayoutManager? = null
    private var adapter: FriendsListAdapter? = null
    private var db: FirebaseFirestore? = null
    private var pref: Preferences? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_my_friends, container, false)
        init(view)
        return view
    }

    private fun init(view: View?) {
        db = Firebase.firestore
        pref = Preferences(activity!!)

        friendsList = view!!.findViewById(R.id.myfriends_list)
        messageText=view!!.findViewById(R.id.messageText)
        mLayoutManager = LinearLayoutManager(activity!!.applicationContext)


        db!!.collection("User").document(pref!!.getUserID()!!).collection("Friends").get()
            .addOnSuccessListener { document ->
                val list: ArrayList<QueryDocumentSnapshot> = ArrayList<QueryDocumentSnapshot>()
                for (doc in document) {
                    list.add(doc)
                }

                if(list.size!=0){
                    adapter=FriendsListAdapter(activity!!,list)
                    friendsList!!.layoutManager = mLayoutManager
                            friendsList!!.adapter = adapter
                }else{
                    messageText!!.visibility=View.VISIBLE
                }
            }


    }
}