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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.adapter.FriendQuickAddAdapter
import com.nightmap.utility.Preferences


class QuickAddFriendListFragment : Fragment(), View.OnClickListener {
    private var friendsList: RecyclerView? = null
    private var mLayoutManager: LinearLayoutManager? = null
    private var adapter: FriendQuickAddAdapter? = null
    private var sync_contact: TextView? = null
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
        mLayoutManager = LinearLayoutManager(activity!!.applicationContext)

        sync_contact = view.findViewById(R.id.sync_contact_text)

        sync_contact!!.setOnClickListener(this)

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
                adapter= FriendQuickAddAdapter(activity!!,docList)
                friendsList!!.layoutManager = mLayoutManager
                friendsList!!.adapter = adapter

            }
    }

    private fun getContactList() {
        val cr: ContentResolver = activity!!.contentResolver
        val cur: Cursor? = cr.query(
            ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null
        )
        if ((cur?.count ?: 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                val id: String = cur.getString(
                    cur.getColumnIndex(ContactsContract.Contacts._ID)
                )
                val name: String = cur.getString(
                    cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME
                    )
                )
                if (cur.getInt(
                        cur.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER
                        )
                    ) > 0
                ) {
                    val pCur: Cursor? = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )
                    while (pCur!!.moveToNext()) {
                        val phoneNo: String = pCur.getString(
                            pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER
                            )
                        )
                        Log.i("Anas", "Name: $name")
                        Log.i("Anas", "Phone Number: $phoneNo")
                        if (phoneNo.contains("+")) {
                            phoneList!!.add(phoneNo)
                        }
                    }

                    pCur.close()
                }
            }
        }
        cur?.close()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.sync_contact_text -> {
                getContactList()
                if (phoneList != null) {
                    for (x in 0 until phoneList!!.size) {
                        val newDB: FirebaseFirestore = Firebase.firestore
                        val hashMap =
                            hashMapOf("uid" to pref!!.getUserID(), "phoneNumber" to phoneList!![x])
                        newDB.collection("Contacts").add(hashMap)
                    }
                    addQuickAddList()
                }
            }
        }
    }
}