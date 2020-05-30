package com.nightmap.ui.fragment.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.adapter.UserNotificationAdapter
import com.nightmap.utility.Preferences

class UserNotificationFragment : Fragment() {
    private var listView:RecyclerView?=null
    private var mLayoutManager: LinearLayoutManager? = null
    private var adapter:UserNotificationAdapter?=null
    private var db:FirebaseFirestore?=null
    private var pref:Preferences?=null
    private var list:ArrayList<QueryDocumentSnapshot>?=null
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
        db=Firebase.firestore
        list= ArrayList<QueryDocumentSnapshot>()
        pref= Preferences(activity!!.applicationContext)

        listView=view!!.findViewById(R.id.notify_list)

        db!!.collection("Notifications").get().addOnSuccessListener { documents->
            for(doc in documents){
                val uid=pref!!.getUserID()
                if(uid==doc.get("uid")){
                    list!!.add(doc)
                }
            }
            adapter=UserNotificationAdapter(activity!!, list!!,db!!)
            mLayoutManager = LinearLayoutManager(activity!!.applicationContext)
            listView!!.layoutManager = mLayoutManager
            listView!!.adapter = adapter
        }



    }


}