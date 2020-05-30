package com.nightmap.ui.activity.user

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.adapter.SeeRatedAdapter

class UserSeeRatedActivity : AppCompatActivity() {
    private var list: RecyclerView? = null
    private var adapter: SeeRatedAdapter? = null
    private var mLayout: GridLayoutManager? = null
    private var db:FirebaseFirestore?=null
    private var documentList: ArrayList<QueryDocumentSnapshot>?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_see_rated)
        init()
    }

    private fun init() {
        db= Firebase.firestore
        documentList= ArrayList<QueryDocumentSnapshot>()
        mLayout = GridLayoutManager(this, 2)

        list = findViewById(R.id.rated_list)
        db!!.collection("Bars").get().addOnSuccessListener { documents->
            for(document in documents){
               documentList!!.add(document)
            }
            adapter=SeeRatedAdapter(this,documentList)
            list!!.layoutManager=mLayout
            list!!.adapter=adapter
        }




    }
}