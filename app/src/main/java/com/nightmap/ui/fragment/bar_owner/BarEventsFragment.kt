package com.nightmap.ui.fragment.bar_owner


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.adapter.AllEventAdapter
import com.nightmap.ui.activity.bar_owner.BarAddNewEventActivity
import com.nightmap.ui.activity.bar_owner.BarEventInfoActivity
import com.nightmap.utility.Preferences
import kotlinx.android.synthetic.main.fragment_bar_events.*


class BarEventsFragment : Fragment(), View.OnClickListener {
    private var add_new_event: Button? = null
    private var all_event_list:RecyclerView?=null
    private var db: FirebaseFirestore?=null
    private var pref: Preferences?=null
    private var documentList: ArrayList<QueryDocumentSnapshot>?=null
    private var mLayout: GridLayoutManager? = null
    private var adapter:AllEventAdapter?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_bar_events, container, false)
        init(view)
        return view
    }

    private fun init(view: View?) {
        db= Firebase.firestore
        documentList= ArrayList<QueryDocumentSnapshot>()
        mLayout = GridLayoutManager(activity, 2)
        pref= Preferences(activity!!.applicationContext)

        add_new_event = view!!.findViewById(R.id.add_new_event)
        all_event_list = view!!.findViewById(R.id.all_event_list)


        add_new_event!!.setOnClickListener(this)

    }

    override fun onResume() {
        super.onResume()
        db!!.collection("Events").get().addOnSuccessListener { documents->

            for(doc in documents){
                val barId=doc.get("barId").toString()
                if(pref!!.getBarID()==barId){
                    documentList!!.add(doc)
                }
                adapter=null
                all_event_list!!.removeAllViewsInLayout()
                adapter= AllEventAdapter(activity!!,documentList)
                all_event_list!!.layoutManager=mLayout
                all_event_list!!.adapter=adapter
            }
        }

    }
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.add_new_event -> {
                activity!!.startActivity(Intent(activity!!, BarAddNewEventActivity::class.java))
            }
//            R.id.event1->{
//                activity!!.startActivity(Intent(activity!!, BarEventInfoActivity::class.java))
//            }
        }
    }

}