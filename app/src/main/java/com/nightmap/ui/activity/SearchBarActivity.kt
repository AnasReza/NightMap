package com.nightmap.ui.activity

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.adapter.SeeRatedAdapter

class SearchBarActivity : AppCompatActivity() {
    private var list: RecyclerView? = null
    private var adapter: SeeRatedAdapter? = null
    private var mLayout: GridLayoutManager? = null
    private var db: FirebaseFirestore? = null
    private var documentList: ArrayList<QueryDocumentSnapshot>? = null
    private var searchBar: EditText? = null
    private var back_arrow: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_bar_layout)
        init()
    }

    private fun init() {
        db = Firebase.firestore
        documentList = ArrayList<QueryDocumentSnapshot>()
        mLayout = GridLayoutManager(this, 2)
        list = findViewById(R.id.list)
        searchBar = findViewById(R.id.search_bar)
        back_arrow = findViewById(R.id.back_arrow)

        searchBar!!.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                db!!.collection("Bars").get().addOnSuccessListener { documents ->
                    documentList!!.clear()
                    adapter=null
                    list!!.removeAllViewsInLayout()
                    for (doc in documents) {
                        val barName = doc.get("title").toString()
                        val barText = v!!.text.toString()
                        if (barName.contains(barText, true)) {
                            documentList!!.add(doc)
                        }
                        adapter=SeeRatedAdapter(this@SearchBarActivity, documentList, false)
                        list!!.layoutManager=mLayout
                        list!!.adapter=adapter
                    }
                }
            }
            false
        }

        back_arrow!!.setOnClickListener { onBackPressed() }

    }
}