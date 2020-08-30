package com.nightmap.ui.fragment.user

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.github.ybq.android.spinkit.SpinKitView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.adapter.FriendQuickAddAdapter
import com.nightmap.adapter.UserTabAdapter
import com.nightmap.utility.Preferences

class UserFriendFragement : Fragment() {
    private var tabs_main: TabLayout? = null
    private var spinKitView: SpinKitView? = null
    private var viewPager_main: ViewPager? = null
    private var fragmentAdapter: UserTabAdapter? = null
    private var search_bar: EditText? = null
    private var search_list: RecyclerView? = null
    private var messageText: TextView? = null
    private var db: FirebaseFirestore? = null
    private var adapter: FriendQuickAddAdapter? = null
    private var pref: Preferences? = null
    private var mLayoutManager: LinearLayoutManager? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_user_friend, container, false)
        init(view)
        return view
    }

    private fun init(view: View?) {
        db = Firebase.firestore
        pref = Preferences(activity!!)
        mLayoutManager = LinearLayoutManager(activity!!.applicationContext)
        fragmentAdapter = UserTabAdapter(childFragmentManager)

        viewPager_main = view!!.findViewById(R.id.viewpager_main)
        tabs_main = view!!.findViewById(R.id.tabs_main)
        search_bar = view!!.findViewById(R.id.search_bar)
        search_list = view!!.findViewById(R.id.search_list)
        messageText = view!!.findViewById(R.id.messageText)
        spinKitView = view!!.findViewById(R.id.spin_kit)

        viewPager_main!!.adapter = fragmentAdapter
        tabs_main!!.setupWithViewPager(viewPager_main)

        search_bar!!.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewPager_main!!.visibility = View.GONE
                tabs_main!!.visibility = View.GONE
                val text = search_bar!!.text

                val docList: ArrayList<QueryDocumentSnapshot> = ArrayList<QueryDocumentSnapshot>()
                db!!.collection("User").document(pref!!.getUserID()!!).collection("QuickAdd").get()
                    .addOnSuccessListener { querySnapshot ->
                        var index: Int = 0;
                        for (doc in querySnapshot) {
                            index += 1
                            val id = doc.id
                            db!!.collection("User").document(id).get()
                                .addOnSuccessListener { documentSnapshot ->
                                    var fullName = documentSnapshot.get("fullName").toString()
                                    if (fullName.contains(text)) {
                                        Log.d("Anas", "$fullName   fullname")
                                        docList.add(doc)
                                        if (querySnapshot.size()==index) {

                                            search_list!!.visibility=View.VISIBLE
                                            adapter = FriendQuickAddAdapter(
                                                activity!!,
                                                docList,
                                                spinKitView!!
                                            )
                                            search_list!!.layoutManager = mLayoutManager
                                            search_list!!.adapter = adapter
                                        }

                                    }
                                }
                        }


                    }
            }

            false
        }

        search_bar!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isEmpty()) {
                    messageText!!.visibility = View.GONE
                    search_list!!.visibility = View.GONE
                    viewPager_main!!.visibility = View.VISIBLE
                    tabs_main!!.visibility = View.VISIBLE
                }
            }

        })
    }
}