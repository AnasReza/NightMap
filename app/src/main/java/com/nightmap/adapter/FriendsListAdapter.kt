package com.nightmap.adapter

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.ui.activity.user.UserDetailsActivity
import com.nightmap.utility.Preferences
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class FriendsListAdapter(
    var activity: FragmentActivity,
    var list: ArrayList<QueryDocumentSnapshot>,
    val spinKitView: SpinKitView
) :
    RecyclerView.Adapter<FriendsListAdapter.FriendsListHolder>() {
    private var notifyHolder: FriendsListHolder? = null
    private var db: FirebaseFirestore? = null
    private var pref: Preferences? = null

    class FriendsListHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image1: ImageView = view.findViewById(R.id.image1)
        var messageText: TextView = view.findViewById(R.id.messageText)
        var date: TextView = view.findViewById(R.id.date)
        var item: LinearLayout = view.findViewById(R.id.item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsListHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friends_list, parent, false)
        notifyHolder = FriendsListHolder(itemView)
        initial(itemView)
        return notifyHolder as FriendsListHolder
    }

    private fun initial(itemView: View?) {
        db = Firebase.firestore
        pref = Preferences(activity)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: FriendsListHolder, position: Int) {
        spinKitView.visibility=View.GONE
        val document = list[position]
        val uid: String = document.get("uid").toString()
        val status: String = document.get("status").toString()

        db!!.collection("User").document(uid).get()
            .addOnSuccessListener { documentSnapshot ->
                val profileURl: String = documentSnapshot.get("profileImage").toString()
                val timestamp: Timestamp = documentSnapshot.get("addedOn") as Timestamp
                var date: Date = timestamp.toDate()
                val day: String = DateFormat.format("dd", date).toString()
                val month: String = DateFormat.format("MM", date).toString()
                val year: String = DateFormat.format("yy", date).toString()

                holder.messageText.text = documentSnapshot.get("fullName").toString()
                holder.date.text = "joined on $day/$month/$year"

                if (profileURl != "") {
                    userImage(
                        profileURl,
                        holder.image1, uid
                    )


                } else {
                    Glide.with(activity).asBitmap()
                        .load(R.drawable.user_placeholder)
                        .apply(RequestOptions.circleCropTransform())
                        .into(holder.image1)

                }

                holder.item.setOnClickListener {
                    activity.startActivity(
                        Intent(
                            activity.applicationContext,
                            UserDetailsActivity::class.java
                        ).putExtra("uid", uid)
                    )
                }
            }
    }

    private fun userImage(
        url: String,
        layout: ImageView,
        id: String
    ) {
        val myDir = File(activity.filesDir, "UserImage/$id")
        if (myDir.exists()) {
            var imageFile = File(myDir, "image0.png")
            if (imageFile.exists()) {
                var b: Bitmap = BitmapFactory.decodeStream(FileInputStream(imageFile))
                Glide.with(activity).asBitmap().load(b)
                    .apply(RequestOptions.circleCropTransform())
                    .into(layout)

                BackgroundUserTask(url, layout, myDir).execute()

            } else {
                BackgroundUserTask(url, layout, myDir).execute()
            }

        } else {
            myDir.mkdirs()

            BackgroundUserTask(url, layout, myDir).execute()

        }
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
            Glide.with(activity).asBitmap().load(result)
                .apply(RequestOptions.circleCropTransform())
                .into(layout)


        }
    }
}