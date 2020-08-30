package com.nightmap.ui.fragment.admin

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.ui.activity.SearchBarActivity
import com.nightmap.ui.activity.user.UserDetailsActivity
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class AdminHomeFragment : Fragment(), View.OnClickListener {
    private var bottomLayout: LinearLayout? = null
    private var usersNumber: TextView? = null
    private var barNumber: TextView? = null
    private var eventsNumber: TextView? = null
    private var search_bar:EditText?=null

    private var newView: View? = null
    private var inflator: LayoutInflater? = null
    private var image: ImageView? = null
    private var db: FirebaseFirestore? = null
    private var userTask:BackgroundUserTask?=null
    private var check:Boolean=true
    private var bundle:Bundle= Bundle()
    private var usersList:ArrayList<QueryDocumentSnapshot> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_admin_home_details, container, false)
        init(view)
        return view
    }

    override fun onPause() {
        super.onPause()
        check=false
    }
    private fun init(view: View?) {
        db = Firebase.firestore
        inflator = LayoutInflater.from(activity!!)
        bottomLayout = view!!.findViewById(R.id.bottomLayout)
        barNumber = view!!.findViewById(R.id.barNumber)
        eventsNumber = view!!.findViewById(R.id.eventNumber)
        usersNumber = view!!.findViewById(R.id.usersNumber)
        search_bar=view!!.findViewById(R.id.search_bar)
if(check){
    db!!.collection("Bars").get().addOnSuccessListener { document ->

        barNumber!!.text = document.size().toString()
        bundle.putString("barNumber",document.size().toString())

    }
    db!!.collection("User").get().addOnSuccessListener { document ->

        usersNumber!!.text = document.size().toString()
        bundle.putString("usersNumber",document.size().toString())
    }
    db!!.collection("Events").get().addOnSuccessListener { document ->

        eventsNumber!!.text = document.size().toString()
        bundle.putString("eventsNumber",document.size().toString())
    }
    db!!.collection("User").orderBy("addedOn", Query.Direction.DESCENDING).get()
        .addOnSuccessListener { documents ->
            for (doc in documents) {
                addBottomLayout(doc)
                usersList.add(doc)
            }
        }
}else{
    barNumber!!.text=bundle.getString("barNumber")
    usersNumber!!.text=bundle.getString("usersNumber")
    eventsNumber!!.text = bundle.getString("eventsNumber")
    for(x in 0 until usersList.size){
        addBottomLayout(usersList[x])
    }

    db!!.collection("Bars").get().addOnSuccessListener { document ->


        bundle.putString("barNumber",document.size().toString())

    }
    db!!.collection("User").get().addOnSuccessListener { document ->


        bundle.putString("usersNumber",document.size().toString())
    }
    db!!.collection("Events").get().addOnSuccessListener { document ->


        bundle.putString("eventsNumber",document.size().toString())
    }
    db!!.collection("User").orderBy("addedOn", Query.Direction.DESCENDING).get()
        .addOnSuccessListener { documents ->
            usersList.clear()
            for (doc in documents) {

                usersList.add(doc)
            }
        }


}

        search_bar!!.setOnClickListener(this)
    }

    private fun addBottomLayout(doc: QueryDocumentSnapshot) {
        val id=doc.id
        val newView = inflator!!.inflate(R.layout.item_friends_list, null)
        val image: ImageView = newView!!.findViewById(R.id.image1)
        val name: TextView = newView!!.findViewById(R.id.messageText)
        val dateText: TextView = newView!!.findViewById(R.id.date)

        val url = doc.get("profileImage").toString()
        val timeStamp: Timestamp = doc.get("addedOn") as Timestamp
        val date: Date = timeStamp.toDate()
        val day: String = DateFormat.format("dd", date).toString()
        val month: String = DateFormat.format("MM", date).toString()
        val year: String = DateFormat.format("yy", date).toString()

        name.text = doc.get("fullName").toString()
        dateText.text = "joined on $day/$month/$year"
        if (url != "") {
            userImage(url,image,id)

        } else {
            Glide.with(activity!!).asBitmap().load(R.drawable.user_placeholder)
                .apply(RequestOptions.circleCropTransform())
                .into(image)
        }
        newView.setOnClickListener {
            activity!!.startActivity(
                Intent(
                    activity!!,
                    UserDetailsActivity::class.java
                ).putExtra("uid", doc.id)
            )
        }

        bottomLayout!!.addView(newView)

    }

    private fun getBitmapFromURL(s: String): Bitmap? {
        return try {
            val url = URL(s)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }

    }

    private fun userImage(
        url: String,
        layout: ImageView,
        id: String
    ) {
        val myDir = File(activity!!.filesDir, "UserImage/$id")
        if (myDir.exists()) {
            var imageFile = File(myDir, "image0.png")
            if (imageFile.exists()) {
                var b: Bitmap = BitmapFactory.decodeStream(FileInputStream(imageFile))
                Glide.with(activity!!).asBitmap().load(b)
                    .apply(RequestOptions.circleCropTransform())
                    .into(layout)

               userTask= BackgroundUserTask(url, layout, myDir)
                   userTask!!.execute()

            } else {
                userTask= BackgroundUserTask(url, layout, myDir)
                userTask!!.execute()
            }

        } else {
            myDir.mkdirs()

            userTask= BackgroundUserTask(url, layout, myDir)
            userTask!!.execute()

        }
    }

    inner class BackgroundUserTask(
        val url: String,
        val layout: ImageView,
        val mainFile: File
    ) :
        AsyncTask<String, URL, Bitmap>() {

        override fun doInBackground(vararg params: String?): Bitmap {
            var length = 0
            val bitmap = getBitmapFromURL(url)
            val scaledBitmap = bitmap?.let { Bitmap.createScaledBitmap(it, 739, 415, false) }
            val bos = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.PNG, 0, bos)
            val bitmapdata = bos.toByteArray()
            val bs = ByteArrayInputStream(bitmapdata)

            val fos = FileOutputStream("${mainFile.absolutePath}${File.separator}image0.png")

            while ({ length = bs.read(bitmapdata); length }() > 0) {
                fos.write(bitmapdata, 0, length)
            }
            fos.flush()
            fos.close()
            bs.close()
            return scaledBitmap!!
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            if (activity != null) {
                Glide.with(activity!!).asBitmap().load(result)
                    .apply(RequestOptions.circleCropTransform())
                    .into(layout)
            }
        }
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.search_bar->{
                activity!!.startActivity(Intent(activity!!,SearchBarActivity::class.java))
            }
        }
    }
}