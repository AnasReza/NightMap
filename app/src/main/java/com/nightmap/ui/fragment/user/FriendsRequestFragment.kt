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
import com.nightmap.adapter.FriendsRequestAdapter
import com.nightmap.utility.Preferences

class FriendsRequestFragment : Fragment() {
    private var friendsList: RecyclerView? = null
    private var messageText: TextView? = null
    private var mLayoutManager: LinearLayoutManager? = null
    private var adapter: FriendsRequestAdapter? = null
    private var db: FirebaseFirestore? = null
    private var pref: Preferences? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_friends_request, container, false)
        init(view)
        return view
    }

    private fun init(view: View?) {
        db = Firebase.firestore
        pref = Preferences(activity!!)

        friendsList = view!!.findViewById(R.id.myfriends_list)
        messageText = view!!.findViewById(R.id.messageText)
        mLayoutManager = LinearLayoutManager(activity!!.applicationContext)
        db!!.collection("User").document(pref!!.getUserID().toString()).collection("FriendRequests")
            .get().addOnSuccessListener { document ->
            val list: ArrayList<QueryDocumentSnapshot> = ArrayList<QueryDocumentSnapshot>()
            for (doc in document) {
                list.add(doc)
            }
                if(list.size!=0){
                    adapter = FriendsRequestAdapter(activity!!, list)

                    friendsList!!.layoutManager = mLayoutManager
                    friendsList!!.adapter = adapter
                }else{
                    messageText!!.visibility=View.VISIBLE
                }

        }


    }
}