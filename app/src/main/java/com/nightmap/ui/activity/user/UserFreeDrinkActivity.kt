package com.nightmap.ui.activity.user

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.utility.Preferences
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class UserFreeDrinkActivity : AppCompatActivity(), View.OnClickListener {
    private var headline: TextView? = null
    private var topLayout: RelativeLayout? = null
    private var back_button: ImageView? = null
    private var checkBox: CheckBox? = null
    private var confirm: Button? = null
    private var timerText: TextView? = null

    private var task: SomeTask? = null
    private var bar_name: String = ""
    private var url: String = ""
    private var db: FirebaseFirestore? = null
    private var pref: Preferences? = null
    private var check: Boolean = false
    private var id: String = ""
    private var timer: CountDownTimer? = null
    private var addedOn: Date? = null
    private var isActive: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_free_drink)
        init()
    }

    override fun onStart() {
        super.onStart()
        isActive = true
    }

    override fun onStop() {
        super.onStop()
        isActive = false
    }

    private fun init() {
        db = Firebase.firestore
        pref = Preferences(this)
        bar_name = intent.getStringExtra("bar_name")
        url = intent.getStringExtra("url")
        id = intent.getStringExtra("notificationID")
        addedOn = intent.getSerializableExtra("addedOn") as Date?

        headline = findViewById(R.id.headline)
        topLayout = findViewById(R.id.topLayout)
        back_button = findViewById(R.id.back_button)
        checkBox = findViewById(R.id.checkBox)
        confirm = findViewById(R.id.confirm_button)
        timerText = findViewById(R.id.timerText)

        headline!!.text = bar_name


        val current: Date = Calendar.getInstance().time
        var different: Long = current.time - addedOn!!.time
        val secondsInMilli: Long = 1000
        val minutesInMilli = secondsInMilli * 60
        val elapsedMinutes = different / minutesInMilli
        val timeLeft = 600000 - different
        //different = different % minutesInMilli
        Log.d("Anas", "$timeLeft  minutes")
        timer = object : CountDownTimer(timeLeft, 1000) {
            override fun onFinish() {
                val newHash = hashMapOf("status" to "expired")
                db!!.collection("Notifications").document(id)
                    .update(newHash as Map<String, Any>).addOnSuccessListener {
                        if (isActive) {
                            onBackPressed()
                            Toast.makeText(this@UserFreeDrinkActivity,"Your Free Drink is Expired",Toast.LENGTH_SHORT).show()
                        }
                    }
            }

            override fun onTick(millisUntilFinished: Long) {
                val secondsInMilli: Long = 1000
                val minutesInMilli = secondsInMilli * 60
                val elapsedMinutes = millisUntilFinished / minutesInMilli

                var seconds = (millisUntilFinished / 1000) % 60

                var text = "$elapsedMinutes:$seconds"
                if (isActive) {
                    timerText!!.text = text
                }

            }

        }
        timer!!.start()

        task = SomeTask(url)
        task!!.execute()
        checkBox!!.setOnCheckedChangeListener { buttonView, isChecked -> check = isChecked }
        back_button!!.setOnClickListener(this)
        confirm!!.setOnClickListener(this)
    }

    override fun onPause() {
        super.onPause()
        if (task != null) {
            if (task!!.status == AsyncTask.Status.RUNNING) {
                task!!.cancel(true)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.back_button -> {
                onBackPressed()
            }
            R.id.confirm_button -> {
                if (check) {
                    timer!!.cancel()
                    val newHash = hashMapOf("status" to "availed")
                    db!!.collection("Notifications").document(id)
                        .update(newHash as Map<String, Any>)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this@UserFreeDrinkActivity,
                                "You have availed your Free Drink.",
                                Toast.LENGTH_SHORT
                            ).show()
                            onBackPressed()
                        }
                }
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

    inner class SomeTask(val url: String) :
        AsyncTask<String, URL, Bitmap>() {

        override fun doInBackground(vararg params: String?): Bitmap {
            val bitmap = getBitmapFromURL(url)
            val scaledBitmap = bitmap?.let { Bitmap.createScaledBitmap(it, 739, 415, false) }
            return scaledBitmap!!
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            var dr: Drawable = BitmapDrawable(this@UserFreeDrinkActivity!!.resources, result)

            topLayout!!.background = dr
        }
    }

}