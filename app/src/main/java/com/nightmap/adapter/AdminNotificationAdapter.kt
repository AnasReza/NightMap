package com.nightmap.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.nightmap.R
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class AdminNotificationAdapter(
    val activity: FragmentActivity,
    val list: ArrayList<QueryDocumentSnapshot>,
    val db: FirebaseFirestore
) : RecyclerView.Adapter<AdminNotificationAdapter.AdminNotificationHolder>() {
    private var notifyHolder: AdminNotificationHolder? = null
    private var userTask: BackgroundUserTask?=null

    class AdminNotificationHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image1: ImageView = view.findViewById(R.id.image1)
        var messageText: TextView = view.findViewById(R.id.messageText)
        var date: TextView = view.findViewById(R.id.date)
        var tapText: TextView = view.findViewById(R.id.tapText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminNotificationHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_notifcation, parent, false)
        notifyHolder = AdminNotificationHolder(itemView)
        return notifyHolder as AdminNotificationHolder
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    override fun onBindViewHolder(holder: AdminNotificationHolder, position: Int) {
        val time: Timestamp = list[position].get("addedOn") as Timestamp
        val type: String = list[position].get("type").toString()
        holder.messageText.text = list[position].get("body").toString()

        val date: Date = time.toDate()
        var day: String = DateFormat.format("dd", date).toString()
        var month: String = DateFormat.format("MM", date).toString()
        var year: String = DateFormat.format("yyyy", date).toString()
        var dateString: String = "$day/$month/$year"

        holder.date.text = dateString
        if (type == "Free Drink"|| type=="Discount") {
            val uid:ArrayList<String> = list[position].get("uid") as ArrayList<String>
            db!!.collection("User").document(uid[0]).get().addOnSuccessListener { document ->
                val url:String= document.get("profileImage").toString()
                if(url!=""){
//                    Glide.with(activity).asBitmap().load(url)
//                        .apply(RequestOptions.circleCropTransform())
//                        .into(holder.image1)
                    userImage(url, holder.image1, document.id,"UserImage")

                }else{
                    Glide.with(activity).asBitmap().load(R.drawable.user_placeholder)
                        .apply(RequestOptions.circleCropTransform())
                        .into(holder.image1)
                }
            }
        }else if(type=="Bar Approval"){

            val barId: String = list[position].get("barId").toString()
            db!!.collection("Bars").document(barId).get().addOnSuccessListener { documentSnapshot ->
                var urls:ArrayList<String> = list[position].get("imagesURL") as ArrayList<String>
                userImage(urls[0], holder.image1, list[position].id,"BarImages")
//                Glide.with(activity).asBitmap().load(urls[0])
//                    .apply(RequestOptions.circleCropTransform())
//                    .into(holder.image1)
            }
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
        id: String,
        s: String
    ) {
        val myDir = File(activity!!.filesDir, "$s/$id")
        if (myDir.exists()) {
            var imageFile = File(myDir, "image0.png")
            if (imageFile.exists()) {
                try{
                    var b: Bitmap = BitmapFactory.decodeStream(FileInputStream(imageFile))
                    Glide.with(activity!!).asBitmap().load(b)
                        .apply(RequestOptions.circleCropTransform())
                        .into(layout)

                    userTask = BackgroundUserTask(url, layout, myDir)
                    userTask!!.execute()
                }catch (e:NullPointerException){
                    userTask = BackgroundUserTask(url, layout, myDir)
                    userTask!!.execute()
                }


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
            if (activity != null) {
                Glide.with(activity).asBitmap().load(result)
                    .apply(RequestOptions.circleCropTransform())
                    .into(layout)
            }
        }
    }

}