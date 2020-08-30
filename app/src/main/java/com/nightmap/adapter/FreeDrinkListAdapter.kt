package com.nightmap.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.AsyncTask
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
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.utility.Preferences
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class FreeDrinkListAdapter (
    val activity: FragmentActivity,
    val list: ArrayList<QueryDocumentSnapshot>
) :
    RecyclerView.Adapter<FreeDrinkListAdapter.FreeDrinkListHolder>() {
    private var notifyHolder: FreeDrinkListHolder? = null
    private var db: FirebaseFirestore? = null
    private var userTask:BackgroundUserTask?=null

    class FreeDrinkListHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image1: ImageView = view.findViewById(R.id.image1)
        var messageText: TextView = view.findViewById(R.id.messageText)
        var date: TextView = view.findViewById(R.id.date)
        var item: LinearLayout = view.findViewById(R.id.item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FreeDrinkListHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friends_list, parent, false)
        db = Firebase.firestore
        notifyHolder = FreeDrinkListHolder(itemView)
        return notifyHolder as FreeDrinkListHolder
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    override fun onBindViewHolder(holder: FreeDrinkListHolder, position: Int) {
        val document = list[position]
        val uid: ArrayList<String> = document.get("uid") as ArrayList<String>
        val time: Timestamp = document.get("addedOn") as Timestamp
        val status:String = document.get("status").toString()
        val addedOnDate=time.toDate()
        val currentTime= Calendar.getInstance().time

        if(status!=""){
            when (status) {
                "expired" -> {
                    holder.date.text="Time Expired"
                    holder.date.setTextColor(Color.RED)
                }
                "availed" -> {
                    holder.date.text="Free Drink Used"
                    holder.date.setTextColor(Color.GREEN)
                }
                "awarded" -> {
                    var difference:Long=addedOnDate.time-currentTime.time
                    val secondsInMilli: Long = 1000
                    val minutesInMilli = secondsInMilli * 60
                    val elapsedMinutes=difference / minutesInMilli
                    val timeLeft=10-elapsedMinutes
                    if(timeLeft>0){
                        holder.date.text="Timer: $timeLeft min left"
                    }else{
                        holder.date.text="Time Expired"
                        holder.date.setTextColor(Color.RED)
                    }
                }
            }
        }

        if (uid.size>0) {
            db!!.collection("User").document(uid[0]).get().addOnSuccessListener { documentSnapshot ->
                val url: String = documentSnapshot.get("profileImage").toString()
                val name: String = documentSnapshot.get("fullName").toString()
                holder.messageText.text = name
                if (url != "") {
                    userImage(url,holder.image1,uid[0])

                } else {
                    Glide.with(activity!!).asBitmap().load(R.drawable.user_placeholder)
                        .apply(RequestOptions.circleCropTransform())
                        .into(holder.image1)
                }
            }
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
            if (activity != null) {
                Glide.with(activity!!).asBitmap().load(result)
                    .apply(RequestOptions.circleCropTransform())
                    .into(layout)
            }
        }
    }
}