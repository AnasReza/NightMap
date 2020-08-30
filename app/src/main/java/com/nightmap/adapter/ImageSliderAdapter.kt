package com.nightmap.adapter

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
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.utility.Preferences
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class ImageSliderAdapter(
    var activity: FragmentActivity,
    var list: ArrayList<String>,
    val documentID: String,
    val imageFolder: String,
    val spinKitView: SpinKitView
) : RecyclerView.Adapter<ImageSliderAdapter.ImageSliderHolder>() {
    private var notifyHolder: ImageSliderHolder? = null
    private var db: FirebaseFirestore? = null
    private var pref: Preferences? = null

    class ImageSliderHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image1: RelativeLayout = view.findViewById(R.id.image_layout)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageSliderHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_layout, parent, false)
        init(itemView)
        notifyHolder = ImageSliderHolder(itemView)
        return notifyHolder as ImageSliderHolder
    }

    fun init(itemView: View) {
        db = Firebase.firestore
        pref = Preferences(activity)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ImageSliderHolder, position: Int) {
        val url: String = list[position]
        val task = SomeTask(url, holder.image1)
        checkImage(url,holder.image1,documentID,imageFolder,position,spinKitView)
        //task!!.execute()

    }
    private fun checkImage(
        url: String,
        layout: RelativeLayout,
        id: String,
        imageFolder: String,
        position: Int,
        spinKitView: SpinKitView
    ) {
        val myDir = File(activity!!.filesDir, "$imageFolder/$id")
        if (myDir.exists()) {
            var imageFile = File(myDir, "image$position.png")
            if (imageFile.exists()) {
                var b: Bitmap = BitmapFactory.decodeStream(FileInputStream(imageFile))
                val scaledBitmap = b?.let { Bitmap.createScaledBitmap(b ,100, 100, false) }
//                val d: Drawable = Drawable.createFromPath(imageFile.absolutePath)!!
                var dr: Drawable = BitmapDrawable(activity!!.resources, scaledBitmap)
                //var dr: Drawable = BitmapDrawable(activity!!.resources, b)

                layout!!.background = dr


                    BackgroundDownloadTask(url, position, layout, myDir,spinKitView).execute()

            } else {
                BackgroundDownloadTask(url, 0, layout, myDir,spinKitView).execute()
            }

        } else {
            myDir.mkdirs()
            Log.d("Anas","${myDir.exists()} boolean of folder")

                BackgroundDownloadTask(url, position, layout, myDir,spinKitView).execute()
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

    inner class SomeTask(val url: String, val layout: RelativeLayout) :
        AsyncTask<String, URL, Bitmap>() {

        override fun doInBackground(vararg params: String?): Bitmap {
            val bitmap = getBitmapFromURL(url)
            val scaledBitmap = bitmap?.let { Bitmap.createScaledBitmap(it, 739, 415, false) }
            return scaledBitmap!!
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)

            var dr: Drawable = BitmapDrawable(activity!!.resources, result)

            layout!!.background = dr
        }

    }


    inner class BackgroundDownloadTask(
        val url: String,
        val position: Int,
        val layout: RelativeLayout,
        val mainFile: File,
        val spinKitView: SpinKitView
    ) :
        AsyncTask<String, URL, Bitmap>() {

        override fun doInBackground(vararg params: String?): Bitmap {
            var length=0
            val bitmap = getBitmapFromURL(url)
            val scaledBitmap = bitmap?.let { Bitmap.createScaledBitmap(it, 739, 415, false) }
            val bos= ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.PNG,0,bos)
            val bitmapdata=bos.toByteArray()
            val bs= ByteArrayInputStream(bitmapdata)

            val fos= FileOutputStream("${mainFile.absolutePath}${File.separator}image$position.png")

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
            Log.e("Anas","Downloading and saving file is complete")
            if (activity != null) {
                spinKitView.visibility=View.GONE
                var dr: Drawable = BitmapDrawable(activity!!.resources, result)

                if (position == 0) {
                    layout!!.background = dr
                }
            }

        }
    }
}