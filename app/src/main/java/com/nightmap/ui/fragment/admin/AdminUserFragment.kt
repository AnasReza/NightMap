package com.nightmap.ui.fragment.admin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.adapter.FriendsListAdapter

class AdminUserFragment : Fragment() {
    private var listView: RecyclerView? = null
    private var search_bar: EditText? = null
    private var spinKitView:SpinKitView?=null
    private var mLayoutManager: LinearLayoutManager? = null
    private var adapter: FriendsListAdapter? = null
    private var db: FirebaseFirestore? = null
    private var list: ArrayList<QueryDocumentSnapshot> = ArrayList()
    private var mainDocument: QuerySnapshot? = null
    private var check:Boolean=true
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_admin_users, container, false)
        init(view)
        return view
    }

    override fun onPause() {
        super.onPause()
check=false
    }

    private fun init(view: View?) {
        db = Firebase.firestore


        listView = view!!.findViewById(R.id.users_list)
        search_bar = view!!.findViewById(R.id.search_bar)
        spinKitView=view!!.findViewById(R.id.spin_kit)
        if(check){
            db!!.collection("User").get().addOnSuccessListener { documents ->
                mainDocument = documents
                for (doc in documents) {
                    list!!.add(doc)
                }

                adapter = FriendsListAdapter(activity!!, list!!,spinKitView!!)
                mLayoutManager = LinearLayoutManager(activity!!.applicationContext)
                listView!!.layoutManager = mLayoutManager
                listView!!.adapter = adapter

                search_bar!!.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {

                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {

                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        Log.e("Anas", "$s charater")
                        spinKitView!!.visibility=View.VISIBLE
                        val mainList = list
                        var tempList: ArrayList<QueryDocumentSnapshot> = ArrayList()
                        for (doc in mainDocument!!) {
                            if (s!!.isNotEmpty()) {
                                val name = doc.get("fullName").toString()
                                if (name.contains(s, true)) {
                                    tempList.add(doc)
                                }
                            } else {
                                tempList = list!!
                            }
                        }
                        adapter = null
                        listView!!.removeAllViewsInLayout()
                        adapter = FriendsListAdapter(activity!!, tempList!!,spinKitView!!)
                        mLayoutManager = LinearLayoutManager(activity!!.applicationContext)
                        listView!!.layoutManager = mLayoutManager
                        listView!!.adapter = adapter


                    }
                })

            }
        }else{
            adapter=null
            adapter = FriendsListAdapter(activity!!, list!!,spinKitView!!)
            mLayoutManager = LinearLayoutManager(activity!!.applicationContext)
            listView!!.layoutManager = mLayoutManager
            listView!!.adapter = adapter
            db!!.collection("User").get().addOnSuccessListener { documents ->
                mainDocument=null
                mainDocument = documents
                for (doc in documents) {
                    list!!.add(doc)
                }
            }
            search_bar!!.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    Log.e("Anas", "$s charater")
                    spinKitView!!.visibility=View.VISIBLE
                    val mainList = list
                    var tempList: ArrayList<QueryDocumentSnapshot> = ArrayList()
                    for (doc in mainDocument!!) {
                        if (s!!.isNotEmpty()) {
                            val name = doc.get("fullName").toString()
                            if (name.contains(s, true)) {
                                tempList.add(doc)
                            }
                        } else {
                            tempList = list!!
                        }
                    }
                    adapter = null
                    listView!!.removeAllViewsInLayout()
                    adapter = FriendsListAdapter(activity!!, tempList!!,spinKitView!!)
                    mLayoutManager = LinearLayoutManager(activity!!.applicationContext)
                    listView!!.layoutManager = mLayoutManager
                    listView!!.adapter = adapter


                }
            })
        }

    }

    override fun onResume() {
        super.onResume()
        search_bar!!.setText("")
    }
}