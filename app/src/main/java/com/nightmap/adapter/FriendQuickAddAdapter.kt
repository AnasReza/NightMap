package com.nightmap.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.utility.Preferences
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class FriendQuickAddAdapter(
    var activity: FragmentActivity,
    var doc: ArrayList<QueryDocumentSnapshot>,
    val spinKitView: SpinKitView
) : RecyclerView.Adapter<FriendQuickAddAdapter.FriendsQuickAddHolder>() {
    private var notifyHolder: FriendsQuickAddHolder? = null
    private var db: FirebaseFirestore? = null
    private var pref: Preferences? = null

    class FriendsQuickAddHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image1: ImageView = view.findViewById(R.id.image1)
        var messageText: TextView = view.findViewById(R.id.messageText)
        var tickLayout: RelativeLayout = view.findViewById(R.id.tickLayout)
        var cutLayout: RelativeLayout = view.findViewById(R.id.cutLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsQuickAddHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friends_request, parent, false)
        initial(itemView)
        notifyHolder = FriendsQuickAddHolder(itemView)
        return notifyHolder as FriendsQuickAddHolder
    }

    private fun initial(itemView: View?) {
        db = Firebase.firestore
        pref = Preferences(activity)
    }

    override fun getItemCount(): Int {
        return doc.size
    }

    override fun onBindViewHolder(holder: FriendsQuickAddHolder, position: Int) {
        spinKitView.visibility=View.GONE
        val mainDocument = doc[position]

        db!!.collection("User").document(mainDocument.id).get()
            .addOnSuccessListener { documentSnapshot ->
                val imageURL = documentSnapshot.get("profileImage").toString()
                holder.messageText.text = documentSnapshot.get("fullName").toString()
                if (imageURL == "") {
                    Glide.with(activity).asBitmap().load(R.drawable.user_placeholder)
                        .apply(RequestOptions.circleCropTransform())
                        .into(holder.image1)
                } else {
                    userImage(imageURL,holder.image1,mainDocument.id)
                }
            }
        holder.tickLayout.setOnClickListener {
            val hashMap = hashMapOf("uid" to mainDocument.id, "status" to "pending")
            db!!.collection("User").document(pref!!.getUserID()!!).collection("FriendRequestSent")
                .document(mainDocument.id).set(hashMap).addOnSuccessListener { document ->
//                    val notiHash =
//                        hashMapOf(
//                            "addedOn" to FieldValue.serverTimestamp(),
//                            "barId" to "",
//                            "body" to "${pref!!.getUserName()} just shared its location with you.",
//                            "status" to "",
//                            "title" to "Location Shared",
//                            "type" to "Location Shared",
//                            "uid" to mainDocument.id
//                        )
//                    db!!.collection("Notifications").add(notiHash)
                    Toast.makeText(activity!!, "Friend Request Sent", Toast.LENGTH_SHORT).show()
                    doc.removeAt(position)
                    notifyDataSetChanged()
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