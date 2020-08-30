package com.nightmap.ui.fragment.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.adapter.AllBarAdapter
import com.nightmap.ui.activity.admin.AdminEventInfoActivity
import com.nightmap.ui.activity.admin.AdminPendingActivty
import com.nightmap.utility.Preferences

class AdminPendingCustomerFragment: Fragment() {
    private var barList: RecyclerView?=null
    private var messageText: TextView?=null

    private var db: FirebaseFirestore?=null
    private var pref: Preferences?=null
    private var documentList: ArrayList<QueryDocumentSnapshot>?=null
    private var mLayout: GridLayoutManager? = null
    private var adapter: AllBarAdapter?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_admin_customer, container, false)
        init(view)
        return view
    }

    override fun onResume() {
        super.onResume()
        Log.e("Anas","OnResume Admin Pending Customer Fragment")
        documentList!!.clear()
        barList?.adapter?.notifyDataSetChanged()

        db!!.collection("Bars").get().addOnSuccessListener { documents->
            for (doc in documents){
                if(doc.get("status")=="pending"){
                    documentList!!.add(doc)
                }
            }
            if(documentList!!.size==0){
                messageText!!.visibility=View.VISIBLE
                messageText!!.text="There Is No Pending Bars"
            }else{
                messageText!!.visibility=View.GONE
                adapter= AllBarAdapter(activity!!,documentList,"pending")
                barList!!.layoutManager=mLayout
                barList!!.adapter=adapter
            }

        }
    }
    override fun onPause() {
        super.onPause()
        if(adapter!=null){
            adapter!!.cancelTask()
        }
    }
    private fun init(view: View?) {
        db= Firebase.firestore
        pref= Preferences(activity!!)
        documentList=java.util.ArrayList<QueryDocumentSnapshot>()
        barList=view!!.findViewById(R.id.all_bar_list)
        messageText=view!!.findViewById(R.id.message)
        mLayout = GridLayoutManager(activity, 2)

    }


}