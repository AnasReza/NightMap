package com.nightmap.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.nightmap.R
import com.nightmap.ui.activity.user.UserBarDetailsActivity
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import kotlin.math.roundToInt


class SeeRatedAdapter(
    val context: Activity,
    val list: ArrayList<QueryDocumentSnapshot>?,
    val check: Boolean
) :
    RecyclerView.Adapter<SeeRatedAdapter.SeeRatedHolder>() {
    private var notifyHolder: SeeRatedHolder? = null

    class SeeRatedHolder(view: View) : RecyclerView.ViewHolder(view) {
        var atmosphereLayout: LinearLayout = view.findViewById(R.id.atmosphereLayout)
        var musicLayout: LinearLayout = view.findViewById(R.id.musicLayout)
        var genderLayout: LinearLayout = view.findViewById(R.id.genderLayout)
        var crowdLayout: LinearLayout = view.findViewById(R.id.crowdLayout)

        var atmosCircleLayout: RelativeLayout = view.findViewById(R.id.atmosCircleLay)
        var musicCircleLayout: RelativeLayout = view.findViewById(R.id.musicCircleLay)
        var genderCircleLayout: RelativeLayout = view.findViewById(R.id.genderCircleLay)
        var crowdCircleLayout: RelativeLayout = view.findViewById(R.id.crowdCircleLay)

        var atmosNumber: TextView = view.findViewById(R.id.atmosNumber)
        var musicNumber: TextView = view.findViewById(R.id.musicNumber)
        var genderImage: ImageView = view.findViewById(R.id.genderImage)
        var crowdImage: ImageView = view.findViewById(R.id.crowdImage)

        var atmosText: TextView = view.findViewById(R.id.atmosText)
        var musicText: TextView = view.findViewById(R.id.musicText)
        var genderText: TextView = view.findViewById(R.id.genderText)
        var crowdText: TextView = view.findViewById(R.id.crowdText)

        var card: CardView = view.findViewById(R.id.mainLayout)
        var layout: RelativeLayout = view.findViewById(R.id.layout)
        var bottomLayout: LinearLayout = view.findViewById(R.id.bottomLayout)
        var headline: TextView = view.findViewById(R.id.headline)
        var spinKitView: SpinKitView = view.findViewById(R.id.spin_kit)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeeRatedHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_see_rated, parent, false)
        notifyHolder = SeeRatedHolder(itemView)
        return notifyHolder as SeeRatedHolder
    }

    override fun getItemCount(): Int {
        return list!!.size
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

    override fun onBindViewHolder(holder: SeeRatedHolder, position: Int) {
        holder.spinKitView.visibility = View.VISIBLE

        val document = list!![position]
        holder.headline!!.text = document.get("title").toString()
        var imageUrl: ArrayList<String> = document.get("imagesURL") as ArrayList<String>
        checkImage(imageUrl, holder.layout, document.id, "BarImages", holder.spinKitView)
        if (check) {
            holder.bottomLayout.visibility = View.VISIBLE
            holder.atmosNumber!!.text =
                DecimalFormat("#.#").format(document.get("averageAtmosphere"))
            holder.musicNumber!!.text = DecimalFormat("#.#").format(document.get("averageMusic"))
            resizeViews(holder)
        }


        holder.card.setOnClickListener {
            context.startActivity(
                Intent(
                    context,
                    UserBarDetailsActivity::class.java
                ).putExtra("id", document.get("barId").toString()).putExtra("userType", "user")
            )
        }

    }

    private fun checkImage(
        list: ArrayList<String>,
        layout: RelativeLayout,
        id: String,
        imageFolder: String, spinKitView: SpinKitView
    ) {
        val myDir = File(context!!.filesDir, "$imageFolder/$id")
        if (myDir.exists()) {
            var imageFile = File(myDir, "image0.png")
            if (imageFile.exists()) {
                spinKitView.visibility = View.GONE
                var b: Bitmap = BitmapFactory.decodeStream(FileInputStream(imageFile))
                var dr: Drawable = BitmapDrawable(context!!.resources, b)
                layout!!.background = dr
                for (x in 0 until list.size) {
                    BackgroundDownloadTask(list[x], x, layout!!, myDir, spinKitView).execute()
                }
            } else {
                BackgroundDownloadTask(
                    list[0],
                    0,
                    layout,
                    myDir, spinKitView
                )
                    .execute()
            }

        } else {
            myDir.mkdirs()
            Log.d("Anas", "${myDir.exists()} boolean of folder")
            for (x in 0 until list.size) {
                BackgroundDownloadTask(
                    list[x],
                    x,
                    layout,
                    myDir, spinKitView
                )
                    .execute()
            }
        }
    }
var width:Int=0
    private fun resizeViews(holder: SeeRatedHolder) {
        val displayMetrics = DisplayMetrics()
        context.windowManager.defaultDisplay.getMetrics(displayMetrics)
        var height = displayMetrics.heightPixels
         width = displayMetrics.widthPixels
        var density = displayMetrics!!.densityDpi

        holder.crowdCircleLayout.layoutParams.width = (width * 0.08).roundToInt()
        holder.crowdCircleLayout.layoutParams.height = (width * 0.08).roundToInt()
        holder.crowdImage.layoutParams.width = (width * 0.5).roundToInt()
        holder.crowdImage.layoutParams.height = (width * 0.5).roundToInt()

        holder.atmosCircleLayout.layoutParams.width = (width * 0.08).roundToInt()
        holder.atmosCircleLayout.layoutParams.height = (width * 0.08).roundToInt()


        holder.musicCircleLayout.layoutParams.width = (width * 0.08).roundToInt()
        holder.musicCircleLayout.layoutParams.height = (width * 0.08).roundToInt()

        holder.genderCircleLayout.layoutParams.width = (width * 0.08).roundToInt()
        holder.genderCircleLayout.layoutParams.height = (width * 0.08).roundToInt()
        holder.genderImage.layoutParams.width = (width * 0.5).roundToInt()
        holder.genderImage.layoutParams.height = (width * 0.5).roundToInt()


        if (density == 320 || density < 420) {
            if (isTablet(context)) {
                holder.crowdText!!.textSize = (width * 0.007).toFloat()
                holder.atmosText!!.textSize = (width * 0.007).toFloat()
                holder.atmosNumber!!.textSize = ((width * 0.012).toFloat())
                holder.musicNumber!!.textSize = ((width * 0.012).toFloat())
                holder.musicText!!.textSize = ((width * 0.007).toFloat())
                holder.genderText!!.textSize = (width * 0.007).toFloat()
            } else {
                holder.crowdText!!.textSize = (width * 0.007).toFloat()
                holder.atmosText!!.textSize = (width * 0.007).toFloat()
                holder.atmosNumber!!.textSize = ((width * 0.015).toFloat())
                holder.musicNumber!!.textSize = ((width * 0.015).toFloat())
                holder.musicText!!.textSize = (width * 0.007).toFloat()
                holder.genderText!!.textSize = (width * 0.007).toFloat()
            }
        } else if (density == 420 || density < 560) {
            if (isTablet(context)) {
                holder.crowdText!!.textSize = (width * 0.005).toFloat()
                holder.atmosText!!.textSize = (width * 0.005).toFloat()
                holder.atmosNumber!!.textSize = ((width * 0.007).toFloat())
                holder.musicNumber!!.textSize = ((width * 0.007).toFloat())
                holder.musicText!!.textSize = ((width * 0.005).toFloat())
                holder.genderText!!.textSize = (width * 0.005).toFloat()
            } else {
                holder.crowdText!!.textSize = (width * 0.004).toFloat()
                holder.atmosText!!.textSize = (width * 0.004).toFloat()
                holder.atmosNumber!!.textSize = ((width * 0.007).toFloat())
                holder.musicNumber!!.textSize = ((width * 0.007).toFloat())
                holder.musicText!!.textSize = (width * 0.004).toFloat()
                holder.genderText!!.textSize = (width * 0.004).toFloat()
            }
        } else if (density == 560 || density < 640) {
            if (isTablet(context)) {
                holder.crowdText!!.textSize = (width * 0.003).toFloat()
                holder.atmosText!!.textSize = (width * 0.003).toFloat()
                holder.atmosNumber!!.textSize = ((width * 0.007).toFloat())
                holder.musicNumber!!.textSize = ((width * 0.007).toFloat())
                holder.musicText!!.textSize = ((width * 0.003).toFloat())
                holder.genderText!!.textSize = (width * 0.003).toFloat()
            } else {
                holder.crowdText!!.textSize = (width * 0.004).toFloat()
                holder.atmosText!!.textSize = (width * 0.004).toFloat()
                holder.atmosNumber!!.textSize = ((width * 0.007).toFloat())
                holder.musicNumber!!.textSize = ((width * 0.007).toFloat())
                holder.musicText!!.textSize = (width * 0.004).toFloat()
                holder.genderText!!.textSize = (width * 0.004).toFloat()
            }
        } else if (density == 640) {
            if (isTablet(context)) {

                holder.crowdText!!.textSize = (width * 0.001).toFloat()
                holder.atmosText!!.textSize = (width * 0.001).toFloat()
                holder.atmosNumber!!.textSize = ((width * 0.0007).toFloat())
                holder.musicNumber!!.textSize = ((width * 0.0007).toFloat())
                holder.musicText!!.textSize = ((width * 0.001).toFloat())
                holder.genderText!!.textSize = (width * 0.001).toFloat()
            } else {
                holder.crowdText!!.textSize = (width * 0.0035).toFloat()
                holder.atmosText!!.textSize = (width * 0.0035).toFloat()
                holder.atmosNumber!!.textSize = ((width * 0.007).toFloat())
                holder.musicNumber!!.textSize = ((width * 0.007).toFloat())
                holder.musicText!!.textSize = (width * 0.0035).toFloat()
                holder.genderText!!.textSize = (width * 0.0035).toFloat()
            }
        }
    }

    fun isTablet(context: Context): Boolean {
        return context.resources.configuration.screenLayout and
                Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
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
            var scaledBitmap: Bitmap? =null


            if (isOnline(context)) {
                var length = 0
                val bitmap = getBitmapFromURL(url)
                scaledBitmap = bitmap?.let { Bitmap.createScaledBitmap(it, 739, 415, false) }
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
            }else{


                val conf = Bitmap.Config.ARGB_8888 // see other conf types

                scaledBitmap = Bitmap.createBitmap(200, 200, conf) // this creates a MUTABLE bitmap

                val canvas = Canvas(scaledBitmap)
            }

            return scaledBitmap!!
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            Log.e("Anas", "Downloading and saving file is complete")
            if (context != null) {
                spinKitView.visibility = View.GONE
                var dr: Drawable = BitmapDrawable(context!!.resources, result)

                if (position == 0) {
                    layout!!.background = dr
                }
            }

        }
    }
}