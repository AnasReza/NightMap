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
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.nightmap.R
import com.nightmap.ui.activity.bar_owner.BarEventInfoActivity
import com.nightmap.ui.activity.user.UserBarDetailsActivity
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import kotlin.math.roundToInt

class AllEventAdapter(
    val context: Activity,
    val list: ArrayList<QueryDocumentSnapshot>?
) :
    RecyclerView.Adapter<AllEventAdapter.AllEventHolder>() {
    private var notifyHolder: AllEventHolder? = null

    class AllEventHolder(view: View) : RecyclerView.ViewHolder(view) {

        var layout: RelativeLayout = view.findViewById(R.id.layout)
        var headline: TextView = view.findViewById(R.id.headline)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllEventHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_explore, parent, false)
        notifyHolder = AllEventHolder(itemView)
        return notifyHolder as AllEventHolder
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

    override fun onBindViewHolder(holder: AllEventHolder, position: Int) {

        val document = list!![position]
        holder.headline.text = document.get("title").toString()
        var imageUrl: ArrayList<String> = document.get("imageUrl") as ArrayList<String>
        SomeTask(imageUrl[0], holder.layout).execute()


        holder.layout.setOnClickListener {
            context.startActivity(
                Intent(
                    context,
                    BarEventInfoActivity::class.java
                ).putExtra("id", document.get("eventId").toString())
            )
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

}