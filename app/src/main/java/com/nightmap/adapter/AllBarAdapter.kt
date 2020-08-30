package com.nightmap.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.nightmap.R
import com.nightmap.ui.activity.admin.AdminPendingActivty
import com.nightmap.ui.activity.bar_owner.BarEventInfoActivity
import com.nightmap.ui.activity.user.UserBarDetailsActivity
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class AllBarAdapter (
    val context: Activity,
    val list: ArrayList<QueryDocumentSnapshot>?,
    val type:String
) :
    RecyclerView.Adapter<AllBarAdapter.AllBarHolder>() {
    private var notifyHolder: AllBarHolder? = null
    private var downloadTask:BackgroundDownloadTask?=null

    class AllBarHolder(view: View) : RecyclerView.ViewHolder(view) {

        var layout: RelativeLayout = view.findViewById(R.id.mainLayout)
        var headline: TextView = view.findViewById(R.id.headline)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllBarHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_explore, parent, false)
        notifyHolder = AllBarHolder(itemView)
        return notifyHolder as AllBarHolder
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    fun cancelTask(){
        if(downloadTask!=null){
            if(downloadTask!!.status==AsyncTask.Status.RUNNING){
                downloadTask!!.cancel(true)
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

    override fun onBindViewHolder(holder: AllBarHolder, position: Int) {

        val document = list!![position]
        val id=document.id
        holder.headline.text = document.get("title").toString()
        var imageUrl: ArrayList<String> = document.get("imagesURL") as ArrayList<String>
//        SomeTask(imageUrl[0], holder.layout).execute()
        checkImage(imageUrl,holder.layout,id,"BarImages")
        var intent: Intent? =null
        if(type=="all_bar"){
            intent=Intent(
                context,
                UserBarDetailsActivity::class.java)
            intent!!.putExtra("id", document.id)
            intent!!.putExtra("userType","admin")
        }else if(type=="pending"){
            intent=Intent(
                context,
                AdminPendingActivty::class.java)
            intent!!.putExtra("id", document.id)
        }
        holder.layout.setOnClickListener {
            context.startActivity(intent)
        }

    }


    fun isTablet(context: Context): Boolean {
        return context.resources.configuration.screenLayout and
                Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    inner class SomeTask(val url: String, val layout: RelativeLayout) :
        AsyncTask<String, URL, Bitmap>() {

        override fun doInBackground(vararg params: String?): Bitmap {
            val bitmap = getBitmapFromURL(url)
            val scaledBitmap = bitmap?.let { Bitmap.createScaledBitmap(it, 739, 415, false) }
            return scaledBitmap!!
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            var dr: Drawable = BitmapDrawable(context!!.resources, result)

            layout!!.background = dr
        }
    }
    private fun checkImage(
        list: ArrayList<String>,
        layout: RelativeLayout,
        id: String,
        imageFolder: String
    ) {
        val myDir = File(context!!.filesDir, "$imageFolder/$id")
        if (myDir.exists()) {
            var imageFile = File(myDir, "image0.png")
            if (imageFile.exists()) {
                var b: Bitmap = BitmapFactory.decodeStream(FileInputStream(imageFile))
                var dr: Drawable = BitmapDrawable(context!!.resources, b)
                layout!!.background = dr
                for (x in 0 until list.size) {
                   downloadTask= BackgroundDownloadTask(list[x], x, layout!!, myDir)
                       downloadTask!!.execute()
                }
            } else {
                downloadTask= BackgroundDownloadTask(list[0], 0, layout!!, myDir)
                downloadTask!!.execute()
            }

        } else {
            myDir.mkdirs()
            Log.d("Anas", "${myDir.exists()} boolean of folder")
            for (x in 0 until list.size) {
                downloadTask= BackgroundDownloadTask(list[x], x, layout!!, myDir)
                downloadTask!!.execute()
            }
        }
    }
    inner class BackgroundDownloadTask(
        val url: String,
        val position: Int,
        val layout: RelativeLayout,
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

            val fos =
                FileOutputStream("${mainFile.absolutePath}${File.separator}image$position.png")

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
            Log.e("Anas", "Downloading and saving file is complete")
            if (context != null) {
                var dr: Drawable = BitmapDrawable(context!!.resources, result)

                if (position == 0) {
                    layout!!.background = dr
                }
            }

        }
    }
}