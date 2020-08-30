package com.nightmap.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class BarNotificationAdapter(val context: Context, val list: ArrayList<QueryDocumentSnapshot>) :
    RecyclerView.Adapter<BarNotificationAdapter.BarNotificationHolder>() {
    private var notifyHolder: BarNotificationHolder? = null
    private var db: FirebaseFirestore? = null
    private var userTask:BackgroundUserTask?=null

    class BarNotificationHolder(view: View) : RecyclerView.ViewHolder(view) {
        var message: TextView = view.findViewById(R.id.messageText)
        var dateText: TextView = view.findViewById(R.id.date)
        var imageView: ImageView = view.findViewById(R.id.image1)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarNotificationHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bar_notifcation, parent, false)
        db = Firebase.firestore
        notifyHolder = BarNotificationHolder(itemView)
        return notifyHolder as BarNotificationHolder
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    fun cancelTask(){
        if(userTask!=null){
            if(userTask!!.status==AsyncTask.Status.RUNNING){
                userTask!!.cancel(true)
            }
        }
    }
    override fun onBindViewHolder(holder: BarNotificationHolder, position: Int) {
        var document = list[position]
        var barId = document.get("barId").toString()
        var uid:ArrayList<String> = document.get("uid") as ArrayList<String>
        val type = document.get("type").toString()
        val time: Timestamp = list[position].get("addedOn") as Timestamp

        val date: Date = time.toDate()
        var day: String = DateFormat.format("dd", date).toString()
        var month: String = DateFormat.format("MM", date).toString()
        var year: String = DateFormat.format("yyyy", date).toString()
        var dateString: String = "$day/$month/$year"

        holder.dateText.text = dateString

        holder.message.text = document.get("body").toString()
        if (type == "Discount"||type == "Free Drink") {
            db!!.collection("User").document(uid[0]).get().addOnSuccessListener { documentSnapshot ->
                val url = documentSnapshot.get("profileImage").toString()
Log.d("Anas","$url")
                if (url != "") {
                    userImage(url, holder.imageView, uid[0])

                } else {
                    Glide.with(context).asBitmap().load(R.drawable.user_placeholder)
                        .apply(RequestOptions.circleCropTransform())
                        .into(holder.imageView)
                }
            }
        } else if (type == "Approved") {
            Glide.with(context).asBitmap().load(R.drawable.ic_notification_tick)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.imageView)
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

    private fun userImage(
        url: String,
        layout: ImageView,
        id: String
    ) {
        val myDir = File(context!!.filesDir, "UserImage/$id")
        if (myDir.exists()) {
            var imageFile = File(myDir, "image0.png")
            if (imageFile.exists()) {
                var b: Bitmap = BitmapFactory.decodeStream(FileInputStream(imageFile))
                Glide.with(context!!).asBitmap().load(b)
                    .apply(RequestOptions.circleCropTransform())
                    .into(layout)

                userTask = BackgroundUserTask(url, layout, myDir)
                userTask!!.execute()

            } else {
                userTask = BackgroundUserTask(url, layout, myDir)
                userTask!!.execute()
            }

        } else {
            myDir.mkdirs()

            userTask = BackgroundUserTask(url, layout, myDir)
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
            if (context != null) {
                Glide.with(context!!).asBitmap().load(result)
                    .apply(RequestOptions.circleCropTransform())
                    .into(layout)
            }
        }
    }

}